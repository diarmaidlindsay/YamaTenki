package pulseanddecibels.jp.yamatenki.model;

import org.joda.time.DateTime;

import java.util.List;

import pulseanddecibels.jp.yamatenki.utils.DateUtils;

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
    private final String timeStamp;
    private final int mountainStatus;
    private final List<WindAndTemperatureElement> windAndTemperatures;
    private final double temperature;
    private final double precipitation;
    private final double totalCloudCover;

    public ForecastArrayElement(String timeStamp, int mountainStatus, List<WindAndTemperatureElement> windAndTemperatures, Integer temperature, float precipitation, int totalCloudCover) {
        this.timeStamp = timeStamp;
        this.mountainStatus = mountainStatus;
        this.windAndTemperatures = windAndTemperatures;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.totalCloudCover = totalCloudCover;
    }

    public DateTime getDateTime() {
        return DateUtils.getDateTimeFromForecast(timeStamp);
    }

    public List<WindAndTemperatureElement> getWindAndTemperatures() {
        return windAndTemperatures;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public int getMountainStatus() {
        return mountainStatus;
    }

    public Double getTotalCloudCover() {
        return totalCloudCover;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
