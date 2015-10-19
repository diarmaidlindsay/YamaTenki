package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class MainActivity extends Activity {

    TextView header;
    ImageView mountainNameSearchButton;
    ImageView closeMountainSearchButton;
    ImageView areaSearchButton;
    ImageView heightSearchButton;
    ImageView myMountainListButton;
    ImageView myMemoButton;
    ImageView settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        header = (TextView) findViewById(R.id.text_main_header);
        header.setTypeface(Utils.getTitleTypeFace(this));
        mountainNameSearchButton = (ImageView) findViewById(R.id.button_main_mountain_name_search);
        closeMountainSearchButton = (ImageView) findViewById(R.id.button_main_20_closest_search);
        areaSearchButton = (ImageView) findViewById(R.id.button_main_area_search);
        heightSearchButton = (ImageView) findViewById(R.id.button_main_height_search);
        myMountainListButton = (ImageView) findViewById(R.id.button_main_my_mountain_list);
        myMemoButton = (ImageView) findViewById(R.id.button_main_memo);
        settingsButton = (ImageView) findViewById(R.id.button_main_settings);

        mountainNameSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MountainSearchActivity.class));
            }
        });

        areaSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AreaSearchActivity.class));
            }
        });
    }
}
