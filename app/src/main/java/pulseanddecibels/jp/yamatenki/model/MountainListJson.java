package pulseanddecibels.jp.yamatenki.model;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListJson {
    ArrayList<MountainListItem> mountainListItems;
    String timeStampString;

    public MountainListJson() {
        mountainListItems = new ArrayList<>();
        String timeStampString = "";
    }

    public MountainListJson(ArrayList<MountainListItem> mountainListItems, String timeStampString) {
        this.mountainListItems = mountainListItems;
        this.timeStampString = timeStampString;
    }

    public ArrayList<MountainListItem> getMountainListItems() {
        return mountainListItems;
    }

    public Timestamp getTimeStamp() {
        //may need tweaked
        //http://developer.android.com/reference/java/text/SimpleDateFormat.html
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'1T'HH:mm:ss.ZZZZZ", Locale.JAPAN);
            Date date = dateFormat.parse(timeStampString);
            timestamp = new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //"timestamp" : "2015-09-181T00:00:00.+09:00"

        return timestamp;
    }
}
