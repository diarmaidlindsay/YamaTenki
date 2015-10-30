package pulseanddecibels.jp.yamatenki.model;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class WindAndTemperatureElement {
    double temperature;
    double windVelocity;
    int windDirection;

    public WindAndTemperatureElement(double temperature, double windVelocity, int windDirection) {
        this.temperature = temperature;
        this.windVelocity = windVelocity;
        this.windDirection = windDirection;
    }

    public String getTemperature() {
        return String.format("%.1f", temperature);
    }

    public String getWindVelocity() {
        return String.format("%.1f", windVelocity);
    }

    public int getWindDirection() {
        return windDirection;
    }
}
