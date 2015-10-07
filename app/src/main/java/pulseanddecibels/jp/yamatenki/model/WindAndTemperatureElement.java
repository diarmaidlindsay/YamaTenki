package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class WindAndTemperatureElement {
    int temperature;
    int windVelocity;
    int windDirection;

    public WindAndTemperatureElement(int temperature, int windVelocity, int windDirection) {
        this.temperature = temperature;
        this.windVelocity = windVelocity;
        this.windDirection = windDirection;
    }

    public String getTemperature() {
        return ""+temperature;
    }

    public String getWindVelocity() {
        return ""+windVelocity;
    }

    public int getWindDirection() {
        return windDirection;
    }
}
