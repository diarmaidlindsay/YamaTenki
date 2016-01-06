package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import java.io.Serializable;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.MemoListAdapter;
import pulseanddecibels.jp.yamatenki.adapter.MountainListAdapter;
import pulseanddecibels.jp.yamatenki.enums.MemoListColumn;
import pulseanddecibels.jp.yamatenki.enums.MountainListColumn;
import pulseanddecibels.jp.yamatenki.enums.Subscription;
import pulseanddecibels.jp.yamatenki.interfaces.OnInAppBillingServiceSetupComplete;
import pulseanddecibels.jp.yamatenki.utils.SubscriptionSingleton;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 * <p/>
 * Android can only have 1 searchable Activity.
 * So all search functionality is contained in this Activity.
 * This includes...
 * <p/>
 * Search by Name
 * Search by Height
 * Search by Area
 * Closest 20 Mountains
 * My Mountain List
 * My Mountain Memos
 */
public class SearchableActivity extends Activity implements OnInAppBillingServiceSetupComplete {
    private final int MIN_HEIGHT = 0;
    private final int MAX_HEIGHT = 3776; //Fuji-san's height
    private TextView header;
    private ListView mountainList;
    private ListView memoList;
    private TextView tableHeaderName;
    private TextView tableHeaderDifficulty;
    private TextView tableHeaderHeight;
    private TextView tableHeaderRating;
    private TextView tableHeaderDate;
    private SearchView minHeightSearchView;
    private SearchView maxHeightSearchView;
    private SearchView myMountainSearchView;
    private SearchView memoSearchView;
    private MountainListAdapter mountainListAdapter;
    private MemoListAdapter memoListAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionSingleton.getInstance(this).disposeIabHelperInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mountainListAdapter = new MountainListAdapter(this);
        if(savedInstanceState != null && savedInstanceState.getSerializable("subscription") != null) {
            Serializable sub = savedInstanceState.getSerializable("subscription");
            if(sub != null) {
                mountainListAdapter.setSubscription((Subscription) sub);
            }
        } else {
            SubscriptionSingleton.getInstance(this).initGoogleBillingApi(this, this);
        }

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

        //memo searchView and headers are different than the other (mountain) searchable options
        //we came here from MainActivity (Climbed Mountain Memo was chosen)
        if (searchType != null && searchType.equals("myMemo")) {
            memoListAdapter = new MemoListAdapter(this);
            setContentView(R.layout.activity_memo_list);
            header = (TextView) findViewById(R.id.text_search_header);
            header.setText(getResources().getString(R.string.text_memo_list_header));
            memoSearchView = (SearchView) findViewById(R.id.search_memo_searchView);
            setupSearchView(memoSearchView, false);
            memoSearchView.setQueryHint(getResources().getString(R.string.text_search_textbox_hint));
            memoSearchView.setOnQueryTextListener(getMemoTextListener());
            memoListAdapter.search("");

            LinearLayout memoListContainer = (LinearLayout) findViewById(R.id.memo_container);
            memoList = (ListView) memoListContainer.findViewById(R.id.list_memos);
            memoList.setAdapter(memoListAdapter);
            tableHeaderName = (TextView) memoListContainer.findViewById(R.id.table_header_name);
            tableHeaderName.setOnClickListener(getMemoNameHeaderOnClickListener());
            tableHeaderRating = (TextView) memoListContainer.findViewById(R.id.table_header_rating);
            tableHeaderRating.setOnClickListener(getRatingHeaderOnClickListener());
            tableHeaderDate = (TextView) memoListContainer.findViewById(R.id.table_header_date);
            tableHeaderDate.setOnClickListener(getDateHeaderOnClickListener());
            memoListAdapter.sort(MemoListColumn.NAME);
            findViewById(R.id.memo_container).requestFocus(); //stop search box auto focusing
        } else {
            //we came here from AreaSearchActivity (Area was chosen)
            if (areaId != 0) {
                setContentView(R.layout.activity_search_area);
                header = (TextView) findViewById(R.id.text_search_header);
                String areaTemplate = "%sの山";
                String area = getAreaForButtonId(areaId);
                header.setText(String.format(areaTemplate, area));
                mountainListAdapter.searchByArea(area);

            } else if (latitude != 0 && longitude != 0) {
                //we came here from Main Activity (Closest 20 Mountains)
                setContentView(R.layout.activity_search_area);
                header = (TextView) findViewById(R.id.text_search_header);
                header.setText(getResources().getString(R.string.text_closest_mountain_header));
                mountainListAdapter.searchByClosestMountains(latitude, longitude);
            } else if (searchType != null && searchType.equals("height")) {
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
                mountainListAdapter.searchByHeight(MIN_HEIGHT, MAX_HEIGHT);
                minHeightSearchView.requestFocus();
            } else if (searchType != null && searchType.equals("name")) {
                //we came here from Main Activity (Search by Name)
                setContentView(R.layout.activity_search_name);
                header = (TextView) findViewById(R.id.text_search_header);
                header.setText(getResources().getString(R.string.text_search_name_header));
                SearchView nameSearchView = (SearchView) findViewById(R.id.search_name_searchView);
                setupSearchView(nameSearchView, false);
                nameSearchView.setQueryHint(getResources().getString(R.string.text_search_textbox_hint));
                nameSearchView.setOnQueryTextListener(getNameTextListener(nameSearchView));
                mountainListAdapter.searchByName(""); //on launch display all
            } else if (searchType != null && searchType.equals("myMountain")) {
                //we came here from Main Activity (My Mountain List)
                setContentView(R.layout.activity_my_mountain_list);
                header = (TextView) findViewById(R.id.text_search_header);
                header.setText(getResources().getString(R.string.text_my_mountain_list_header));
                myMountainSearchView = (SearchView) findViewById(R.id.search_my_mountain_searchView);
                setupSearchView(myMountainSearchView, false);
                myMountainSearchView.setQueryHint(getResources().getString(R.string.text_search_textbox_hint));
                myMountainSearchView.setOnQueryTextListener(getMyMountainTextListener());
                mountainListAdapter.searchByMyMountainName("");
                LinearLayout searchViewOuter = (LinearLayout) findViewById(R.id.searchview_outer);
                //stop search box auto focusing even with blank my mountain list
                searchViewOuter.requestFocus();
            }

            LinearLayout mountainListContainer = (LinearLayout) findViewById(R.id.mountain_list_container);
            mountainList = (ListView) mountainListContainer.findViewById(R.id.list_mountains);
            mountainList.setAdapter(mountainListAdapter);
            tableHeaderName = (TextView) mountainListContainer.findViewById(R.id.table_header_name);
            tableHeaderName.setOnClickListener(getNameHeaderOnClickListener());
            tableHeaderDifficulty = (TextView) mountainListContainer.findViewById(R.id.table_header_difficulty);
            tableHeaderDifficulty.setOnClickListener(getDifficultyHeaderOnClickListener());
            tableHeaderHeight = (TextView) mountainListContainer.findViewById(R.id.table_header_height);
            tableHeaderHeight.setOnClickListener(getHeightHeaderOnClickListener());
            mountainListAdapter.sort(MountainListColumn.NAME);
            mountainListRequestFocus(); //stop search box auto focusing
        }

