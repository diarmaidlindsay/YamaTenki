package pulseanddecibels.jp.yamatenki.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels
 *
 * Representation of an entire JSON file for a mountain's weather forecast
 */
public class MountainForecastJSON {
    MountainArrayElement mountainArrayElement;
    List<ForecastArrayElement> forecasts; //today and tomorrow, 3 hour intervals, 9-16 min/max
    List<ForecastArrayElement> forecastsDaily; //day after tomorrow and onwards
    String referenceCity;
    List<Integer> heights;
    String timestamp;

    public MountainForecastJSON() {
        forecasts = new ArrayList<>();
        forecastsDaily = new ArrayList<>();
        heights = new ArrayList<>();
    }

    public MountainForecastJSON(MountainArrayElement mountainArrayElement, List<ForecastArrayElement> forecasts, List<ForecastArrayElement> forecastsDaily, String referenceCity, List<Integer> heights, String timestamp) {
        this.mountainArrayElement = mountainArrayElement;
        this.forecasts = forecasts;
        this.forecastsDaily = forecastsDaily;
        this.referenceCity = referenceCity;
        this.heights = heights;
        this.timestamp = timestamp;
    }

    public MountainArrayElement getMountainArrayElement() {
        return mountainArrayElement;
    }

    public List<ForecastArrayElement> getForecasts() {
        return forecasts;
    }

    public List<ForecastArrayElement> getForecastsDaily() {
        return forecastsDaily;
    }

    public String getReferenceCity() {
        return referenceCity;
    }

    public int getPeakHeight() {
        return heights.get(0);
    }

    public int getBaseHeight() {
        return heights.get(1);
    }

    public Timestamp getTimestamp() {
        return Utils.getTimeStamp(timestamp);
    }
}
