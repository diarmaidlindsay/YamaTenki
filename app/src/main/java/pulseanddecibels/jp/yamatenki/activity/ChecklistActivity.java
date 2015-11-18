package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.ChecklistListAdapter;

/**
 * Created by Diarmaid Lindsay on 2015/11/16.
 * Copyright Pulse and Decibels 2015
 */
public class ChecklistActivity extends Activity {
    ListView checkList;
    Button addButton;
    Button editButton;
    ChecklistListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        listAdapter = new ChecklistListAdapter(this);
        checkList = (ListView) findViewById(R.id.checklist_list);
        addButton = (Button) findViewById(R.id.button_new_checklist_item);
        editButton = (Button) findViewById(R.id.button_edit_checklist_item);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editOn = getResources().getString(R.string.button_editing_checklist);
                String editOff = getResources().getString(R.string.button_edit_checklist);
                listAdapter.setEditMode(!listAdapter.isEditMode());
                editButton.setText(listAdapter.isEditMode() ? editOn : editOff);
            }
        });
        checkList.setAdapter(listAdapter);
    }
}
