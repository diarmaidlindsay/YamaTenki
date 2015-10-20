package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    TextView header;
    ImageView mountainNameSearchButton;
    ImageView closeMountainSearchButton;
    ImageView areaSearchButton;
    ImageView heightSearchButton;
    ImageView myMountainListButton;
    ImageView myMemoButton;
    ImageView settingsButton;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

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
                startActivity(new Intent(getApplicationContext(), MountainListActivity.class));
            }
        });

        areaSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AreaSearchActivity.class));
            }
        });

        closeMountainSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    Intent intent = new Intent(getApplicationContext(), MountainListActivity.class);
                    intent.putExtra("lat", mLastLocation.getLatitude());
                    intent.putExtra("long", mLastLocation.getLongitude());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Couldn't get the current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        connectToGooglePlayService();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Couldn't connect to Google Play Service", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToGooglePlayService() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Google Play Service Connection Suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect to Google Play Service...", Toast.LENGTH_SHORT).show();
    }
}
