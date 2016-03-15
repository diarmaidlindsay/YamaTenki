package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2016/03/14.
 * Copyright Pulse and Decibels 2016
 *
 * preferenceList.csv row representation
 */
public class PrefectureElement {
    String name;
    String nameEnglish;

    public PrefectureElement(String name, String nameEnglish) {
        this.name = name;
        this.nameEnglish = nameEnglish;
    }

    public String getName() {
        return name;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }
}
