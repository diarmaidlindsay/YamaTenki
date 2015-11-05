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
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView header;
    Button mountainNameSearchButton;
    Button nearMountainSearchButton;
    Button areaSearchButton;
    Button heightSearchButton;
    Button myMountainListButton;
    Button myMemoButton;
    Button settingsButton;
    Button checklistButton;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        header = (TextView) findViewById(R.id.text_main_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        mountainNameSearchButton = (Button) findViewById(R.id.button_main_mountain_name_search);
        mountainNameSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        nearMountainSearchButton = (Button) findViewById(R.id.button_main_20_closest_search);
        nearMountainSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        areaSearchButton = (Button) findViewById(R.id.button_main_area_search);
        areaSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        heightSearchButton = (Button) findViewById(R.id.button_main_height_search);
        heightSearchButton.setTypeface(Utils.getHannariTypeFace(this));
        myMountainListButton = (Button) findViewById(R.id.button_main_my_mountain_list);
        myMountainListButton.setTypeface(Utils.getHannariTypeFace(this));
        myMemoButton = (Button) findViewById(R.id.button_main_memo);
        myMemoButton.setTypeface(Utils.getHannariTypeFace(this));
        settingsButton = (Button) findViewById(R.id.button_main_settings);
        settingsButton.setTypeface(Utils.getHannariTypeFace(this));
        checklistButton = (Button) findViewById(R.id.button_main_tool_list);
        checklistButton.setTypeface(Utils.getHannariTypeFace(this));

        mountainNameSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MountainListActivity.class);
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
                    Intent intent = new Intent(getApplicationContext(), MountainListActivity.class);
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
                Intent intent = new Intent(getApplicationContext(), MountainListActivity.class);
                intent.putExtra("searchType", "height"); //indicate we want height search
                startActivity(intent);
            }
        });

        myMountainListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MountainListActivity.class);
                intent.putExtra("searchType", "myMountain"); //indicate we want my mountain list
                startActivity(intent);
            }
        });

        buildGoogleApiClient();
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
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
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
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() && mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
