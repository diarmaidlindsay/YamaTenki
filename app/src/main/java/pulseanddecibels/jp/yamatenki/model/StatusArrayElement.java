package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/12/09.
 * Copyright Pulse and Decibels 2015
 */
public class StatusArrayElement {
    private final String yid;
    private final Integer cms;

    public StatusArrayElement(String yid, Integer cms) {
        this.yid = yid;
        this.cms = cms;
    }

    public Integer getCms() {
        return cms;
    }

    public String getYid() {
        return yid;
    }
}
