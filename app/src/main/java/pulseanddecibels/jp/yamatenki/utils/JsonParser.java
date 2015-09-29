package pulseanddecibels.jp.yamatenki.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pulseanddecibels.jp.yamatenki.model.Coordinate;
import pulseanddecibels.jp.yamatenki.model.MountainListItem;
import pulseanddecibels.jp.yamatenki.model.MountainListJson;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class JsonParser {
    public static MountainListJson parseMountainsFromMountainList(String json) {
        MountainListJson mountainListJson = new MountainListJson();
        ArrayList<MountainListItem> mountains = new ArrayList<>();

        try {
            JSONObject jsonRoot = new JSONObject(json);
            JSONArray jsonArray = jsonRoot.optJSONArray("list");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject mountain = jsonArray.getJSONObject(i);

                String yid = mountain.optString("yid");
                String title = mountain.optString("title");
                String kana = mountain.optString("kana");
                JSONObject coordinateObject = mountain.getJSONObject("coordinate");
                long latitude = coordinateObject.optLong("lat");
                long longitude = coordinateObject.optLong("lng");
                Coordinate coordinate = new Coordinate(latitude, longitude);
                int area = mountain.optInt("area");
                int height = mountain.optInt("height");
                int currentMountainIndex = mountain.optInt("currentMountainIndex");

                MountainListItem mountainListItem =
                        new MountainListItem(yid, title, kana, coordinate, area, height, currentMountainIndex);

                mountains.add(mountainListItem);
            }

            String timestamp = jsonRoot.optString("timestamp");

            mountainListJson = new MountainListJson(mountains, timestamp);

        } catch (JSONException e) {
            Log.e(JsonParser.class.getSimpleName(), "Error while parsing Mountain List JSON");
            e.printStackTrace();
        }

        return mountainListJson;
    }
}
