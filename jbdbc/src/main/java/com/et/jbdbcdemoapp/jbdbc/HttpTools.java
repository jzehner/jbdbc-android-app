package com.et.jbdbcdemoapp.jbdbc;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by jzehner on 4/30/14.
 */
public class HttpTools {

    public static class SendGetRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(urls[0]);
                HttpResponse response = httpclient.execute(request);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                return sb.toString();
            }catch (Exception ex){
                Log.e("JBDBC", ex.toString());
                return "Failed";
            }
        }
    }

    public static class SendPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urls[0]);
                StringEntity params =new StringEntity(urls[1]);
                httppost.addHeader("Content-type", "application/json");
                httppost.setEntity(params);
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                return sb.toString();
            }
            catch (Exception ex){
                Log.e("JBDBC", ex.toString());
                return "Failed";
            }
        }
    }

}
