package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItem;
import pulseanddecibels.jp.yamatenki.database.dao.CheckListItemDao;

/**
 * Created by Diarmaid Lindsay on 2015/11/17.
 * Copyright Pulse and Decibels 2015
 */
public class ChecklistListAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private final CheckListItemDao checkListItemDao;
    private List<CheckListItem> checkList = new ArrayList<>();

    private boolean editMode = false;

    public ChecklistListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        checkListItemDao = Database.getInstance(context).getCheckListItemDao();
        checkList = checkListItemDao.loadAll();
        Collections.sort(checkList, getIdComparitor());
    }

    @Override
    public int getCount() {
        return checkList.size();
    }

    @Override
    public Object getItem(int position) {
        return checkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolderItem();
            convertView = layoutInflater.inflate(R.layout.list_item_checklist, parent, false);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checklist_checkbox);
            viewHolder.text = (TextView) convertView.findViewById(R.id.checklist_text);
            viewHolder.clearButton = (Button) convertView.findViewById(R.id.checklist_x_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        final CheckListItem item = (CheckListItem) getItem(position);

        viewHolder.checkBox.setChecked(item.getChecked());
        viewHolder.text.setText(item.getText());
        viewHolder.checkBox.setOnClickListener(getCheckboxOnClickListener(item));
        viewHolder.checkBox.setVisibility(isEditMode() ? View.INVISIBLE : View.VISIBLE);
        viewHolder.clearButton.setVisibility(isEditMode() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.clearButton.setOnClickListener(getClearButtonOnClickListener(item));

        return convertView;
    }

    private View.OnClickListener getCheckboxOnClickListener(final CheckListItem item) {
        if (isEditMode()) return null; //disable checkbox in edit mode
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChecked(!item.getChecked());
                notifyDataSetChanged();
            }
        };
    }

    private View.OnClickListener getClearButtonOnClickListener(final CheckListItem item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckListItemDao dao = Database.getInstance(mContext).getCheckListItemDao();
                dao.delete(item);
                checkList.remove(item);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * We want newest items on top of the list so need a custom comparitor
     */
    public Comparator<CheckListItem> getIdComparitor() {
        return new Comparator<CheckListItem>() {
            @Override
            public int compare(CheckListItem lhs, CheckListItem rhs) {
                if(lhs.getId() < rhs.getId()) {
                    return 1;
                } else if (lhs.getId() > rhs.getId()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    public void addNewItem(String textEntered) {
        final CheckListItem item = new CheckListItem(null, textEntered, false);
        CheckListItemDao dao = Database.getInstance(mContext).getCheckListItemDao();
        dao.insert(item);
        checkList.add(0, item);
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    static class ViewHolderItem {
        CheckBox checkBox;
        TextView text;
        Button clearButton;
    }
}
