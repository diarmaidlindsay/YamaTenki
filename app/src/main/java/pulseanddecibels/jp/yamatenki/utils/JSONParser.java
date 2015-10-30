package pulseanddecibels.jp.yamatenki.utils;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

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
        ArrayList<MountainArrayElement> mountains = new ArrayList<>();

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

    @Nullable
    public static MountainForecastJSON parseMountainForecastFromFile(String json) {
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
                ForecastArrayElement element = parseForecast(forecasts.getJSONObject(i));
                String key = DateUtils.timeStampToMapKey(element.getDateTime());
                forecastMap.put(key, element);
            }

            String referenceCity = jsonRoot.optString("referenceCity");
            JSONArray heights = jsonRoot.optJSONArray("heights");
            SparseArray<Integer> heightsMap = new SparseArray<>();
            for (int i = 0; i < heights.length(); i++) {
                JSONObject heightJSONObject = heights.getJSONObject(i);
                heightsMap.append(heightJSONObject.optInt("height"), heightJSONObject.optInt("pressure"));
            }
            String dateTime = jsonRoot.optString("timestamp");

            mountainForecastJSON = new MountainForecastJSON(mountain, forecastMap,
                    referenceCity, heightsMap, dateTime);

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
        int currentMountainIndex = mountain.optInt("currentMountainStatus");

        return new MountainArrayElement(yid, title, titleExt, titleEnglish, kana, coordinate, prefecture,
                area, height, currentMountainIndex);
    }

    private static ForecastArrayElement parseForecast(JSONObject forecast) throws JSONException {
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
        return new ForecastArrayElement(Utils.getTimeStamp(dateTime), mountainStatus,
                windAndTemperaturesList, temperature, precipitation, totalCloudCover);
    }
}