        updateTableHeaders();
        header.setTypeface(Utils.getHannariTypeFace(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                //a mountain was removed from my mountain list so we should resubmit the search
                if (myMountainSearchView != null) {
                    long mountainId = data.getLongExtra("changedMountain", 0L);
                    if (mountainId != 0L) {
                        mountainListAdapter.searchByMyMountainName(myMountainSearchView.getQuery().toString());
                    }
                }
            } else if (requestCode == 200) {
                //a memo was changed, so we should resubmit the search
                if (memoSearchView != null) {
                    long memoId = data.getLongExtra("changedMemo", 0L);
                    if (memoId != 0L) {
                        memoListAdapter.search(memoSearchView.getQuery().toString());
                    }
                }
            }
        }
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
     * @param hideXIcon  - Whether or not to hide the X icon which clears the searchview
     */
    private void hideSearchPlateWidgets(SearchView searchView, boolean hideXIcon) {
        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.WHITE);
            if (hideXIcon) {
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
            private final Handler mHandler = new Handler();
            private String minHeightString;
            private String maxHeightString;
            final Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {

                    int minHeight = MIN_HEIGHT;
                    int maxHeight = MAX_HEIGHT;
                    if (!minHeightString.equals("")) {
                        minHeight = Integer.parseInt(minHeightString);
                    }
                    if (!maxHeightString.equals("")) {
                        maxHeight = Integer.parseInt(maxHeightString);
                    }

                    if (minHeight > maxHeight) {
                        Toast.makeText(SearchableActivity.this, getString(R.string.toast_height_min_max_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mountainListAdapter.searchByHeight(minHeight, maxHeight);
                    hideKeyboard(minHeightSearchView); //only hide the keyboard if there is no error
                    mountainListRequestFocus();
                }
            };

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
            private final Handler mHandler = new Handler();
            private String text;
            final Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    mountainListAdapter.searchByName(text);
                    //result.setText(mountainListAdapter.getCount() + " items displayed");
                }
            };

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                hideKeyboard(searchView);
                mountainListRequestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if text cleared display all again
                if (newText.length() == 0) {
                    text = newText;
                    mHandler.removeCallbacks(mFilterTask);
                    mHandler.postDelayed(mFilterTask, 0);
                    mountainListRequestFocus();
                }
                return true;
            }
        };
    }

    private SearchView.OnQueryTextListener getMyMountainTextListener() {
        return new SearchView.OnQueryTextListener() {
            private final Handler mHandler = new Handler();
            private String text;
            final Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    mountainListAdapter.searchByMyMountainName(text);
                }
            };

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                hideKeyboard(myMountainSearchView);
                mountainListRequestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if text cleared display all again
                if (newText.length() == 0) {
                    text = newText;
                    mHandler.removeCallbacks(mFilterTask);
                    mHandler.postDelayed(mFilterTask, 0);
                    mountainListRequestFocus();
                }
                return true;
            }
        };
    }

    private SearchView.OnQueryTextListener getMemoTextListener() {
        return new SearchView.OnQueryTextListener() {
            private final Handler mHandler = new Handler();
            private String text;
            final Runnable mFilterTask = new Runnable() {
                @Override
                public void run() {
                    memoListAdapter.search(text);
                }
            };

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                hideKeyboard(memoSearchView);
                memoList.requestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if text cleared display all again
                if (newText.length() == 0) {
                    text = newText;
                    mHandler.removeCallbacks(mFilterTask);
                    mHandler.postDelayed(mFilterTask, 0);
                    memoList.requestFocus();
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

    private View.OnClickListener getNameHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mountainListAdapter.sort(MountainListColumn.NAME);
                updateTableHeaders();
            }
        };
    }

    private View.OnClickListener getDifficultyHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mountainListAdapter.sort(MountainListColumn.DIFFICULTY);
                updateTableHeaders();
            }
        };
    }

    private View.OnClickListener getHeightHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mountainListAdapter.sort(MountainListColumn.HEIGHT);
                updateTableHeaders();
            }
        };
    }

    private View.OnClickListener getMemoNameHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoListAdapter.sort(MemoListColumn.NAME);
                updateTableHeaders();
            }
        };
    }

    private View.OnClickListener getRatingHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoListAdapter.sort(MemoListColumn.RATING);
                updateTableHeaders();
            }
        };
    }

    private View.OnClickListener getDateHeaderOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoListAdapter.sort(MemoListColumn.DATE);
                updateTableHeaders();
            }
        };
    }

    private void hideKeyboard(SearchView searchView) {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private String getAreaForButtonId(int areaId) {
        String area;
        switch (areaId) {
            case R.id.button_area_chuugoku:
                area = "中国";
                break;
            case R.id.button_area_hokkaidou:
                area = "北海道";
                break;
            case R.id.button_area_hokuriku:
                area = "北陸";
                break;
            case R.id.button_area_kinki:
                area = "近畿";
                break;
            case R.id.button_area_koushin:
                area = "関東・甲信";
                break;
            case R.id.button_area_okinawa:
                area = "九州・沖縄";
                break;
            case R.id.button_area_touhoku:
                area = "東北";
                break;
            case R.id.button_area_toukai:
                area = "東海";
                break;
            case R.id.button_area_shikoku:
                area = "四国";
                break;
            default:
                area = "不明";
        }

        return area;
    }

    /**
     * Call after sorting to add sorting arrow to a header
     */
    private void updateTableHeaders() {
        //memos
        if (memoListAdapter != null) {
            String nameString = getResources().getString(R.string.text_memo_table_header_name);
            String ratingString = getResources().getString(R.string.text_memo_table_header_rating);
            String dateString = getResources().getString(R.string.text_memo_table_header_date);
            switch (memoListAdapter.getCurrentSorting()) {

                case NAME:
                    nameString = memoListAdapter.getCurrentOrder().getIndicator() + nameString;
                    break;
                case RATING:
                    ratingString = memoListAdapter.getCurrentOrder().getIndicator() + ratingString;
                    break;
                case DATE:
                    dateString = memoListAdapter.getCurrentOrder().getIndicator() + dateString;
                    break;
            }
            tableHeaderName.setText(nameString);
            tableHeaderRating.setText(ratingString);
            tableHeaderDate.setText(dateString);
        } else {
            //mountains
            String nameString = getResources().getString(R.string.text_search_table_header_name);
            String difficultyString = getResources().getString(R.string.text_search_table_header_difficulty);
            String heightString = getResources().getString(R.string.text_search_table_header_height);
            switch (mountainListAdapter.getCurrentSorting()) {

                case NAME:
                    nameString = mountainListAdapter.getCurrentOrder().getIndicator() + nameString;
                    break;
                case DIFFICULTY:
                    difficultyString = mountainListAdapter.getCurrentOrder().getIndicator() + difficultyString;
                    break;
                case HEIGHT:
                    heightString = mountainListAdapter.getCurrentOrder().getIndicator() + heightString;
                    break;
            }
            tableHeaderName.setText(nameString);
            tableHeaderDifficulty.setText(difficultyString);
            tableHeaderHeight.setText(heightString);
        }
    }

    /**
     * Stop search box getting focus and soft keyboard appearing
     */
    public void mountainListRequestFocus() {
        if(minHeightSearchView != null && maxHeightSearchView != null) {
            minHeightSearchView.clearFocus();
            maxHeightSearchView.clearFocus();
        }
        mountainList.requestFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("subscription", mountainListAdapter.getSubscription());
    }

    @Override
    public void iabSetupCompleted(Subscription subscription) {
        if(mountainListAdapter != null) {
            mountainListAdapter.setSubscription(subscription);
        }
    }
}
