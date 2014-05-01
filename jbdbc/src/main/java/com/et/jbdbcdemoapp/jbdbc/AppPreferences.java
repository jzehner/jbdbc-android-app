package com.et.jbdbcdemoapp.jbdbc;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by jzehner on 4/29/14.
 */
public class AppPreferences extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment());
    }


    public static class MyPreferenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
