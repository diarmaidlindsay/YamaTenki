package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastAdapter extends BaseAdapter {

    MountainForecastJSON forecasts;

    final SparseIntArray difficultyArray = new SparseIntArray() {
        {
            append(1, R.drawable.a_grade);
            append(2, R.drawable.b_grade);
            append(3, R.drawable.c_grade);
        }
    };

    final SparseIntArray weatherArray = new SparseIntArray() {
        {
            append(1, R.drawable.a_grade);
            append(2, R.drawable.b_grade);
            append(3, R.drawable.c_grade);
            append(4, R.drawable.c_grade);
            append(5, R.drawable.c_grade);
        }
    };

    private Context mContext;
    private String yid; //Id of the mountain to display the forecast for.
    private LayoutInflater layoutInflater;

    public MountainForecastAdapter(Context mContext, String yid) {
        this.mContext = mContext;
        this.yid = yid;
        initialiseDataSets();
    }

    private void initialiseDataSets() {
        forecasts = JSONParser.parseMountainForecastFromFile(
                JSONDownloader.getMockMountainDetail(mContext, yid));
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.scroll_item_forecast,                                                                                                                        parent, false);
        }

        return convertView;
    }
}
