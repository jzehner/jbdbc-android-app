package com.et.jbdbcdemoapp.jbdbc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jzehner on 4/29/14.
 */
public class HomeFragment extends Fragment {
    public static SlackController slackController;

    public static HomeFragment newInstance(SlackController slackController){
        HomeFragment fragment = new HomeFragment(slackController);
        return fragment;
    }

    public HomeFragment(SlackController slackController){
        this.slackController = slackController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.home_layout, container, false);
        return rootView;
    }


}
