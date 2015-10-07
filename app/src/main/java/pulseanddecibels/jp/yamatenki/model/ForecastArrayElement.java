package pulseanddecibels.jp.yamatenki.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 *
 * Representation of a forecast object in a
 * "forecastsDaily" or
 * "forecasts"
 * in a json file
 */
public class ForecastArrayElement {
    Timestamp dateTime;
    int mountainIndex;
    List<WindAndTemperatureElement> windAndTemperatures;
    int weather;
    Integer temperatureHigh; //Integer because null should be allowed
    Integer temperatureLow;
    Integer temperature;
    float precipitation;

    public ForecastArrayElement(Timestamp dateTime, int mountainIndex, List<WindAndTemperatureElement> windAndTemperatures, int weather, Integer temperatureHigh, Integer temperatureLow, float precipitation) {
        this.dateTime = dateTime;
        this.mountainIndex = mountainIndex;
        this.windAndTemperatures = windAndTemperatures;
        this.weather = weather;
        this.temperatureHigh = temperatureHigh;
        this.temperatureLow = temperatureLow;
        this.precipitation = precipitation;
    }

    public ForecastArrayElement(Timestamp dateTime, int mountainIndex, List<WindAndTemperatureElement> windAndTemperatures, int weather, Integer temperature, float precipitation) {
        this.dateTime = dateTime;
        this.mountainIndex = mountainIndex;
        this.windAndTemperatures = windAndTemperatures;
        this.weather = weather;
        this.temperature = temperature;
        this.precipitation = precipitation;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public int getMountainIndex() {
        return mountainIndex;
    }

    public List<WindAndTemperatureElement> getWindAndTemperatures() {
        return windAndTemperatures;
    }

    public int getWeather() {
        return weather;
    }

    public String getTemperatureHigh() {
        return ""+temperatureHigh;
    }

    public String getTemperatureLow() {
        return ""+temperatureLow;
    }

    public String getTemperature() {
        return ""+temperature;
    }

    public String getPrecipitation() {
        return ""+precipitation;
    }
}
