package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.enums.Subscription;
import pulseanddecibels.jp.yamatenki.interfaces.OnInAppBillingServiceSetupComplete;
import pulseanddecibels.jp.yamatenki.utils.SubscriptionSingleton;

/**
 * Created by Diarmaid Lindsay on 2016/01/08.
 * Copyright Pulse and Decibels 2016
 */
public class MapActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, OnInAppBillingServiceSetupComplete {

    HashMap<Marker, Long> markerMap = new HashMap<>();
    private Subscription subscription = null;
    GoogleMap googleMap;
    MapView mMapView;

    private final SparseIntArray DIFFICULTY_SMALL_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_small_a);
            append(2, R.drawable.difficulty_small_b);
            append(3, R.drawable.difficulty_small_c);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if(savedInstanceState != null && savedInstanceState.getSerializable("subscription") != null) {
            Serializable sub = savedInstanceState.getSerializable("subscription");
            if(sub != null) {
                subscription = (Subscription) sub;
                //need to clear because MapView will crash if it tries to read our Subscription object
                savedInstanceState.clear();
            }
        }
        mMapView = (MapView) findViewById(R.id.mapview);
        //must call the whole lifecycle http://www.matt-reid.co.uk/blog_post.php?id=93
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(MapActivity.this);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //center on Japan
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(36, 138);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 4);
        googleMap.moveCamera(cameraUpdate);
        googleMap.setOnInfoWindowClickListener(this);
        if(subscription == null) {
            SubscriptionSingleton.getInstance(this).initGoogleBillingApi(this, this);
        } else {
            InitialiseMapTask initialiseMapTask = new InitialiseMapTask();
            initialiseMapTask.execute(googleMap);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getApplicationContext(), MountainForecastActivity.class);
        intent.putExtra("mountainId", markerMap.get(marker));
        startActivity(intent);
    }

    @Override
    public void iabSetupCompleted(Subscription subscription) {
        this.subscription = subscription;
        InitialiseMapTask initialiseMapTask = new InitialiseMapTask();
        initialiseMapTask.execute(googleMap);
    }

    private class InitialiseMapTask extends AsyncTask<GoogleMap, GoogleMap, GoogleMap> {

        ProgressDialog progressDialog;
        List<Mountain> mountains;
        HashMap<Mountain, Coordinate> coordinateHashMap = new HashMap<>();
        HashMap<Mountain, Integer> statusHashMap = new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(MapActivity.this.getString(R.string.text_dialog_please_wait));
            progressDialog.show();
        }

        @Override
        protected GoogleMap doInBackground(GoogleMap... params) {
            doDatabasePreparation();
            return params[0];
        }

        private void doDatabasePreparation() {
            MountainDao mountainDao = Database.getInstance(MapActivity.this).getMountainDao();

            if(subscription == null || subscription == Subscription.FREE) {
                //Free users can only see top 100 mountains of Japan
                mountains = mountainDao.queryBuilder().where(MountainDao.Properties.TopMountain.eq(1)).list();
            } else {
                mountains = mountainDao.loadAll();
            }

            for(Mountain mountain : mountains) {
                coordinateHashMap.put(mountain, mountain.getCoordinate());
                statusHashMap.put(mountain, mountain.getStatus());
            }
        }

        @Override
        protected void onPostExecute(GoogleMap googleMap) {
            super.onPostExecute(googleMap);
            addMarkers(googleMap);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        private void addMarkers(GoogleMap googleMap) {
            for(Mountain mountain : mountains) {
                if(!mountain.getTopMountain()) {
                    //Free users can only see top 100 mountains of Japan
                    if(subscription == null || subscription == Subscription.FREE) {
                        continue;
                    }
                }
                Coordinate coordinate = coordinateHashMap.get(mountain);
                Integer status = statusHashMap.get(mountain);
                if(coordinate == null || coordinate.getLatitude() == null || coordinate.getLongitude() == null) {
                    Log.e("MapActivity:addMarkers", "Null co-ordinate for mountain : "+mountain.getId());
                    continue;
                }
                LatLng latLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(DIFFICULTY_SMALL_IMAGES.get(status)))
                        .snippet(mountain.getHeight() + "m")
                        .title(mountain.getTitleExt()));
                markerMap.put(marker, mountain.getId());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("subscription", subscription);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        markerMap.clear();
        if(googleMap != null) {
            googleMap.clear();
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        SubscriptionSingleton.getInstance(this).disposeIabHelperInstance(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }
}