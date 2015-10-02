package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import pulseanddecibels.jp.yamatenki.R;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastActivity extends Activity {
    ImageView currentDifficultyImage;
    ScrollView mountainForecastScrollView;
    String yid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Bundle arguments = getIntent().getExtras();
        yid = arguments.getString("yid");

        currentDifficultyImage = (ImageView) findViewById(R.id.mountain_forecast_current_difficulty);
        mountainForecastScrollView = (ScrollView) findViewById(R.id.scroll_forecasts);
        LinearLayout todayForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_forecast);
        LinearLayout tomorrowForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_forecast);
    }
}
