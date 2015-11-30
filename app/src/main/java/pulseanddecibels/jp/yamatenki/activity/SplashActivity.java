package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.facebook.FacebookSdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Area;
import pulseanddecibels.jp.yamatenki.database.dao.AreaDao;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItem;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItemDao;
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
import pulseanddecibels.jp.yamatenki.utils.Settings;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class SplashActivity extends Activity {

    static {
        //change to Japan Time Zone
        DateUtils.setDefaultTimeZone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());
        goFullScreen();

        if (new Settings(this).isFirstTimeRun()) {
            new DatabaseSetupTask().execute();
            writeDefaultSettings();
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

    /**
     * Only for development purposes
     */
    private void insertSampleData() {
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        AreaDao areaDao = Database.getInstance(this).getAreaDao();
        PrefectureDao prefectureDao = Database.getInstance(this).getPrefectureDao();
        CoordinateDao coordinateDao = Database.getInstance(this).getCoordinateDao();
        StatusDao statusDao = Database.getInstance(this).getStatusDao();
        CheckListItemDao checkListItemDao = Database.getInstance(this).getCheckListItemDao();
        mountainDao.deleteAll();
        areaDao.deleteAll();
        prefectureDao.deleteAll();
        coordinateDao.deleteAll();
        statusDao.deleteAll();
        checkListItemDao.deleteAll();

        try {
            List<String> prefecturesListRaw = Database.parsePrefectureCSV(this);
            List<Prefecture> prefectureList = new ArrayList<>();
            for (String prefecture : prefecturesListRaw) {
                prefectureList.add(new Prefecture(null, prefecture));
            }
            prefectureDao.insertInTx(prefectureList);

            List<String> areasListRaw = Database.parseAreaCSV(this);
            List<Area> areaList = new ArrayList<>();
            //start from zero to match the specifications (0 = 北海道, etc)
            for (int i = 0; i < areasListRaw.size(); i++) {
                areaList.add(new Area((long) i, areasListRaw.get(i)));
            }
            areaDao.insertInTx(areaList);

            List<String> yids = new ArrayList<>();

            Map<String, Coordinate> coordinateMap = new HashMap<>();
            Map<String, Status> statusMap = new HashMap<>();
            List<Mountain> mountainList = new ArrayList<>();

            List<MountainArrayElement> jsonEntries = Database.parseMountainJSON(this);
            for (MountainArrayElement element : jsonEntries) {
                String key = element.getYid();
                yids.add(key);
                Area area = areaDao.queryBuilder().where(AreaDao.Properties.Id.eq(element.getArea())).unique();
                Prefecture prefecture = prefectureDao.queryBuilder().where(PrefectureDao.Properties.Name.eq(element.getPrefecture().trim())).unique();
                Mountain mountain = new Mountain(null, element.getYid(), element.getTitle(), element.getTitleExt(), element.getTitleEnglish(), element.getKana(),
                        prefecture.getId(), area.getId(), element.getHeight());
                mountainList.add(mountain);
                Coordinate coordinate = new Coordinate(null, (float) element.getCoordinate().getLatitude(), (float) element.getCoordinate().getLongitude());
                coordinateMap.put(key, coordinate);
                Status status = new Status(null, element.getCurrentMountainStatus());
                statusMap.put(key, status);
            }

            mountainDao.insertInTx(mountainList);

            for (String yid : yids) {
                Mountain mountain = mountainDao.queryBuilder().where(MountainDao.Properties.Yid.eq(yid)).unique();
                Long mountainId = mountain.getId();
                Status status = statusMap.get(yid);
                status.setMountainId(mountainId);
                Coordinate coordinate = coordinateMap.get(yid);
                coordinate.setMountainId(mountainId);
            }

            statusDao.insertInTx(statusMap.values());
            coordinateDao.insertInTx(coordinateMap.values());

            List<String> checklistListRaw = Database.parseChecklistCSV(this);
            List<CheckListItem> checkList = new ArrayList<>();
            for (String item : checklistListRaw) {
                checkList.add(new CheckListItem(null, item, false));
            }
            checkListItemDao.insertInTx(checkList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultSettings() {
        Settings settings = new Settings(this);
        settings.setSetting("setting_display_warning", true);
        settings.setSetting("setting_download_mobile", true);
        settings.setSetting("setting_reset_checklist", false);
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
