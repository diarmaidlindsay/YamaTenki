package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class WindAndTemperatureElement {
    private final double temperature;
    private final double windVelocity;
    private final int windDirection;

    public WindAndTemperatureElement(double temperature, double windVelocity, int windDirection) {
        this.temperature = temperature;
        this.windVelocity = windVelocity;
        this.windDirection = windDirection;
    }

    public String getTemperature() {
        return "" + (int) temperature;
    }

    public String getWindVelocityString() {
        return "" + (int) windVelocity;
    }

    public int getWindVelocity() {
        return (int) windVelocity;
    }

    public int getWindDirection() {
        return windDirection;
    }
}
