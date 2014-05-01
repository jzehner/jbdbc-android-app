package com.et.jbdbcdemoapp.jbdbc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by jzehner on 4/29/14.
 */
public class MessageListAdapter extends ArrayAdapter<SlackMessage> {
    Context mContext;
    int layoutResourceId;
    List<SlackMessage> data = null;
    public String userKey;

    public MessageListAdapter(Context context, int layoutResourceId, List<SlackMessage> data) {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

        }
        SlackMessage message = data.get(position);
        if(!message.username.equals(userKey)){
            //user message
            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(message.text);

            TextView userText = (TextView) convertView.findViewById(R.id.textViewName);
            userText.setText(message.username);

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            TextView dateText = (TextView) convertView.findViewById(R.id.textViewDate);
            dateText.setText(df.format(message.sent));

            TextView textViewItem2 = (TextView) convertView.findViewById(R.id.textViewGuestItem);
            textViewItem2.setText("");

            TextView userText2 = (TextView) convertView.findViewById(R.id.textViewGuestName);
            userText2.setText("");

            TextView dateText2 = (TextView) convertView.findViewById(R.id.textViewGuestDate);
            dateText2.setText("");
        }
        else{
            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewGuestItem);
            textViewItem.setText(message.text);

            TextView userText = (TextView) convertView.findViewById(R.id.textViewGuestName);
            userText.setText(message.username);

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            TextView dateText = (TextView) convertView.findViewById(R.id.textViewGuestDate);
            dateText.setText(df.format(message.sent));

            TextView textViewItem2 = (TextView) convertView.findViewById(R.id.textViewItem);
            textViewItem2.setText("");

            TextView userText2 = (TextView) convertView.findViewById(R.id.textViewName);
            userText2.setText("");

            TextView dateText2 = (TextView) convertView.findViewById(R.id.textViewDate);
            dateText2.setText("");
        }




        return convertView;

    }

}
