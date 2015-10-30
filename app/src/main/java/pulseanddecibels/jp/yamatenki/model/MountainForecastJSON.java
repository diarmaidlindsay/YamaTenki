package pulseanddecibels.jp.yamatenki.model;

import android.util.SparseArray;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels
 * <p/>
 * Representation of an entire JSON file for a mountain's weather forecast
 */
public class MountainForecastJSON {
    MountainArrayElement mountainArrayElement;
    Map<String, ForecastArrayElement> forecasts;
    String referenceCity;
    SparseArray<Integer> heights;
    String timestamp;

    public MountainForecastJSON() {
        forecasts = new HashMap<>();
        heights = new SparseArray<>();
    }

    public MountainForecastJSON(MountainArrayElement mountainArrayElement, Map<String, ForecastArrayElement> forecasts, String referenceCity, SparseArray<Integer> heights, String timestamp) {
        this.mountainArrayElement = mountainArrayElement;
        this.forecasts = forecasts;
        this.referenceCity = referenceCity;
        this.heights = heights;
        this.timestamp = timestamp;
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

    public SparseArray<Integer> getHeights() {
        return heights;
    }

    public Timestamp getTimestamp() {
        return Utils.getTimeStamp(timestamp);
    }
}
