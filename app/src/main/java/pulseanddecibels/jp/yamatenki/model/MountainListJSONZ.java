package pulseanddecibels.jp.yamatenki.model;

import java.sql.Timestamp;
import java.util.ArrayList;

import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a mountain in the "mountain_list" JSON
 */
public class MountainListJSONZ {
    ArrayList<MountainArrayElement> mountainArrayElements;
    String timestamp;

    public MountainListJSONZ() {
        mountainArrayElements = new ArrayList<>();
        String timeStampString = "";
    }

    public MountainListJSONZ(ArrayList<MountainArrayElement> mountainArrayElements, String timeStampString) {
        this.mountainArrayElements = mountainArrayElements;
        this.timestamp = timeStampString;
    }

    public ArrayList<MountainArrayElement> getMountainArrayElements() {
        return mountainArrayElements;
    }

    public Timestamp getTimestamp() {
        return Utils.getTimeStamp(timestamp);
    }
}
