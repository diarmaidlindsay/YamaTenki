package pulseanddecibels.jp.yamatenki.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.ETag;
import pulseanddecibels.jp.yamatenki.database.dao.ETagDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.interfaces.OnDownloadComplete;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.MountainListJSON;
import pulseanddecibels.jp.yamatenki.model.MountainStatusJSON;
import pulseanddecibels.jp.yamatenki.model.StatusArrayElement;

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

    public static void getMountainStatusFromServer(Context context) {
        mContext = context;
        DownloadMountainStatusTask downloadMountainStatusTask = new DownloadMountainStatusTask();
        downloadMountainStatusTask.execute();
    }

    public static void getMountainForecastFromServer(Context context, String yid, OnDownloadComplete downloadComplete) {
        mDownloadComplete = downloadComplete;
        mContext = context;
        DownloadMountainForecastTask downloadMountainForecastTask = new DownloadMountainForecastTask();
        downloadMountainForecastTask.execute(yid);
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
    @Deprecated
    public static String getMockMountainForecast(Context context, String yid) {
        Integer forecastJSONFile = context.getResources().getIdentifier(yid, "raw", context.getPackageName());
        if (forecastJSONFile != 0) {
            InputStream inputStream = context.getResources().openRawResource(forecastJSONFile);
            return readJSONFile(inputStream);
        }
        return null;
    }

    private static class DownloadMountainStatusTask extends ASyncTaskWithProgress {
        String etag;

        @Override
        protected Boolean doInBackground(String... params) {
            String json = downloadJSON(getURL());
            if (json != null) {
                publishProgress(33);
                List<StatusArrayElement> statusArrayElements = parseJSON(json);
                if (statusArrayElements.size() > 0) {
                    publishProgress(66);
                    insertIntoDatabase(statusArrayElements);
                    publishProgress(100);
                    //update was successful, so update the existingETag
                    new Settings(mContext).setStatusEtag(etag);
                    return true;
                }
            }
            return false;
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
                if (new Settings(mContext).isNewStatusETag(etag)) {
                    publishProgress(0);
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
                Log.e(JSONDownloader.class.getSimpleName(), "Error when downloading Status JSON from server");
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                    //do nothing
                }
            }
            return result;
        }

        @Override
        protected String getDownloadMessage() {
            return mContext.getString(R.string.dialog_downloading_status);
        }

        @Override
        protected String getURL() {
            return "https://yamatenki.pulseanddecibels.jp/1/mountainStatus.json";
        }

        private List<StatusArrayElement> parseJSON(String statusListRaw) {
            MountainStatusJSON mountainStatusJSON = JSONParser.parseStatusFromMountainStatus(statusListRaw);
            return mountainStatusJSON.getList();
        }

        private void insertIntoDatabase(List<StatusArrayElement> statusList) {
            Database.insertMountainStatusList(mContext, statusList);
        }
    }

    private static class DownloadMountainListTask extends ASyncTaskWithProgress {
        String etag;

        @Override
        protected Boolean doInBackground(String... params) {
            String json = downloadJSON(getURL());
            if (json != null) {
                publishProgress(33);
                List<MountainArrayElement> mountainArrayElements = parseJSON(json);
                if (mountainArrayElements.size() > 0) {
                    publishProgress(66);
                    insertIntoDatabase(mountainArrayElements);
                    publishProgress(100);
                    //update was successful, so update the existingETag
                    new Settings(mContext).setListEtag(etag);
                    return true;
                }
            }
            return false;
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
                if (new Settings(mContext).isNewListEtag(etag)) {
                    publishProgress(0);
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
                Log.e(JSONDownloader.class.getSimpleName(), "Error when downloading Mountain List JSON from server");
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                    //do nothing
                }
            }
            return result;
        }

        private List<MountainArrayElement> parseJSON(String mountainListRaw) {
            MountainListJSON mountainListJSON = JSONParser.parseMountainsFromMountainList(mountainListRaw);
            return mountainListJSON.getMountainArrayElements();
        }

        private void insertIntoDatabase(List<MountainArrayElement> mountains) {
            Database.insertMountainList(mContext, mountains);
        }

        @Override
        protected String getDownloadMessage() {
            return mContext.getString(R.string.dialog_downloading_list);
        }

        @Override
        protected String getURL() {
            return "https://yamatenki.pulseanddecibels.jp/1/mountainList.json";
        }
    }

    private static class DownloadMountainForecastTask extends ASyncTaskWithProgress {
        Mountain mountain;
        ETag existingETag;
        // Do the long-running work in here
        @Override
        protected Boolean doInBackground(String... params) {
            String yid = params[0];
            String url = String.format(getURL(), yid);
            existingETag = getExistingEtag(yid);

            String json = downloadJSON(url);
            if (json != null) {
                publishProgress(33);
                Log.i(JSONDownloader.class.getSimpleName(), "JSON "+yid+" Downloaded");
                MountainForecastJSON mountainForecastJSON = parseJSON(json);
                if (mountainForecastJSON!= null) {
                    Log.i(JSONDownloader.class.getSimpleName(), "JSON Parsed");
                    publishProgress(66);
                    insertIntoDatabase(mountainForecastJSON);
                    publishProgress(100);
                    Log.i(JSONDownloader.class.getSimpleName(), "Inserted into Database");
                    return true;
                }
            } else {
                Log.i(JSONDownloader.class.getSimpleName(), "Using cached copy of Data");
                return true;
            }

            Log.e(JSONDownloader.class.getSimpleName(), "Error while downloading, parsing and inserting JSON");
            return false;
        }

        @Nullable
        private ETag getExistingEtag(String yid) {
            MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
            mountain = mountainDao.queryBuilder().where(MountainDao.Properties.Yid.eq(yid)).unique();
            if(mountain != null) {
                return mountain.getETag();
            }

            return null;
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                Log.i(JSONDownloader.class.getSimpleName(), "Successfully Updated Forecast.");
            } else {
                Log.e(JSONDownloader.class.getSimpleName(), "Failed to Update Forecast");
            }

            mDownloadComplete.downloadingCompleted(result);
        }

        @Override
        protected String getDownloadMessage() {
            return mContext.getString(R.string.dialog_updating_forecast);
        }

        @Override
        protected String getURL() {
            return "https://yamatenki.pulseanddecibels.jp/1/%s.json";
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
                String serverEtag = response.getFirstHeader("etag").toString();
                if(existingETag == null || !existingETag.getEtag().equals(serverEtag)) {
                    publishProgress(0);
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

                    ETagDao eTagDao = Database.getInstance(mContext).getETagDao();
                    if(existingETag == null) {
                        Log.i(JSONDownloader.class.getSimpleName(), "Inserting new ETag");
                        eTagDao.insert(new ETag(null, serverEtag, mountain.getId()));

                    } else {
                        Log.i(JSONDownloader.class.getSimpleName(), "Updating Existing ETag");
                        existingETag.setEtag(serverEtag);
                        eTagDao.update(existingETag);
                    }
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

        private MountainForecastJSON parseJSON(String mountainForecastRaw) {
            return JSONParser.parseMountainForecast(mountainForecastRaw);
        }

        private void insertIntoDatabase(MountainForecastJSON forecastJSON) {
            Database.insertMountainForecast(mContext, forecastJSON);
        }
    }

    private static abstract class ASyncTaskWithProgress extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            //change color of progress bar
            progressDialog.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar));
            progressDialog.setMessage(getDownloadMessage());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(!progressDialog.isShowing()) {
                progressDialog.show();
                TextView message = (TextView) progressDialog.findViewById(android.R.id.message);
                //change color of progress text
                message.setTextColor(ContextCompat.getColor(mContext, R.color.yama_background));
            }
            progressDialog.setProgress(values[0]);
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Boolean result) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        protected abstract String getDownloadMessage();
        protected abstract String getURL();
    }
}
