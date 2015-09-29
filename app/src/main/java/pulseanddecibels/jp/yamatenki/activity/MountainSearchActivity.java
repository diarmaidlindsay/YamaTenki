package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.MountainListAdapter;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainSearchActivity extends Activity {
    SearchView searchView;
    ListView mountainList;

    MountainListAdapter mountainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_name);

        searchView = (SearchView) findViewById(R.id.search_name_searchView);
        mountainList = (ListView) findViewById(R.id.list_mountains);
        mountainListAdapter = new MountainListAdapter(this);
        mountainList.setAdapter(mountainListAdapter);
    }
}
