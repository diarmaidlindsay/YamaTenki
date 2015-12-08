package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView header = (TextView) findViewById(R.id.text_main_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        Button mountainNameSearchButton = (Button) findViewById(R.id.button_main_mountain_name_search);
        mountainNameSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        Button nearMountainSearchButton = (Button) findViewById(R.id.button_main_20_closest_search);
        nearMountainSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        Button areaSearchButton = (Button) findViewById(R.id.button_main_area_search);
        areaSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        Button heightSearchButton = (Button) findViewById(R.id.button_main_height_search);
        heightSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        Button myMountainListButton = (Button) findViewById(R.id.button_main_my_mountain_list);
        myMountainListButton.setTypeface(Utils.getHannariTypeFace(this));
        Button myMemoButton = (Button) findViewById(R.id.button_main_memo);
        myMemoButton.setTypeface(Utils.getHannariTypeFace(this));
        Button settingsButton = (Button) findViewById(R.id.button_main_settings);
        settingsButton.setTypeface(Utils.getHannariTypeFace(this));
        Button checklistButton = (Button) findViewById(R.id.button_main_tool_list);
        checklistButton.setTypeface(Utils.getHannariTypeFace(this));

        mountainNameSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("searchType", "name");
                startActivity(intent);
            }
        });

        areaSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AreaSearchActivity.class));
            }
        });

        nearMountainSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                    intent.putExtra("lat", mLastLocation.getLatitude());
                    intent.putExtra("long", mLastLocation.getLongitude());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Waiting for current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        heightSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("searchType", "height"); //indicate we want height search
                startActivity(intent);
            }
        });

        myMountainListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("searchType", "myMountain"); //indicate we want my mountain list
                startActivity(intent);
            }
        });

        myMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("searchType", "myMemo"); //indicate we want my memo
                startActivity(intent);
            }
        });

        checklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChecklistActivity.class));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        buildGoogleApiClient();
        JSONDownloader.getMountainListFromServer(this);
    }

    private void buildGoogleApiClient() {
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

        if (mLastLocation == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30000);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Google Play Service Connection Suspended...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect to Google Play Service...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Couldn't connect to Google Play Service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
