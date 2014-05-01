package com.et.jbdbcdemoapp.jbdbc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jzehner on 4/29/14.
 */
public class ChatFragment extends Fragment {

    private MsgThread msgThread;
    private ArrayList<SlackMessage> msgs = new ArrayList<SlackMessage>();
    private MessageListAdapter adapter;
    private static ArrayList<SlackUser> userList;
    private static SlackController slackController;


    public static ChatFragment newInstance(SlackController slackController){
        ChatFragment fragment = new ChatFragment(slackController);
        userList = slackController.getUsers();
        return fragment;
    }

    public ChatFragment(SlackController slackController){
        this.slackController = slackController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.chat_layout, container, false);

        Button sendButton = (Button)rootView.findViewById(R.id.sendButton);
        final ListView listView = (ListView)rootView.findViewById(R.id.listView);
        final EditText sendText = (EditText)rootView.findViewById(R.id.editText);

        adapter = new MessageListAdapter(getActivity(), R.layout.message_list_view_item, msgs);

        adapter.userKey = slackController.myUser.slackName;

        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendText.getText().toString() != null){
                    //textList.add(sendText.getText().toString());
                    slackController.sendMessage(sendText.getText().toString(), slackController.myUser.slackName);
                    sendText.setText(null);
                }
            }
        });

        listView.setAdapter(adapter);

        this.msgThread = new MsgThread(getActivity().getBaseContext());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle){
        super.onSaveInstanceState(outBundle);

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.i("JBDBC", "Destroying the chat view");
        this.msgThread.requestStop();
    }

    public void loadChat(){
        ArrayList<SlackMessage> slackMessages = slackController.getNewMessages(msgs);
        if(slackMessages.size() > 0){
            msgs.addAll(slackMessages);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }

            });
        }

    }

    public class MsgThread extends Thread{
        private volatile boolean stopPolling = false;

        public MsgThread(Context context){
            super();
            start();
        }
        public void run(){
            while(!stopPolling){
                try{
                    loadChat();
                    Thread.sleep(2000);
                }
                catch (Exception ex){
                    Log.e("JBDBC",ex.toString());
                }
            }
        }
        public void requestStop(){
            stopPolling = true;
        }

    }

}
