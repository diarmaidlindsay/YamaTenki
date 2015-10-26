package pulseanddecibels.jp.yamatenki.enums;

/**
 * Created by Diarmaid Lindsay on 2015/10/23.
 * Copyright Pulse and Decibels 2015
 */
public enum Difficulty {
    A(1), B(2), C(3);

    int index;

    Difficulty(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
