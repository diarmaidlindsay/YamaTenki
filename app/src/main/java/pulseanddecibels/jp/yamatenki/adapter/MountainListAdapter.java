package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.activity.MountainForecastActivity;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainListJSON;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListAdapter extends BaseAdapter {

    private List<MountainArrayElement> mountainList = new ArrayList<>();
    final SparseIntArray difficultyArray = new SparseIntArray() {
        {
            append(1, R.drawable.a_difficulty_small);
            append(2, R.drawable.b_difficulty_small);
            append(3, R.drawable.c_difficulty_small);
        }
    };
    private Context mContext;
    private LayoutInflater layoutInflater;

    public MountainListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        initialiseDataSets();
    }

    private void initialiseDataSets() {
        String json = JSONDownloader.getMockMountainList(mContext);
        //String json = JSONDownloader.getJsonFromServer(); // Future way
        //Will be stored in database eventually, in future will get from datasource
        MountainListJSON mountainListJSON = JSONParser.parseMountainsFromMountainList(json);
        mountainList = mountainListJSON.getMountainArrayElements();
    }

    @Override
    public int getCount() {
        return mountainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mountainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Temporary until real difficulty data is available
     * Return random difficulty between 1 and 3
     */
    private int getRandomDifficulty() {
        final int MIN = 1;
        final int MAX = 3;
        return MIN + (int)(Math.random() * ((MAX - MIN) + 1));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolderItem();
            convertView = layoutInflater.inflate(R.layout.list_item_mountain, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.mountain_list_name);
            viewHolder.difficulty = (ImageView) convertView.findViewById(R.id.mountain_list_difficulty);
            viewHolder.height = (TextView) convertView.findViewById(R.id.mountain_list_height);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //check first bit, if set then it is an odd number. We'll give alternate rows different backgrounds
        if((position % 2) == 0) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.table_bg_alt));
        }
        MountainArrayElement mountainArrayElement = (MountainArrayElement) getItem(position);
        viewHolder.name.setText(mountainArrayElement.getTitle());
        final String yid = mountainArrayElement.getYid();
        final int difficulty = getRandomDifficulty(); //TODO : Lookup from current weather forecast (for now make random)
        viewHolder.difficulty.setImageResource(difficultyArray.get(difficulty));
        viewHolder.height.setText(String.format("%sm", String.valueOf(mountainArrayElement.getHeight())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MountainForecastActivity.class);
                intent.putExtra("yid", yid);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolderItem {
        TextView name;
        ImageView difficulty;
        TextView height;
    }
}
