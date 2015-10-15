package pulseanddecibels.jp.yamatenki.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pulseanddecibels.jp.yamatenki.database.dao.DaoMaster;
import pulseanddecibels.jp.yamatenki.database.dao.DaoSession;
import pulseanddecibels.jp.yamatenki.model.MountainListCSVEntry;
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
            //TODO : DevOpenHelper is only for DEVELOPMENT. Switch to OpenHelper for production.
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "yama-tenki.db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static List<MountainListCSVEntry> parseMountainCSV(Context context) throws IOException {
        List<MountainListCSVEntry> csvEntries = new ArrayList<>();

        InputStream inputStream = context.getAssets().open("databases/mountainList.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String csvSplitBy = ",";

        while((line = br.readLine()) != null) {
            String[] row = line.split(csvSplitBy, 10);
            //first line is header, we should ignore
            if(row.length == 10 && Utils.isNumeric(row[4].trim())) {
                Integer height = null;
                Float latitude = null;
                Float longitude = null;
                try {
                    height = Integer.parseInt(row[4].trim());
                    latitude = Float.parseFloat(row[7]);
                    longitude = Float.parseFloat(row[8]);
                } catch (NumberFormatException e) {
                    //do nothing
                }
                csvEntries.add(new MountainListCSVEntry(row[0], row[1], row[2], row[3], height,
                        row[5], row[6], latitude, longitude, row[9]));
            }
        }

        return csvEntries;
    }

    public static List<String> parsePrefectureCSV(Context context) throws IOException {
        return parseCSV(context, "databases/prefectureList.csv");
    }

    public static List<String> parseAreaCSV(Context context) throws IOException {
        return parseCSV(context, "databases/areaList.csv");
    }

    private static List<String> parseCSV(Context context, String path) throws IOException {
        List<String> csvEntries = new ArrayList<>();

        InputStream inputStream = context.getAssets().open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while((line = br.readLine()) != null) {
            csvEntries.add(line);
        }

        return csvEntries;
    }
}
