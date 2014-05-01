package com.et.jbdbcdemoapp.jbdbc;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.util.Collection;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, IBeaconConsumer {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final SlackController slackController = new SlackController();
    protected static final String TAG = "JBDBC";
    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
    private IBeacon triggerBeacon = new IBeacon("D57092AC-DFAA-446C-8EF3-C81AA22815B5",5,5000);
    private boolean iBeaconHit = false;
    private int iBeaconCurrentProximity = IBeacon.PROXIMITY_UNKNOWN;
    private Region myRegion = new Region("myRangingUniqueId", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section3),
                        }),
                this);

        iBeaconManager.bind(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent in = new Intent(this,AppPreferences.class);
            this.startActivity(in);
        }
        else if(id == R.id.action_beacon){
            startIBeaconSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        Fragment fragment = PlaceholderFragment.newInstance(position + 1);
        switch (position){
            case 0:
                fragment = HomeFragment.newInstance(slackController);
                break;
            case 1:
                fragment = ChatFragment.newInstance(slackController);
                break;
            default:
                break;

        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        iBeaconManager.unBind(this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }


    }

    @Override
    public void onIBeaconServiceConnect() {


        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if(iBeacons.size() > 0){
                    for(IBeacon beacon: iBeacons ){
                        if(beacon.getProximityUuid().toLowerCase().equals(triggerBeacon.getProximityUuid().toLowerCase())
                                && beacon.getMajor() == triggerBeacon.getMajor()
                                && beacon.getMinor() == triggerBeacon.getMinor()){
                            if(beacon.getProximity() < IBeacon.PROXIMITY_NEAR && !iBeaconHit){
                                iBeaconHit = true;
                                stopIBeaconSearch();
                                new HttpTools.SendPostRequest().execute(
                                        "https://shielded-fortress-9160.herokuapp.com/fireEvent/beaconEntry",
                                        "{\"email\": \"nkirkes@exacttarget.com\",\"slackId\": \"100\", \"honorsMember\": \"true\"}"
                                );
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "iBeacon hit", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

    }

    public void startIBeaconSearch(){
        try {
            iBeaconManager.startRangingBeaconsInRegion(myRegion);
        }
        catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void stopIBeaconSearch(){
        try{
            iBeaconManager.stopRangingBeaconsInRegion(myRegion);
            iBeaconHit = false;
        }
        catch (RemoteException e){
            Log.e(TAG, e.toString());
        }
    }


}
