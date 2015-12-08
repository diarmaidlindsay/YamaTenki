package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class WindAndTemperatureElement {
    private final double temperature;
    private final double windVelocity;
    private final double windDirection;

    public WindAndTemperatureElement(double temperature, double windVelocity, double windDirection) {
        this.temperature = temperature;
        this.windVelocity = windVelocity;
        this.windDirection = windDirection;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getWindVelocity() {
        return windVelocity;
    }

    public Double getWindDirection() {
        return windDirection;
    }
}
