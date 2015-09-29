package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
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
import pulseanddecibels.jp.yamatenki.model.MountainListItem;
import pulseanddecibels.jp.yamatenki.model.MountainListJson;
import pulseanddecibels.jp.yamatenki.utils.JsonDownloader;
import pulseanddecibels.jp.yamatenki.utils.JsonParser;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListAdapter extends BaseAdapter {

    private List<MountainListItem> mountainList = new ArrayList<>();
    final SparseIntArray difficultyArray = new SparseIntArray() {
        {
            append(1, R.drawable.a_grade);
            append(2, R.drawable.b_grade);
            append(3, R.drawable.c_grade);
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
        String json = JsonDownloader.getMockMountainList(mContext);
        //String json = JsonDownloader.getJsonFromServer(); // Future way
        //Will be stored in database eventually, in future will get from datasource
        MountainListJson mountainListJson = JsonParser.parseMountainsFromMountainList(json);
        mountainList = mountainListJson.getMountainListItems();
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

        MountainListItem mountainListItem = (MountainListItem) getItem(position);
        viewHolder.name.setText(mountainListItem.getTitle());
        String mountainId = mountainListItem.getYid();
        final int difficulty = getRandomDifficulty(); //TODO : Lookup from current weather forecast (for now make random)
        viewHolder.difficulty.setImageResource(difficultyArray.get(difficulty));
        viewHolder.height.setText(String.valueOf(mountainListItem.getHeight())+"m");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
