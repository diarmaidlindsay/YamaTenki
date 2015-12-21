package pulseanddecibels.jp.yamatenki.utils;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.model.CoordinateElement;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.MountainListJSON;
import pulseanddecibels.jp.yamatenki.model.MountainStatusJSON;
import pulseanddecibels.jp.yamatenki.model.StatusArrayElement;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Parse the JSON Data containing weather forecasts
 */
public class JSONParser {
    public static MountainListJSON parseMountainsFromMountainList(String json) {
        MountainListJSON mountainListJSON = new MountainListJSON();
        List<MountainArrayElement> mountains = new ArrayList<>();

        try {
            JSONObject jsonRoot = new JSONObject(json);
            JSONArray jsonArray = jsonRoot.optJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mountain = jsonArray.getJSONObject(i);
                mountains.add(parseMountain(mountain));
            }

            String timestamp = jsonRoot.optString("timestamp");
            mountainListJSON = new MountainListJSON(mountains, timestamp);

        } catch (JSONException e) {
            Log.e(JSONParser.class.getSimpleName(), "Error while parsing Mountain List JSON");
            e.printStackTrace();
        }

        return mountainListJSON;
    }

    public static MountainStatusJSON parseStatusFromMountainStatus(String json) {
        MountainStatusJSON mountainStatusJSON = new MountainStatusJSON();
        List<StatusArrayElement> statusList = new ArrayList<>();

        try {
            JSONObject jsonRoot = new JSONObject(json);
            JSONArray jsonArray = jsonRoot.optJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject status = jsonArray.getJSONObject(i);
                statusList.add(parseStatus(status));
            }

            mountainStatusJSON = new MountainStatusJSON(statusList);
        }
        catch (JSONException e) {
                Log.e(JSONParser.class.getSimpleName(), "Error while parsing Mountain Status JSON");
                e.printStackTrace();
            }

        return mountainStatusJSON;
    }

    @Nullable
    public static MountainForecastJSON parseMountainForecast(String json) {
        MountainForecastJSON mountainForecastJSON = new MountainForecastJSON();
        if (json == null) {
            return null;
        }
        try {
            JSONObject jsonRoot = new JSONObject(json);

            JSONObject mountainObject = jsonRoot.optJSONObject("mountain");
            MountainArrayElement mountain = parseMountain(mountainObject);

            JSONArray forecasts = jsonRoot.optJSONArray("forecasts");
            Map<String, ForecastArrayElement> forecastMap = new HashMap<>();
            for (int i = 0; i < forecasts.length(); i++) {
                ForecastArrayElement element = parseForecast(forecasts.getJSONObject(i), false);
                String key = DateUtils.getDateToShortTermForecastKey(element.getDateTime());
                forecastMap.put(key, element);
            }

            JSONArray forecastsDaily = jsonRoot.optJSONArray("forecastsDaily");
            for (int i = 0; i < forecastsDaily.length(); i++) {
                ForecastArrayElement element = parseForecast(forecastsDaily.getJSONObject(i), true);
                String key = DateUtils.getDateToShortTermForecastKey(element.getDateTime());
                forecastMap.put(key, element);
            }

            String referenceCity = jsonRoot.optString("referenceCity");
            JSONArray heights = jsonRoot.optJSONArray("heights");
            SparseIntArray heightsMap = new SparseIntArray();
            for (int i = 0; i < heights.length(); i++) {
                JSONObject heightJSONObject = heights.getJSONObject(i);
                heightsMap.append(heightJSONObject.optInt("height"), heightJSONObject.optInt("pressure"));
            }
            String dateTime = jsonRoot.optString("timestamp");
            int currentMountainForecast = jsonRoot.optInt("currentMountainForecast");

            mountainForecastJSON = new MountainForecastJSON(mountain, forecastMap,
                    referenceCity, heightsMap, dateTime, currentMountainForecast);

        } catch (JSONException e) {
            Log.e(JSONParser.class.getSimpleName(), "Error while parsing Mountain Forecast JSON");
            e.printStackTrace();
        }

        return mountainForecastJSON;
    }

    private static MountainArrayElement parseMountain(JSONObject mountain) throws JSONException {
        String yid = mountain.optString("yid");
        String title = mountain.optString("title");
        String titleExt = mountain.optString("titleExt");
        String titleEnglish = mountain.optString("titleEnglish");
        String kana = mountain.optString("kana");
        JSONObject coordinateObject = mountain.getJSONObject("coordinate");
        double latitude = coordinateObject.optDouble("lat");
        double longitude = coordinateObject.optDouble("lng");
        CoordinateElement coordinate = new CoordinateElement(latitude, longitude);
        String prefecture = mountain.optString("prefecture");
        int area = mountain.optInt("area");
        int height = mountain.optInt("height");
        int topMountain = mountain.optInt("topMountain");

        return new MountainArrayElement(yid, title, titleExt, titleEnglish, kana, coordinate, prefecture,
                area, height, topMountain);
    }

    private static StatusArrayElement parseStatus(JSONObject status) throws JSONException {
        String yid = status.optString("yid");
        Integer cms = status.optInt("cms");

        return new StatusArrayElement(yid, cms);
    }

    private static ForecastArrayElement parseForecast(JSONObject forecast, boolean daily) throws JSONException {
        String dateTime = forecast.optString("dateTime");
        int mountainStatus = forecast.optInt("mountainStatus");
        List<WindAndTemperatureElement> windAndTemperaturesList = new ArrayList<>();
        JSONArray windAndTemperatures = forecast.optJSONArray("windAndTemperatures");

        for (int j = 0; j < windAndTemperatures.length(); j++) {
            JSONObject windAndTemperature = windAndTemperatures.getJSONObject(j);
            int temperature = windAndTemperature.optInt("temperature");
            int windVelocity = windAndTemperature.optInt("windVelocity");
            int windDirection = windAndTemperature.optInt("windDirection");
            windAndTemperaturesList.add(new WindAndTemperatureElement(temperature, windVelocity, windDirection));
        }

        float precipitation = (float) forecast.optDouble("precipitation");
        Integer temperature = forecast.has("temperature") ? forecast.optInt("temperature") : null;
        int totalCloudCover = forecast.optInt("totalCloudCover");
        return new ForecastArrayElement(dateTime, mountainStatus,
                windAndTemperaturesList, temperature, precipitation, totalCloudCover, daily);
    }
}
