package com.et.jbdbcdemoapp.jbdbc;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by jzehner on 4/29/14.
 */
public class SlackMessage {
    public String text;
    public String user;
    public String username;
    public Date sent;
    public String epoch;

    public static Comparator<SlackMessage> COMPARE_BY_DATE = new Comparator<SlackMessage>() {
        public int compare(SlackMessage one, SlackMessage other) {
            return one.sent.compareTo(other.sent);
        }
    };

    public static Comparator<SlackMessage> COMPARE_BY_EPOCH = new Comparator<SlackMessage>() {
        public int compare(SlackMessage one, SlackMessage other) {
            return one.epoch.compareTo(other.epoch);
        }
    };

    public boolean equalsMessage(SlackMessage other){
        if(this.text.equals(other.text) && this.epoch.equals(other.epoch)){
            return true;
        }
        return false;
    }

}
