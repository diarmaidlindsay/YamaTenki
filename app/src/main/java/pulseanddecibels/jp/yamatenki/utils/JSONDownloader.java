package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.interfaces.OnDownloadComplete;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.MountainListJSON;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class JSONDownloader {

    /**
     * Sample code from StackOverflow.
     * Use a similar method to get the JSON file from our server for the user.
     */
    static Context mContext;
    private static OnDownloadComplete mDownloadComplete;

    public static void getMountainListFromServer(Context context) {
        mContext = context;
        DownloadMountainListTask downloadMountainListTask = new DownloadMountainListTask();
        downloadMountainListTask.execute();
    }

    public static void getMountainForecastFromServer(Context context, String yid, OnDownloadComplete downloadComplete) {
        final String MOUNTAIN_FORECAST_URL = "https://yamatenki.pulseanddecibels.jp/1/%s.json";
        mDownloadComplete = downloadComplete;
        String url = String.format(MOUNTAIN_FORECAST_URL, yid);

        mContext = context;
        DownloadMountainForecastTask downloadMountainForecastTask = new DownloadMountainForecastTask();
        downloadMountainForecastTask.execute(url);
    }

    private static String readJSONFile(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return stringBuilder.toString();
    }

    @Nullable
    public static String getMockMountainForecast(Context context, String yid) {
        Integer forecastJSONFile = context.getResources().getIdentifier(yid, "raw", context.getPackageName());
        if (forecastJSONFile != 0) {
            InputStream inputStream = context.getResources().openRawResource(forecastJSONFile);
            return readJSONFile(inputStream);
        }
        return null;
    }

    private static class DownloadMountainListTask extends AsyncTask<Void, Integer, Boolean> {
        final String MOUNTAIN_LIST_URL = "https://yamatenki.pulseanddecibels.jp/1/mountainList.json";
        String etag;

        // Do the long-running work in here
        @Override
        protected Boolean doInBackground(Void... params) {
            String json = downloadJSON(MOUNTAIN_LIST_URL);
            if (json != null) {
                ArrayList<MountainArrayElement> mountainArrayElements = parseJSON(json);
                if (mountainArrayElements.size() > 0) {
                    insertIntoDatabase(mountainArrayElements);
                    //update was successful, so update the etag
                    new Settings(mContext).setEtag(etag);
                    return true;
                }
            }
            return false;
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(mContext, "Mountain List updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "No new data available.", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadJSON(String url) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost(url);
            // Depends on your web service
            httppost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;

            try {
                HttpResponse response = httpclient.execute(httppost);
                etag = response.getFirstHeader("etag").toString();

                if (new Settings(mContext).isNewEtag(etag)) {
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    result = sb.toString();
                }

            } catch (Exception e) {
                Log.e(JSONDownloader.class.getSimpleName(), "Error when downloading JSON from server");
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                    //do nothing
                }
            }
            return result;
        }

        private ArrayList<MountainArrayElement> parseJSON(String mountainListRaw) {
            MountainListJSON mountainListJSON = JSONParser.parseMountainsFromMountainList(mountainListRaw);
            return mountainListJSON.getMountainArrayElements();
        }

        private void insertIntoDatabase(List<MountainArrayElement> mountains) {
            Database.insertMountainList(mContext, mountains);
        }
    }

    private static class DownloadMountainForecastTask extends AsyncTask<String, Integer, Boolean> {
        // Do the long-running work in here
        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String json = downloadJSON(url);
            if (json != null) {
                Log.i(JSONDownloader.class.getSimpleName(), "JSON Downloaded");
                MountainForecastJSON mountainForecastJSON = parseJSON(json);
                if (mountainForecastJSON!= null) {
                    Log.i(JSONDownloader.class.getSimpleName(), "JSON Parsed");
                    insertIntoDatabase(mountainForecastJSON);
                    Log.i(JSONDownloader.class.getSimpleName(), "Inserted into Database");
                    return true;
                }
            }

            Log.e(JSONDownloader.class.getSimpleName(), "Error while downloading, parsing and inserting JSON");
            return false;
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(mContext, "Successfully updated.", Toast.LENGTH_SHORT).show();
                mDownloadComplete.downloadingCompleted(result);
            } else {
                Toast.makeText(mContext, "Failed to update.", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadJSON(String url) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost(url);
            // Depends on your web service
            httppost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;

            try {
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                result = sb.toString();

            } catch (Exception e) {
                Log.e(JSONDownloader.class.getSimpleName(), "Error when downloading JSON from server");
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                    //do nothing
                }
            }
            return result;
        }

        private MountainForecastJSON parseJSON(String mountainForecastRaw) {
            return JSONParser.parseMountainForecast(mountainForecastRaw);
        }

        private void insertIntoDatabase(MountainForecastJSON forecastJSON) {
            Database.insertMountainForecast(mContext, forecastJSON);
        }
    }
}
