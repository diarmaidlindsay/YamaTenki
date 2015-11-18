package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.adapter.ChecklistListAdapter;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/11/16.
 * Copyright Pulse and Decibels 2015
 */
public class ChecklistActivity extends Activity {
    private ListView checkList;
    private EditText editText;
    private Button addButton;
    private Button editButton;
    private ChecklistListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        listAdapter = new ChecklistListAdapter(this);
        TextView header = (TextView) findViewById(R.id.checklist_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        checkList = (ListView) findViewById(R.id.checklist_list);
        editText = (EditText) findViewById(R.id.checklist_edittext);
        addButton = (Button) findViewById(R.id.button_new_checklist_item);
        addButton.setOnClickListener(getAddButtonOnClickListener());
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

    public View.OnClickListener getAddButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                //show keyboard
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String textEntered = editText.getText().toString();
                            if (!textEntered.equals("")) {
                                listAdapter.addNewItem(textEntered);
                                editText.setText(""); //clear text
                                checkList.setSelectionAfterHeaderView(); //scroll to top of list
                            }
                            editText.setVisibility(View.GONE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            return true;
                        }
                        return false;
                    }
                });
            }
        };
    }
}
