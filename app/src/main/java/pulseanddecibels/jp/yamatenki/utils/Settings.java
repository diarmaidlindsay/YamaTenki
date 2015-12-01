package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Diarmaid Lindsay on 2015/11/24.
 * Copyright Pulse and Decibels 2015
 *
 * First time run
 *
 * Forecast Warning
 * Update over Mobile Network
 * Reset Checklist Automatically
 */
public class Settings {
    private final String PREFS_NAME = "YamaTenkiPrefs";
    private final SharedPreferences settings;

    public Settings(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public boolean isFirstTimeRun() {
        if (settings.getBoolean("first_time", true)) {
            settings.edit().putBoolean("first_time", false).apply();
            return true;
        }

        return false;
    }

    public boolean getSetting(String key) {
        return settings.getBoolean(key, false);
    }

    public void setSetting(String key, boolean value) {
        settings.edit().putBoolean(key, value).apply();
    }
}