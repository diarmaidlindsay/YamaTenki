package pulseanddecibels.jp.yamatenki.enums;

/**
 * Created by Diarmaid Lindsay on 2015/11/17.
 * Copyright Pulse and Decibels 2015
 */
public enum SortOrder {
    ASC("▲"),
    DESC("▼");

    final String indicator;

    SortOrder(String indicator) {
        this.indicator = indicator;
    }

    public String getIndicator() {
        return indicator;
    }
}
