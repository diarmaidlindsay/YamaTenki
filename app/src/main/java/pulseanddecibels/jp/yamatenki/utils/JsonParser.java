package pulseanddecibels.jp.yamatenki.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pulseanddecibels.jp.yamatenki.model.CoordinateElement;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainListJSON;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 *
 * Parse the JSON Data containing weather forecasts
 */
public class JSONParser {
    public static MountainListJSON parseMountainsFromMountainList(String json) {
        MountainListJSON mountainListJSON = new MountainListJSON();
        ArrayList<MountainArrayElement> mountains = new ArrayList<>();

        try {
            JSONObject jsonRoot = new JSONObject(json);
            JSONArray jsonArray = jsonRoot.optJSONArray("list");

            for(int i = 0; i < jsonArray.length(); i++) {
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

    public static MountainForecastJSON parseMountainForecastFromFile(String json) {
        MountainForecastJSON mountainForecastJSON = new MountainForecastJSON();

        try {
            JSONObject jsonRoot = new JSONObject(json);

            JSONObject mountainObject = jsonRoot.optJSONObject("mountain");
            MountainArrayElement mountain = parseMountain(mountainObject);

            JSONArray forecasts = jsonRoot.optJSONArray("forecasts");
            List<ForecastArrayElement> longTermForecastList = new ArrayList<>();
            //It will have 9-16 ForecastArrayElement objects according to the design
            for(int i = 0; i < forecasts.length(); i++) {
                longTermForecastList.add(parseForecast(forecasts.getJSONObject(i)));
            }

            JSONArray forecastsDaily = jsonRoot.optJSONArray("forecastsDaily");
            List<ForecastArrayElement> shortTermForecastList = new ArrayList<>();
            //It will have 9-16 ForecastArrayElement objects according to the design
            for(int i = 0; i < forecastsDaily.length(); i++) {
                shortTermForecastList.add(parseForecast(forecastsDaily.getJSONObject(i)));
            }

            String referenceCity = jsonRoot.optString("referenceCity");
            JSONArray heights = jsonRoot.optJSONArray("heights");
            List<Integer> heightsList = new ArrayList<>();
            for(int i = 0; i < heights.length(); i++) {
                heightsList.add(heights.getInt(0));
            }
            String dateTime = jsonRoot.optString("timestamp");

            mountainForecastJSON = new MountainForecastJSON(mountain, longTermForecastList, shortTermForecastList,
                    referenceCity, heightsList, dateTime);

        } catch (JSONException e) {
            Log.e(JSONParser.class.getSimpleName(), "Error while parsing Mountain Forecast JSON");
            e.printStackTrace();
        }

        return mountainForecastJSON;
    }

    /*
     * Sample json :
     *
        "yid" : "ID0001",
		"title" : "富士山富士宮口５合目",
		"kana" : "ふじさんふじみやぐちごごうめ",
		"coordinate" : {
			"lat" : 40.6935133,
			"lng" : 140.9367265
		},
		"area" : 4,
		"height" : 2318,
		"currentMountainIndex" : 1
     */
    private static MountainArrayElement parseMountain(JSONObject mountain) throws JSONException {
        String yid = mountain.optString("yid");
        String title = mountain.optString("title");
        String kana = mountain.optString("kana");
        JSONObject coordinateObject = mountain.getJSONObject("coordinate");
        long latitude = coordinateObject.optLong("lat");
        long longitude = coordinateObject.optLong("lng");
        CoordinateElement coordinate = new CoordinateElement(latitude, longitude);
        int area = mountain.optInt("area");
        int height = mountain.optInt("height");
        int currentMountainIndex = mountain.optInt("currentMountainIndex");

        return new MountainArrayElement(yid, title, kana, coordinate, area, height, currentMountainIndex);
    }

    /*
     * Sample json :
        // (long term forecast type)
        "dateTime" : "2015-09-18T06:00:00.+09:00",
        "mountainIndex" : 1,
        "windAndTemperatures" : [
            {
                "temperature" : 7,
                "windVelocity" : 12,
                "windDirection" : 1
            },
            {
                "temperature" : 13,
                "windVelocity" : 9,
                "windDirection" : 2
            }
        ],
        "weather" : 1,
        "temperature" : 22,
        "precipitation" : 0

        //or
        // (short term forecast type)

        "dateTime" : "2015-09-20T00:00:00.+09:00",
        "mountainIndex" : 1,
        "windAndTemperatures" : [
            {
                "temperature" : 7,
                "windVelocity" : 6,
                "windDirection" : 2
            },
            {
                "temperature" : 12,
                "windVelocity" : 1,
                "windDirection" : 0
            }
        ],
        "weather" : 0,
        "temperatureHigh" : 28,
        "temperatureLow" : 19,
        "precipitation" : 10
     */
    private static ForecastArrayElement parseForecast(JSONObject forecast) throws JSONException {
        String dateTime = forecast.optString("dateTime");
        int mountainIndex = forecast.optInt("mountainIndex");
        List<WindAndTemperatureElement> windAndTemperaturesList = new ArrayList<>();
        JSONArray windAndTemperatures = forecast.optJSONArray("windAndTemperatures");

        for(int j = 0; j < windAndTemperatures.length(); j++) {
            JSONObject windAndTemperature = windAndTemperatures.getJSONObject(j);
            int temperature = windAndTemperature.optInt("temperature");
            int windVelocity = windAndTemperature.optInt("windVelocity");
            int windDirection = windAndTemperature.optInt("windDirection");
            windAndTemperaturesList.add(new WindAndTemperatureElement(temperature, windVelocity, windDirection));
        }

        float precipitation = (float) forecast.optDouble("precipitation");

        int weather = forecast.optInt("weather");
        if(forecast.has("temperature")) {
            Integer temperature = forecast.has("temperature") ? forecast.optInt("temperature") : null;
            return new ForecastArrayElement(Utils.getTimeStamp(dateTime),mountainIndex,
                    windAndTemperaturesList, weather, temperature, precipitation);
        } else {
            Integer temperatureHigh = forecast.has("temperatureHigh") ? forecast.optInt("temperatureHigh") : null;
            Integer temperatureLow = forecast.has("temperatureLow") ? forecast.optInt("temperatureLow") : null;
            return new ForecastArrayElement(Utils.getTimeStamp(dateTime),mountainIndex,
                    windAndTemperaturesList, weather, temperatureHigh, temperatureLow, precipitation);
        }
    }
}
