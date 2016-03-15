package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a mountain "list" array element from "mountainList" JSON
 */
public class MountainArrayElement {
    private final String yid;
    private final String title;
    private final String titleExt;
    private final String titleSplitted1;
    private final String titleSplitted2;
    private final String titleEnglish;
    private final String titleSplittedEnglish1;
    private final String titleSplittedEnglish2;
    private final String kana;
    private final String referenceCity;
    private final String referenceCityEnglish;
    private final CoordinateElement coordinate;
    private final String prefecture;
    private final String prefectureEnglish;
    private final int area;
    private final int height;
    private final int topMountain;

    public MountainArrayElement(String yid, String title, String titleExt, String titleSplitted1, String titleSplitted2, String titleEnglish, String titleSplittedEnglish1, String titleSplittedEnglish2, String kana, String referenceCity, String referenceCityEnglish, CoordinateElement coordinate, String prefecture, String prefectureEnglish, int area, int height, int topMountain) {
        this.yid = yid;
        this.title = title;
        this.titleExt = titleExt;
        this.titleSplitted1 = titleSplitted1;
        this.titleSplitted2 = titleSplitted2;
        this.titleEnglish = titleEnglish;
        this.titleSplittedEnglish1 = titleSplittedEnglish1;
        this.titleSplittedEnglish2 = titleSplittedEnglish2;
        this.kana = kana;
        this.referenceCity = referenceCity;
        this.referenceCityEnglish = referenceCityEnglish;
        this.coordinate = coordinate;
        this.prefecture = prefecture;
        this.prefectureEnglish = prefectureEnglish;
        this.area = area;
        this.height = height;
        this.topMountain = topMountain;
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

    public int getTopMountain() {
        return topMountain;
    }

    public String getReferenceCity() {
        return referenceCity;
    }

    public String getTitleSplitted1() {
        return titleSplitted1;
    }

    public String getTitleSplitted2() {
        return titleSplitted2;
    }

    public String getTitleSplittedEnglish1() {
        return titleSplittedEnglish1;
    }

    public String getTitleSplittedEnglish2() {
        return titleSplittedEnglish2;
    }

    public String getReferenceCityEnglish() {
        return referenceCityEnglish;
    }

    public String getPrefectureEnglish() {
        return prefectureEnglish;
    }
}
