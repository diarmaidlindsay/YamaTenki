package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/10/19.
 * Copyright Pulse and Decibels 2015
 */
public class AreaSearchActivity extends Activity {
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        header = (TextView) findViewById(R.id.text_search_header);
        header.setTypeface(Utils.getHannariTypeFace(this));

        initButton(R.id.button_area_hokkaidou);
        initButton(R.id.button_area_hokuriku);
        initButton(R.id.button_area_toukai);
        initButton(R.id.button_area_chuugoku);
        initButton(R.id.button_area_okinawa);
        initButton(R.id.button_area_touhoku);
        initButton(R.id.button_area_koushin);
        initButton(R.id.button_area_kinki);
        initButton(R.id.button_area_shikoku);
    }

    private void initButton(int id) {
        Button button = (Button) findViewById(id);
        button.setTypeface(Utils.getHannariTypeFace(this));
        button.setOnClickListener(getOnClickListener(id));
    }

    private View.OnClickListener getOnClickListener(final int buttonId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("areaButtonId", buttonId);
                startActivity(intent);
            }
        };
    }
}
