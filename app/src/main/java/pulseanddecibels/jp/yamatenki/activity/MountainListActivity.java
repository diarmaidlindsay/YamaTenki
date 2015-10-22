package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.MountainListAdapter;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListActivity extends Activity  {
    TextView header;
    ListView mountainList;
    SearchView minHeightSearchView;
    SearchView maxHeightSearchView;

    MountainListAdapter mountainListAdapter;

    private final int MIN_HEIGHT = 0;
    private final int MAX_HEIGHT = 3776; //Fuji-san's height

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        String searchType = "";
        int areaId = 0;
        double latitude = 0;
        double longitude = 0;
        if (arguments != null) {
            areaId = arguments.getInt("areaButtonId");
            latitude = arguments.getDouble("lat");
            longitude = arguments.getDouble("long");
            searchType = arguments.getString("searchType") == null ? "" : arguments.getString("searchType");
        }

        mountainListAdapter = new MountainListAdapter(this);

        //we came here from AreaSearchActivity (Area was chosen)
        if(areaId != 0) {
            setContentView(R.layout.activity_search_area);
            header = (TextView) findViewById(R.id.text_search_header);
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
            header = (TextView) findViewById(R.id.text_search_header);
            header.setText(getResources().getString(R.string.text_closest_mountain_header));
            mountainListAdapter.searchByClosestMountains(latitude, longitude);
        } else if(searchType != null && searchType.equals("height")) {
            //we came here from Main Activity (Search by Height)
            setContentView(R.layout.activity_search_height);
            header = (TextView) findViewById(R.id.text_search_header);
            header.setText(getResources().getString(R.string.text_search_height_header));
            minHeightSearchView = (SearchView) findViewById(R.id.min_height_searchview);
            maxHeightSearchView = (SearchView) findViewById(R.id.max_height_searchview);

            setupSearchView(minHeightSearchView, true);
            setupSearchView(maxHeightSearchView, true);
            minHeightSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);
            maxHeightSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);
            minHeightSearchView.setOnQueryTextListener(getHeightTextListener());
            maxHeightSearchView.setOnQueryTextListener(getHeightTextListener());
            minHeightSearchView.setQuery("" + MIN_HEIGHT, false); //set default values
            maxHeightSearchView.setQuery("" + MAX_HEIGHT, false); //set default values
            minHeightSearchView.requestFocus();
        }
        else if (searchType != null && searchType.equals("name")) {
            //we came here from Main Activity (Search by Name)
            setContentView(R.layout.activity_search_name);
            header = (TextView) findViewById(R.id.text_search_header);
            header.setText(getResources().getString(R.string.text_search_height_header));
            SearchView nameSearchView = (SearchView) findViewById(R.id.search_name_searchView);
            setupSearchView(nameSearchView, false);
            nameSearchView.setQueryHint(getResources().getString(R.string.text_search_name_textbox_hint));
            nameSearchView.setOnQueryTextListener(getNameTextListener(nameSearchView));
            mountainListAdapter.searchByName(""); //on launch display all
        }

        LinearLayout mountainListContainer = (LinearLayout) findViewById(R.id.mountain_list_container);
        mountainList = (ListView) mountainListContainer.findViewById(R.id.list_mountains);
        mountainList.setAdapter(mountainListAdapter);
        header.setTypeface(Utils.getTitleTypeFace(this));
    }

    private void setupSearchView(SearchView searchView, boolean hideXIcon) {
        hideSearchPlateWidgets(searchView, hideXIcon);
        hideSearchIcon(searchView);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setOnClickListener(getSearchViewOnClickListener(searchView));
    }

    /**
     * Hide the blue underline (Search plate) of the search view.
     * Optionally hide the X icon too
     *
     * @param searchView - Search view to hide the widgets of
     * @param hideXIcon - Whether or not to hide the X icon which clears the searchview
     */
    private void hideSearchPlateWidgets(SearchView searchView, boolean hideXIcon) {
        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.WHITE);
            if(hideXIcon) {
                int xButtonId = searchPlateView.getContext().getResources()
                        .getIdentifier("android:id/search_close_btn", null, null);
                ImageView xButton = (ImageView) searchView.findViewById(xButtonId);
                xButton.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }

        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
    }

    private void hideSearchIcon(SearchView searchView) {
        int searchImageViewId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchImageView = (ImageView) searchView.findViewById(searchImageViewId);
        searchImageView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private SearchView.OnQueryTextListener getHeightTextListener() {
        return new SearchView.OnQueryTextListener() {
            private String minHeightString;
            private String maxHeightString;

            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {

                    int minHeight = MIN_HEIGHT;
                    int maxHeight = MAX_HEIGHT;
                    if(!minHeightString.equals("")) {
                        minHeight = Integer.parseInt(minHeightString);
                    }
                    if(!maxHeightString.equals("")) {
                        maxHeight = Integer.parseInt(maxHeightString);
                    }

                    if(minHeight > maxHeight) {
                        Toast.makeText(MountainListActivity.this, "The minimum should be less than the maximum", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mountainListAdapter.searchByHeight(minHeight, maxHeight);
                    hideKeyboard(minHeightSearchView); //only hide the keyboard if there is no error
                    mountainList.requestFocus();
                }
            };
            private Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextSubmit(String query) {
                minHeightString = minHeightSearchView.getQuery().toString();
                maxHeightString = maxHeightSearchView.getQuery().toString();
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    private SearchView.OnQueryTextListener getNameTextListener(final SearchView searchView) {
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
                hideKeyboard(searchView);
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

    private View.OnClickListener getSearchViewOnClickListener(final SearchView searchView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        };
    }

    public void hideKeyboard(SearchView searchView) {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
