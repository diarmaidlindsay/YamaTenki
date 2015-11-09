package pulseanddecibels.jp.yamatenki.model;

import org.joda.time.DateTime;

import java.util.ArrayList;

import pulseanddecibels.jp.yamatenki.utils.DateUtils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a mountain in the "mountain_list" JSON
 */
public class MountainListJSON {
    ArrayList<MountainArrayElement> mountainArrayElements;
    String timestamp;

    public MountainListJSON() {
        mountainArrayElements = new ArrayList<>();
        timestamp = "";
    }

    public MountainListJSON(ArrayList<MountainArrayElement> mountainArrayElements, String timeStampString) {
        this.mountainArrayElements = mountainArrayElements;
        this.timestamp = timeStampString;
    }

    public ArrayList<MountainArrayElement> getMountainArrayElements() {
        return mountainArrayElements;
    }

    public DateTime getTimestamp() {
        return DateUtils.getDateTimeFromForecast(timestamp);
    }
}
