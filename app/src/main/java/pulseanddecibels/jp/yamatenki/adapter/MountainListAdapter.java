package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.activity.MountainForecastActivity;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListAdapter extends BaseAdapter {

    final SparseIntArray difficultyArray = new SparseIntArray() {
        {
            append(1, R.drawable.a_difficulty_small);
            append(2, R.drawable.b_difficulty_small);
            append(3, R.drawable.c_difficulty_small);
        }
    };
    private List mountainList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater layoutInflater;

    public MountainListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        initialiseDataSets();
    }

    private void initialiseDataSets() {
//        String json = JSONDownloader.getMockMountainList(mContext);
//        //String json = JSONDownloader.getJsonFromServer(); // Future way
//        //Will be stored in database eventually, in future will get from datasource
//        MountainListJSONZ mountainListJSON = JSONParser.parseMountainsFromMountainList(json);
//        mountainList = mountainListJSON.getMountainArrayElements();

        search(""); //display all at first
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
        return MIN + (int) (Math.random() * ((MAX - MIN) + 1));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolderItem();
            convertView = layoutInflater.inflate(R.layout.list_item_mountain, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.mountain_list_name);
            viewHolder.difficulty = (ImageView) convertView.findViewById(R.id.mountain_list_difficulty);
            viewHolder.height = (TextView) convertView.findViewById(R.id.mountain_list_height);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //check first bit, if set then it is an odd number. We'll give alternate rows different backgrounds
        if ((position % 2) == 0) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.table_bg_alt));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.yama_background));
        }
        Mountain mountain = (Mountain) getItem(position);
        viewHolder.name.setText(mountain.getKanjiName());
        final long mountainId = mountain.getId();
        final int difficulty = getRandomDifficulty(); //TODO : Lookup from current weather forecast (for now make random)
        viewHolder.difficulty.setImageResource(difficultyArray.get(difficulty));
        viewHolder.height.setText(String.format("%sm", String.valueOf(mountain.getHeight())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MountainForecastActivity.class);
                intent.putExtra("mountainId", mountainId);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void search(String searchString) {
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
        QueryBuilder qb = mountainDao.queryBuilder();

        if (searchString.length() > 0) {

            if (Utils.isKanji(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.KanjiName.like("%" + searchString + "%"));
            } else if (Utils.isKana(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.HiraganaName.like("%" + searchString + "%"));
            } else {
                qb.where(MountainDao.Properties.RomajiName.like("%" + searchString + "%"));
//                //Had to use raw query instead of GreenDAO because upper(X) function could not be used with API
//                List<Mountain> romajiMountains = new ArrayList<>();
//                SQLiteDatabase db = Database.getInstance(mContext).getDatabase();
//                Cursor cursor = db.rawQuery("SELECT * FROM " + MountainDao.TABLENAME + " WHERE upper(" + MountainDao.Properties.RomajiName.columnName + ") LIKE '%?%'",
//                        new String[]{searchString.toUpperCase()});
//                cursor.moveToFirst();
//                while(!cursor.isAfterLast()) {
//                    romajiMountains.add(cursorToMountain(cursor));
//                    cursor.moveToNext();
//                }
//                cursor.close();
//                mountainList = romajiMountains;
//                return;
            }
        }

        mountainList = qb.list();
        notifyDataSetChanged();
    }

    /**
     * Long id, String kanjiName, String kanjiNameArea,
     * String hiraganaName, String romajiName, Integer height, long prefectureId,
     * long areaId, long coordinateId, String closestTown
     */
    private Mountain cursorToMountain(Cursor cursor) {
        return new Mountain(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getInt(5), cursor.getLong(6), cursor.getLong(7), cursor.getLong(8), cursor.getString(9));
    }

    static class ViewHolderItem {
        TextView name;
        ImageView difficulty;
        TextView height;
    }
}
