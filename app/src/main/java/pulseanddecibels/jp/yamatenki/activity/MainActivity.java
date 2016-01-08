package pulseanddecibels.jp.yamatenki.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
@RuntimePermissions
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(this);
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
        Button mapButton = (Button) findViewById(R.id.button_main_map);
        mapButton.setTypeface(Utils.getHannariTypeFace(this));

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
                    startIntent20ClosestMountains();
                } else {
                    MainActivityPermissionsDispatcher.buildGoogleApiClientWithCheck(MainActivity.this);
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

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
            }
        });

        JSONDownloader.getMountainListFromServer(this);
        JSONDownloader.getMountainStatusFromServer(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(MainActivity.this.getString(R.string.text_dialog_please_wait));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mGoogleApiClient.disconnect();
            }
        });
        progressDialog.show();
    }

    private void startIntent20ClosestMountains() {
        Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
        intent.putExtra("lat", mLastLocation.getLatitude());
        intent.putExtra("long", mLastLocation.getLongitude());
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            //do nothing! The PermissionsDispatcher framework handles this
            Log.e("MainActivity", "onConnected : "+e);
        }

        if (mLastLocation == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30000);

            requestLocationUpdates();
        } else {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startIntent20ClosestMountains();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(Settings.isDebugMode())
            Toast.makeText(this, "Google Play Service Connection Suspended...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(Settings.isDebugMode())
            Toast.makeText(this, "Failed to connect to Google Play Service...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        startIntent20ClosestMountains();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() && progressDialog != null && progressDialog.isShowing()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if the user pressed "home" when we're still looking for updates, resume the process now
        if(progressDialog != null && progressDialog.isShowing() && mGoogleApiClient != null && mLastLocation == null) {
            if(!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void requestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            //do nothing! The PermissionsDispatcher framework handles this
            Log.e("MainActivity", "requestLocationUpdates : "+e);
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationDenied() {
        Toast.makeText(this, getString(R.string.toast_error_location_denied), Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationNeverAskAgain() {
        Toast.makeText(this, getString(R.string.toast_error_location_dont_ask_again), Toast.LENGTH_LONG).show();
    }
}
