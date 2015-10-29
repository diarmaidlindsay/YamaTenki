package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a mountain "list" array element from "mountainList" JSON
 */
public class MountainArrayElement {
    String yid;
    String title;
    String titleExt;
    String titleEnglish;
    String kana;
    CoordinateElement coordinate;
    String prefecture;
    int area;
    int height;
    int currentMountainIndex;

    public MountainArrayElement(String yid, String title, String titleExt, String titleEnglish, String kana, CoordinateElement coordinate, String prefecture, int area, int height, int currentMountainIndex) {
        this.yid = yid;
        this.title = title;
        this.titleExt = titleExt;
        this.titleEnglish = titleEnglish;
        this.kana = kana;
        this.coordinate = coordinate;
        this.prefecture = prefecture;
        this.area = area;
        this.height = height;
        this.currentMountainIndex = currentMountainIndex;
    }

    public String getYid() {
        return yid;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleExt() {
        return titleExt;
    }

    public String getTitleEnglish() {
        return titleEnglish;
    }

    public String getKana() {
        return kana;
    }

    public CoordinateElement getCoordinate() {
        return coordinate;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public int getArea() {
        return area;
    }

    public int getHeight() {
        return height;
    }

    public int getCurrentMountainIndex() {
        return currentMountainIndex;
    }
}
