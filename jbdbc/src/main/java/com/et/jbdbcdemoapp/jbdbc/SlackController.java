package com.et.jbdbcdemoapp.jbdbc;

import android.app.ExpandableListActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by jzehner on 4/29/14.
 */
public class SlackController {
    protected static final String TAG = "JBDBC";
    private static final String token = "xoxp-2318464914-2317693959-2318631470-8e744c";
    private String myUserName = "jzehner";
    private Exception exeption;
    public SlackUser myUser;
    public ArrayList<SlackUser> slackUsers;
    public String channel = "C029CDNT4"; //#general
    //public String channel = "C029CDNT8"; //#random

    public SlackController(){
        Log.i(TAG,"Getting Users list");
        slackUsers = getUsers();
    }

    public void sendMessage(String text, String username){
        try{
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority("slack.com")
                    .path("api/chat.postMessage")
                    .appendQueryParameter("token", token)
                    .appendQueryParameter("channel", channel)
                    .appendQueryParameter("text", text)
                    .appendQueryParameter("username", username)
                    .build();

            new HttpTools.SendGetRequest().execute(uri.toString());


        }
        catch (Exception ex){
            Log.e("jbdbc",ex.toString());
        }
    }

    public ArrayList<SlackMessage> getNewMessages(ArrayList<SlackMessage> oldMsgs){
        ArrayList<SlackMessage> returnList = new ArrayList<SlackMessage>();
        try{
            ArrayList<SlackMessage> messageList = new ArrayList<SlackMessage>();
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority("slack.com")
                    .path("api/channels.history")
                    .appendQueryParameter("token", token)
                    .appendQueryParameter("channel",channel)
                    .build();

            String data = new HttpTools.SendGetRequest().execute(uri.toString()).get();
            messageList = new ParseMessages().execute(data).get();

            Collections.sort(messageList, SlackMessage.COMPARE_BY_EPOCH);
            for(Iterator<SlackMessage> i = messageList.iterator(); i.hasNext(); ){
                SlackMessage msgToCheck = i.next();
                boolean msgFound = false;
                for(Iterator<SlackMessage> j = oldMsgs.iterator(); j.hasNext(); ){
                    SlackMessage oldMsg = j.next();

                    if(msgToCheck.equalsMessage(oldMsg)){
                        msgFound = true;
                        break;
                    }
                }
                if(!msgFound){
                    if(msgToCheck.user != null && msgToCheck.username == null){
                        for(SlackUser su: slackUsers){
                            if(msgToCheck.user.equals(su.slackId) && su.slackName != null){
                                msgToCheck.username = su.slackName;
                            }
                        }
                    }
                    returnList.add(msgToCheck);
                }
            }
        }
        catch (Exception ex){
            Log.e(TAG, ex.toString());
        }
        return returnList;
    }

    public ArrayList<SlackUser> getUsers(){
        ArrayList<SlackUser> returnList = new ArrayList<SlackUser>();
        try{
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority("slack.com")
                    .path("api/users.list")
                    .appendQueryParameter("token", token)
                    .build();

            String data = new HttpTools.SendGetRequest().execute(uri.toString()).get();
            returnList = new ParseUsers().execute(data).get();
            for(SlackUser su : returnList){
                if(su.slackName != null && su.slackName.equals(myUserName)){
                    Log.i(TAG,su.slackName + " was set as app user account");
                    myUser = su;
                }
                break;
            }

        }
        catch (Exception ex){
            Log.e(TAG, ex.toString());
        }
        return returnList;
    }


    private static class ParseMessages extends AsyncTask<String, Void, ArrayList<SlackMessage>> {

        @Override
        protected ArrayList<SlackMessage> doInBackground(String... params){
            ArrayList<SlackMessage> messageList = new ArrayList<SlackMessage>();
            try{
                JSONObject jsonObject = new JSONObject(params[0]);
                if(jsonObject.getBoolean("ok")){
                    JSONArray messages = jsonObject.getJSONArray("messages");
                    for(int i = 0; i < messages.length(); i++){
                        SlackMessage slackMessage = new SlackMessage();
                        JSONObject message = messages.getJSONObject(i);
                        slackMessage.text = message.getString("text");
                        if(message.has("user")){
                            slackMessage.user = message.getString("user");
                        }
                        else if(message.has("username")){
                            slackMessage.username = message.getString("username");
                        }
                        String epochDate = message.getString("ts");
                        if(message.has("subtype")){ slackMessage.subtype = message.getString("subtype"); }
                        slackMessage.epoch = epochDate;
                        if(epochDate.indexOf('.') != -1){

                            slackMessage.sent = new Date(Long.parseLong(epochDate.substring(0, epochDate.indexOf('.')))*1000);
                        }
                        if(slackMessage.subtype != null && !slackMessage.subtype.equals("channel_join")){
                            messageList.add(slackMessage);
                        }

                    }

                }
                else{
                    Log.e(TAG,jsonObject.getString("error"));
                }
            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return messageList;
        }
    }

    private static class ParseUsers extends AsyncTask<String, Void, ArrayList<SlackUser>> {

        @Override
        protected ArrayList<SlackUser> doInBackground(String... params){
            ArrayList<SlackUser> userList = new ArrayList<SlackUser>();
            try{
                JSONObject jsonObject = new JSONObject(params[0]);
                if(jsonObject.getBoolean("ok")){
                    JSONArray members = jsonObject.getJSONArray("members");
                    for(int i = 0; i < members.length(); i++){
                        SlackUser slackUser = new SlackUser();
                        JSONObject jsonUser = members.getJSONObject(i);
                        slackUser.slackId = jsonUser.getString("id");
                        if(jsonUser.has("name")){ slackUser.slackName = jsonUser.getString("name"); }
                        if(jsonUser.has("profile")){
                            JSONObject jsonUserProfile = jsonUser.getJSONObject("profile");
                            if(jsonUserProfile.has("first_name")){ slackUser.firstName = jsonUserProfile.getString("first_name"); }
                            if(jsonUserProfile.has("last_name")){ slackUser.lastName = jsonUserProfile.getString("last_name"); }
                            if(jsonUserProfile.has("email")){ slackUser.email = jsonUserProfile.getString("email"); }
                        }
                        userList.add(slackUser);
                    }

                }

            }
            catch (Exception ex)
            {
                Log.e(TAG,ex.toString());
            }
            return userList;
        }

    }
}
