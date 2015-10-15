package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class JSONDownloader {

    /**
     * Sample code from StackOverflow.
     * Use a similar method to get the JSON file from our server for the user.
     */
    public static String getJsonFromServer() {
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost("http://someJSONUrl/jsonWebService");
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

    private static String readJSONFile(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            while (( line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }
        return stringBuilder.toString();
    }

    /**
     * Temporary while in development
     */
    public static String getMockMountainList(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.mountain_list);
        return readJSONFile(inputStream);
    }

    private static final Map<Long, Integer> forecastJSONFiles;
    static
    {
        forecastJSONFiles = new HashMap<>();
        forecastJSONFiles.put(1L, R.raw.id0001);
        forecastJSONFiles.put(2L, R.raw.id0002);
    }

    @Nullable
    public static String getMockMountainForecast(Context context, long mountainId) {
        Integer forecastJSONFile = forecastJSONFiles.get(mountainId);
        if(forecastJSONFile != null) {
            InputStream inputStream = context.getResources().openRawResource(forecastJSONFile);
            return readJSONFile(inputStream);
        }
        return null;
    }
}
