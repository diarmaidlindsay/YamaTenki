package pulseanddecibels.jp.yamatenki.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pulseanddecibels.jp.yamatenki.database.dao.Area;
import pulseanddecibels.jp.yamatenki.database.dao.AreaDao;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItem;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItemDao;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.CoordinateDao;
import pulseanddecibels.jp.yamatenki.database.dao.DaoMaster;
import pulseanddecibels.jp.yamatenki.database.dao.DaoSession;
import pulseanddecibels.jp.yamatenki.database.dao.Forecast;
import pulseanddecibels.jp.yamatenki.database.dao.ForecastDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.Prefecture;
import pulseanddecibels.jp.yamatenki.database.dao.PrefectureDao;
import pulseanddecibels.jp.yamatenki.database.dao.Pressure;
import pulseanddecibels.jp.yamatenki.database.dao.PressureDao;
import pulseanddecibels.jp.yamatenki.database.dao.Status;
import pulseanddecibels.jp.yamatenki.database.dao.StatusDao;
import pulseanddecibels.jp.yamatenki.database.dao.WindAndTemperature;
import pulseanddecibels.jp.yamatenki.database.dao.WindAndTemperatureDao;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.StatusArrayElement;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/10/14.
 * Copyright Pulse and Decibels 2015
 */
public class Database {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static SQLiteDatabase db;

    private Database() {

    }

    public static DaoSession getInstance(Context context) {
        if (daoSession == null) {
            //TODO : DevOpenHelper is only for DEVELOPMENT. Switch to MyOpenHelper for production.
//            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "yama-tenki.db", null);
            MyOpenHelper helper = new MyOpenHelper(context, "yama-tenki.db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static class MyOpenHelper extends DaoMaster.OpenHelper {

        Context mContext;

        public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
            mContext = context;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // if we need to add new columns or tables or do data migration in later versions of Yama Tenki, the code should go here
            if(oldVersion < 4) {
                //add Reference City to Forecast
                try {
                    db.execSQL("ALTER TABLE 'MOUNTAIN' ADD 'REFERENCE_CITY' TEXT");
                } catch (SQLException e) {
                    Log.v("Database.onUpgrade", "Reference City was already added to Database, not adding again.");
                }
                //force re-downloading of mountain list after adding REFERENCE_CITY
                new Settings(mContext).setListEtag("");
            }
        }
    }

    public static List<String> parsePrefectureCSV(Context context) throws IOException {
        return parseCSV(context, "databases/prefectureList.csv");
    }

    public static List<String> parseAreaCSV(Context context) throws IOException {
        return parseCSV(context, "databases/areaList.csv");
    }

    public static List<String> parseChecklistCSV(Context context) throws IOException {
        return parseCSV(context, Utils.isEnglishLocale(context) ?
                "databases/initialChecklist_en.csv" :
                "databases/initialChecklist_jp.csv");
    }

    private static List<String> parseCSV(Context context, String path) throws IOException {
        List<String> csvEntries = new ArrayList<>();

        InputStream inputStream = context.getAssets().open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = br.readLine()) != null) {
            csvEntries.add(line);
        }

