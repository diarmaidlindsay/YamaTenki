package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class Coordinate {
    float latitude;
    float longitude;

    public Coordinate(long latitude, long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
