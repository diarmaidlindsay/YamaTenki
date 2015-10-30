package pulseanddecibels.jp.yamatenki.model;

import java.sql.Timestamp;
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
    Timestamp dateTime;
    int mountainStatus;
    List<WindAndTemperatureElement> windAndTemperatures;
    double temperature;
    double precipitation;
    int totalCloudCover;

    public ForecastArrayElement(Timestamp dateTime, int mountainStatus, List<WindAndTemperatureElement> windAndTemperatures, Integer temperature, float precipitation, int totalCloudCover) {
        this.dateTime = dateTime;
        this.mountainStatus = mountainStatus;
        this.windAndTemperatures = windAndTemperatures;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.totalCloudCover = totalCloudCover;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public List<WindAndTemperatureElement> getWindAndTemperatures() {
        return windAndTemperatures;
    }

    public String getTemperature() { //what is this for?
        return String.format("%.1f", temperature);
    }

    public String getPrecipitation() {
        return String.format("%.1f", precipitation);
    }

    public int getMountainStatus() {
        return mountainStatus;
    }

    public String getTotalCloudCover() {
        return "" + totalCloudCover;
    }
}