        return csvEntries;
    }

    public static void initialiseData(Context context) {
        AreaDao areaDao = Database.getInstance(context).getAreaDao();
        PrefectureDao prefectureDao = Database.getInstance(context).getPrefectureDao();
        CheckListItemDao checkListItemDao = Database.getInstance(context).getCheckListItemDao();

        areaDao.deleteAll();
        prefectureDao.deleteAll();
        checkListItemDao.deleteAll();

        try {
            List<String> prefecturesListRaw = Database.parsePrefectureCSV(context);
            List<Prefecture> prefectureList = new ArrayList<>();
            for (String prefecture : prefecturesListRaw) {
                prefectureList.add(new Prefecture(null, prefecture));
            }
            prefectureDao.insertInTx(prefectureList);

            List<String> areasListRaw = Database.parseAreaCSV(context);
            List<Area> areaList = new ArrayList<>();
            //start from zero to match the specifications (0 = 北海道, etc)
            for (int i = 0; i < areasListRaw.size(); i++) {
                areaList.add(new Area((long) i, areasListRaw.get(i)));
            }
            areaDao.insertInTx(areaList);

            List<String> checklistListRaw = Database.parseChecklistCSV(context);
            List<CheckListItem> checkList = new ArrayList<>();
            for (String item : checklistListRaw) {
                checkList.add(new CheckListItem(null, item, false));
            }
            checkListItemDao.insertInTx(checkList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertMountainList(Context context, List<MountainArrayElement> jsonEntries) {
        MountainDao mountainDao = Database.getInstance(context).getMountainDao();
        CoordinateDao coordinateDao = Database.getInstance(context).getCoordinateDao();
        AreaDao areaDao = Database.getInstance(context).getAreaDao();
        PrefectureDao prefectureDao = Database.getInstance(context).getPrefectureDao();
        mountainDao.deleteAll();
        coordinateDao.deleteAll();

        List<String> yids = new ArrayList<>();

        Map<String, Coordinate> coordinateMap = new HashMap<>();
        List<Mountain> mountainList = new ArrayList<>();

        for (MountainArrayElement element : jsonEntries) {
            String key = element.getYid();
            yids.add(key);
            Area area = areaDao.queryBuilder().where(AreaDao.Properties.Id.eq(element.getArea())).unique();
            Prefecture prefecture = prefectureDao.queryBuilder().where(PrefectureDao.Properties.Name.eq(element.getPrefecture().trim())).unique();
            Mountain mountain = new Mountain(null, element.getYid(), element.getTitle(), element.getTitleExt(), element.getTitleEnglish(), element.getKana(), element.getReferenceCity(),
                    prefecture.getId(), area.getId(), element.getHeight(), element.getTopMountain() == 1);
            mountainList.add(mountain);
            Coordinate coordinate = new Coordinate(null, (float) element.getCoordinate().getLatitude(), (float) element.getCoordinate().getLongitude());
            coordinateMap.put(key, coordinate);
        }

        mountainDao.insertInTx(mountainList);

        for (String yid : yids) {
            Mountain mountain = mountainDao.queryBuilder().where(MountainDao.Properties.Yid.eq(yid)).unique();
            Long mountainId = mountain.getId();
            Coordinate coordinate = coordinateMap.get(yid);
            coordinate.setMountainId(mountainId);
        }

        coordinateDao.insertInTx(coordinateMap.values());
    }

    public static void insertMountainStatusList(Context context, List<StatusArrayElement> jsonEntries) {
        StatusDao statusDao = Database.getInstance(context).getStatusDao();
        MountainDao mountainDao = Database.getInstance(context).getMountainDao();

        statusDao.deleteAll();
        //status json just has yid, but we need to find mountainId for all yids to make our status objects in database

        Map<String, Long> yidToIdMap = new HashMap<>();
        List<Mountain> allMountains = mountainDao.loadAll();
        for(Mountain mountain : allMountains) {
            yidToIdMap.put(mountain.getYid(), mountain.getId());
        }

        List<Status> statusList = new ArrayList<>();
        for(StatusArrayElement element : jsonEntries) {
            statusList.add(new Status(null, yidToIdMap.get(element.getYid()), element.getCms()));
        }

        statusDao.insertInTx(statusList);
    }

    public static void insertMountainForecast(Context context, MountainForecastJSON forecastJSON) {
        MountainDao mountainDao = Database.getInstance(context).getMountainDao();
        final ForecastDao forecastDao = Database.getInstance(context).getForecastDao();
        WindAndTemperatureDao windAndTemperatureDao = Database.getInstance(context).getWindAndTemperatureDao();
        PressureDao pressureDao = Database.getInstance(context).getPressureDao();

        MountainArrayElement mountainArrayElement = forecastJSON.getMountainArrayElement();
        String yid = mountainArrayElement.getYid();
        Mountain mountain = mountainDao.queryBuilder().where(MountainDao.Properties.Yid.eq(yid)).unique();
        long mountainId = mountain.getId();

        //delete existing forecasts and wind/temperatures
        List<Forecast> existingForecasts = mountain.getForecastList();
        for(Forecast forecast : existingForecasts) {
            List<WindAndTemperature> existingWindAndTemperatures = forecast.getWindAndTemperatureList();
            windAndTemperatureDao.deleteInTx(existingWindAndTemperatures);
        }
        forecastDao.deleteInTx(existingForecasts);

        //delete existing heights/pressures
        List<Pressure> existingPressures = mountain.getPressureList();
        pressureDao.deleteInTx(existingPressures);

        Map<String, ForecastArrayElement> forecastMap = forecastJSON.getForecasts();
        Set<String> timestamps = forecastMap.keySet();

        final List<Forecast> newForecasts = new ArrayList<>();

        for (String dateTime : timestamps) {
            ForecastArrayElement element = forecastMap.get(dateTime);
            Double outerTemperature = element.getTemperature();
            Double precipitation = element.getPrecipitation();
            Double totalCloudCover = element.getTotalCloudCover();
            Integer mountainStatus = element.getMountainStatus();
            String timeStamp = element.getTimeStamp(); //should be same as "dateTime"
            boolean daily = element.isDaily();
            Forecast forecast = new Forecast(null, outerTemperature, precipitation, totalCloudCover, mountainStatus, timeStamp, daily, mountainId);
            newForecasts.add(forecast);
        }

        forecastDao.insertInTx(newForecasts);

        //after the forecasts get assigned ids from the database, retrieve them and bind them to their WindAndTemperatures
        List<Forecast> forecastList = forecastDao.queryBuilder().where(ForecastDao.Properties.MountainId.eq(mountainId)).list();
        Map<String, Long> forecastIdsByDateTime = new HashMap<>();

        for(Forecast forecast : forecastList) {
            forecastIdsByDateTime.put(forecast.getDateTime(), forecast.getId());
        }

        for (String dateTime : timestamps) {
            ForecastArrayElement element = forecastMap.get(dateTime);
            String timeStamp = element.getTimeStamp();
            Long forecastId = forecastIdsByDateTime.get(timeStamp);
            List<WindAndTemperature> windAndTemperatures = new ArrayList<>();
            List<WindAndTemperatureElement> windAndTemperatureElements = element.getWindAndTemperatures();
            for(int i=0; i < windAndTemperatureElements.size(); i++) {

                WindAndTemperatureElement windAndTemperatureElement = windAndTemperatureElements.get(i);
                Integer height = i;
                Double temperature = windAndTemperatureElement.getTemperature();
                Double windVelocity = windAndTemperatureElement.getWindVelocity();
                Double windDirection = windAndTemperatureElement.getWindDirection();

                WindAndTemperature windAndTemperature = new WindAndTemperature(null, height, temperature, windVelocity, windDirection, forecastId);
                windAndTemperatures.add(windAndTemperature);
            }
            windAndTemperatureDao.insertInTx(windAndTemperatures);
        }

        List<Pressure> pressureList = new ArrayList<>();
        SparseIntArray heightsPressures = forecastJSON.getHeights();
        for(int i = 0; i < heightsPressures.size(); i++) {
            int height = heightsPressures.keyAt(i);
            // get the object by the key.
            int pressure = heightsPressures.get(height);
            pressureList.add(new Pressure(null, height, pressure, mountainId));
        }
        pressureDao.insertInTx(pressureList);
    }
}
