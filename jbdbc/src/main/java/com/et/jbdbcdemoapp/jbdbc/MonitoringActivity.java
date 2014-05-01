package com.et.jbdbcdemoapp.jbdbc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.Region;

/**
 * Created by jzehner on 4/29/14.
 */
public class MonitoringActivity extends Activity implements IBeaconConsumer{
    protected static final String TAG = "JBDBC";
    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
    private IBeacon triggerBeacon = new IBeacon("D57092AC-DFAA-446C-8EF3-C81AA22815B5",5,5000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        iBeaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }



    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an iBeacon for the first time!");
                if(region.getProximityUuid().toLowerCase().equals(triggerBeacon.getProximityUuid().toLowerCase())
                        && region.getMajor().equals(triggerBeacon.getMajor())
                        && region.getMinor().equals(triggerBeacon.getMinor())){
                    new HttpTools.SendPostRequest().execute(
                            "https://shielded-fortress-9160.herokuapp.com/fireEvent/beaconEntry",
                            "{\"email\": \"nkirkes@exacttarget.com\",\"slackId\": \"100\", \"honorsMember\": \"false\"}");
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an iBeacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing iBeacons: " + state);
            }
        });

        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("myId", "D57092AC-DFAA-446C-8EF3-C81AA22815B5", 5, 5000));
        } catch (RemoteException e) {   }
    }



}
