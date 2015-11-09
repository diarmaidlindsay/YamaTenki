package pulseanddecibels.jp.yamatenki.model;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Representation of a forecast object in a
 * "forecastsDaily" or
 * "forecasts"
 * in a json file
 */
public class ForecastArrayElement {
    DateTime dateTime;
    int mountainStatus;
    List<WindAndTemperatureElement> windAndTemperatures;
    double temperature;
    double precipitation;
    int totalCloudCover;

    public ForecastArrayElement(DateTime dateTime, int mountainStatus, List<WindAndTemperatureElement> windAndTemperatures, Integer temperature, float precipitation, int totalCloudCover) {
        this.dateTime = dateTime;
        this.mountainStatus = mountainStatus;
        this.windAndTemperatures = windAndTemperatures;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.totalCloudCover = totalCloudCover;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public List<WindAndTemperatureElement> getWindAndTemperatures() {
        return windAndTemperatures;
    }

    public String getTemperature() { //what is this for?
        return ""+ (int) temperature;
    }

    public String getPrecipitation() {
        return "" + (int) precipitation;
    }

    public int getMountainStatus() {
        return mountainStatus;
    }

    public String getTotalCloudCover() {
        return "" + totalCloudCover / 10;
    }
}
