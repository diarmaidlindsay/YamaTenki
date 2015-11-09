package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.io.IOException;
import java.util.List;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Area;
import pulseanddecibels.jp.yamatenki.database.dao.AreaDao;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.CoordinateDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.Prefecture;
import pulseanddecibels.jp.yamatenki.database.dao.PrefectureDao;
import pulseanddecibels.jp.yamatenki.database.dao.Status;
import pulseanddecibels.jp.yamatenki.database.dao.StatusDao;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class SplashActivity extends Activity {

    static {
        //change to Japan Time Zone
        DateUtils.setDefaultTimeZone();
    }

    final String PREFS_NAME = "YamaTenkiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        goFullScreen();

        if (isFirstTime()) {
            new DatabaseSetupTask().execute();
        } else {
            displayNormalSplash();
        }
    }

    private void displayNormalSplash() {
        final int SPLASH_TIME_OUT = 3000;

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void goFullScreen() {
        if (Build.VERSION.SDK_INT < 19) { //19 or above api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for lower api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private boolean isFirstTime() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("first_time", true)) {
            settings.edit().putBoolean("first_time", false).apply();
            return true;
        }

        return false;
    }

    /**
     * Only for development purposes
     */
    private void insertSampleData() {
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        AreaDao areaDao = Database.getInstance(this).getAreaDao();
        PrefectureDao prefectureDao = Database.getInstance(this).getPrefectureDao();
        CoordinateDao coordinateDao = Database.getInstance(this).getCoordinateDao();
        StatusDao statusDao = Database.getInstance(this).getStatusDao();
        mountainDao.deleteAll();
        areaDao.deleteAll();
        prefectureDao.deleteAll();
        coordinateDao.deleteAll();
        statusDao.deleteAll();

        try {
            List<String> prefecturesList = Database.parsePrefectureCSV(this);
            for (String prefecture : prefecturesList) {
                prefectureDao.insert(new Prefecture(null, prefecture));
            }

            List<String> areasList = Database.parseAreaCSV(this);
            //start from zero to match the specifications (0 = 北海道, etc)
            for (int i=0; i < areasList.size(); i++) {
                areaDao.insert(new Area((long)i, areasList.get(i)));
            }

            List<MountainArrayElement> jsonEntries = Database.parseMountainJSON(this);
            for(MountainArrayElement element : jsonEntries) {

                Area area = areaDao.queryBuilder().where(AreaDao.Properties.Id.eq(element.getArea())).unique();
                Prefecture prefecture = prefectureDao.queryBuilder().where(PrefectureDao.Properties.Name.eq(element.getPrefecture())).unique();
                Long coordinateId = coordinateDao.insert(new Coordinate(null, (float) element.getCoordinate().getLatitude(), (float) element.getCoordinate().getLongitude()));
                Long areaId = area != null ? area.getId() : 9L; //9L == Unknown 不明
                Long prefectureId = prefecture != null ? prefecture.getId() : 47L; //47L == Unknown 不明
                Long mountainId = mountainDao.insert(new Mountain(null, element.getYid(), element.getTitle(), element.getTitleExt(), element.getTitleEnglish(), element.getKana(),
                                coordinateId, prefectureId, areaId, element.getHeight()));
                statusDao.insert(new Status(null, mountainId, element.getCurrentMountainStatus()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DatabaseSetupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            insertSampleData();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
