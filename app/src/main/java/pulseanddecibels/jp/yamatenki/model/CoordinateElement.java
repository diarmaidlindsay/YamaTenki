package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a "coordinate" object found in JSON
 */
public class CoordinateElement {
    float latitude;
    float longitude;

    public CoordinateElement(long latitude, long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
