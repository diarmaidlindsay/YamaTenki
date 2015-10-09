package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.reflect.Field;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.MountainListAdapter;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainSearchActivity extends Activity {
    TextView header;
    SearchView searchView;
    ListView mountainList;

    MountainListAdapter mountainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_name);
        header = (TextView) findViewById(R.id.text_search_header);
        header.setTypeface(Utils.getTitleTypeFace(this));
        searchView = (SearchView) findViewById(R.id.search_name_searchView);
        mountainList = (ListView) findViewById(R.id.list_mountains);
        mountainListAdapter = new MountainListAdapter(this);
        mountainList.setAdapter(mountainListAdapter);
        hideSearchViewUnderline();
        hideSearchIcon();

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getTextListener());
        searchView.setOnClickListener(getSearchViewOnClickListener());
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

    private void hideSearchIcon()
    {
        int searchImageViewId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchImageView = (ImageView) findViewById(searchImageViewId);
        searchImageView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private SearchView.OnQueryTextListener getTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
}
