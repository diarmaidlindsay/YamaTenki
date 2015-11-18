package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/10/14.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListCSVEntry {
    private final String kanjiName;
    private final String kanjiNameArea;
    private final String hiraganaName;
    private final String romajiName;
    private final Integer height;
    private final String prefecture;
    private final String area;
    private final Float latitude;
    private final Float longitude;
    private final String closestTown;

    public MountainListCSVEntry(String kanjiName, String kanjiNameArea, String hiraganaName, String romajiName, Integer height, String prefecture, String area, Float latitude, Float longitude, String closestTown) {
        this.kanjiName = kanjiName;
        this.kanjiNameArea = kanjiNameArea;
        this.hiraganaName = hiraganaName;
        this.romajiName = romajiName;
        this.height = height;
        this.prefecture = prefecture;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.closestTown = closestTown;
    }

    public String getKanjiName() {
        return kanjiName;
    }

    public String getKanjiNameArea() {
        return kanjiNameArea;
    }

    public String getHiraganaName() {
        return hiraganaName;
    }

    public String getRomajiName() {
        return romajiName;
    }

    public Integer getHeight() {
        return height;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public String getArea() {
        return area;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public String getClosestTown() {
        return closestTown;
    }
}
