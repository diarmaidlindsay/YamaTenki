package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.MountainListAdapter;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListActivity extends Activity  {
    TextView header;
    SearchView searchView;
    ListView mountainList;

    MountainListAdapter mountainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        int areaId = 0;
        double latitude = 0;
        double longitude = 0;
        if (arguments != null) {
            areaId = arguments.getInt("areaButtonId");
            latitude = arguments.getDouble("lat");
            longitude = arguments.getDouble("long");
        }

        mountainListAdapter = new MountainListAdapter(this);

        //we came here from AreaSearchActivity (Area was chosen)
        if(areaId != 0) {
            setContentView(R.layout.activity_search_area);
            header = (TextView) findViewById(R.id.text_area_header);
            String areaTemplate = "%sの山";
            String area;

            switch (areaId) {
                case R.id.button_area_chuugoku : area = "中国"; break;
                case R.id.button_area_hokkaidou : area = "北海道"; break;
                case R.id.button_area_hokuriku : area = "北陸"; break;
                case R.id.button_area_kinki : area = "近畿"; break;
                case R.id.button_area_koushin : area = "関東・甲信"; break;
                case R.id.button_area_okinawa : area = "九州・沖縄"; break;
                case R.id.button_area_touhoku : area = "東北"; break;
                case R.id.button_area_toukai : area = "東海"; break;
                case R.id.button_area_shikoku : area = "四国"; break;
                default: area = "不明";
            }
            header.setText(String.format(areaTemplate, area));
            mountainListAdapter.searchByArea(area);

        } else if (latitude != 0 && longitude != 0) {
            //we came here from Main Activity (Closest 20 Mountains)
            setContentView(R.layout.activity_search_area);
            header = (TextView) findViewById(R.id.text_area_header);
            header.setText(R.string.text_closest_mountain_header);
            mountainListAdapter.searchByClosestMountains(latitude, longitude);
        }
        else {
            //we came here from Main Activity (Search by Name)
            setContentView(R.layout.activity_search_name);
            header = (TextView) findViewById(R.id.text_search_header);
            searchView = (SearchView) findViewById(R.id.search_name_searchView);
            hideSearchViewUnderline();
            hideSearchIcon();

            SearchManager searchManager = (SearchManager)
                    getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(getTextListener());
            searchView.setOnClickListener(getSearchViewOnClickListener());
            mountainListAdapter.searchByName(""); //on launch display all
        }

        LinearLayout mountainListContainer = (LinearLayout) findViewById(R.id.mountain_list_container);
        mountainList = (ListView) mountainListContainer.findViewById(R.id.list_mountains);
        mountainList.setAdapter(mountainListAdapter);
        header.setTypeface(Utils.getTitleTypeFace(this));
    }

    /**
     * Hide the blue underline (default theme) of the SearchView
     */
    private void hideSearchViewUnderline() {
        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.WHITE);
        }

        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
    }

    private void hideSearchIcon() {
        int searchImageViewId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchImageView = (ImageView) findViewById(searchImageViewId);
        searchImageView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private SearchView.OnQueryTextListener getTextListener() {
        return new SearchView.OnQueryTextListener() {
            private String text;
            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    mountainListAdapter.searchByName(text);
                    //result.setText(mountainListAdapter.getCount() + " items displayed");
                }
            };
            private Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                hideKeyboard();
                mountainList.requestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if text cleared display all again
                if (newText.length() == 0) {
                    text = newText;
                    mHandler.removeCallbacks(mFilterTask);
                    mHandler.postDelayed(mFilterTask, 0);
                    mountainList.requestFocus();
                }
                return true;
            }
        };
    }

    private View.OnClickListener getSearchViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        };
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
