package pulseanddecibels.jp.yamatenki.model;

import android.util.SparseIntArray;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.utils.DateUtils;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels
 * <p/>
 * Representation of an entire JSON file for a mountain's weather forecast
 */
public class MountainForecastJSON {
    private final Map<String, ForecastArrayElement> forecasts;
    private final SparseIntArray heights;
    private MountainArrayElement mountainArrayElement;
    private String referenceCity;
    private String timestamp;
    private Integer currentMountainForecast;

    public MountainForecastJSON() {
        forecasts = new HashMap<>();
        heights = new SparseIntArray();
    }

    public MountainForecastJSON(MountainArrayElement mountainArrayElement, Map<String, ForecastArrayElement> forecasts, String referenceCity, SparseIntArray heights, String timestamp, Integer currentMountainForecast) {
        this.mountainArrayElement = mountainArrayElement;
        this.forecasts = forecasts;
        this.referenceCity = referenceCity;
        this.heights = heights;
        this.timestamp = timestamp;
        this.currentMountainForecast = currentMountainForecast;
    }

    public MountainArrayElement getMountainArrayElement() {
        return mountainArrayElement;
    }

    public Map<String, ForecastArrayElement> getForecasts() {
        return forecasts;
    }

    public String getReferenceCity() {
        return referenceCity;
    }

    public SparseIntArray getHeights() {
        return heights;
    }

    public DateTime getTimestamp() {
        return DateUtils.getDateTimeFromForecast(timestamp);
    }

    public Integer getCurrentMountainForecast() {
        return currentMountainForecast;
    }
}
