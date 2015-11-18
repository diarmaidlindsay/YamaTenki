package pulseanddecibels.jp.yamatenki.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.dao.query.QueryBuilder;
import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.activity.MemoDetailActivity;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMemo;
import pulseanddecibels.jp.yamatenki.database.dao.MyMemoDao;
import pulseanddecibels.jp.yamatenki.enums.MemoListColumn;
import pulseanddecibels.jp.yamatenki.enums.SortOrder;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/11/10.
 * Copyright Pulse and Decibels 2015
 */
public class MemoListAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private List memoList = new ArrayList<>();
    private MemoListColumn currentSorting;
    private SortOrder currentOrder;
    private String searchString = ""; //in case we have to resubmit the query after update in child activity

    public MemoListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.list_item_memo, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.memo_list_name);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.memo_list_rating);
            viewHolder.date = (TextView) convertView.findViewById(R.id.memo_list_date);

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

        final MyMemo memo = (MyMemo) getItem(position);
        Mountain mountain = memo.getMountain();

        viewHolder.name.setText(mountain.getTitle());
        viewHolder.rating.setText(memo.getRating() == null ? "" : String.format("%d", memo.getRating()));
        Long dateTime = memo.getDateTimeFrom();
        viewHolder.date.setText(dateTime == null ? "" : DateUtils.getMemoDateFromMillis(dateTime));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MemoDetailActivity.class);
                intent.putExtra("memoId", memo.getId());
                ((Activity) mContext).startActivityForResult(intent, 200);
            }
        });

        return convertView;
    }

    public void search(String searchString) {
        this.searchString = searchString;
        //all mountains which have memos
        MyMemoDao myMemoDao = Database.getInstance(mContext).getMyMemoDao();
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();

        List<MyMemo> allMemos = myMemoDao.loadAll();
        Set<Long> uniqueMountainIds = new HashSet<>();
        for (MyMemo memo : allMemos) {
            uniqueMountainIds.add(memo.getMountainId());
        }

        QueryBuilder qb = mountainDao.queryBuilder();

        if (searchString.length() > 0) {

            if (Utils.isKanji(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Title.like("%" + searchString + "%"));
            } else if (Utils.isKana(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Kana.like("%" + searchString + "%"));
            } else {
                qb.where(MountainDao.Properties.TitleEnglish.like("%" + searchString + "%"));
            }
        }

        qb.where(MountainDao.Properties.Id.in(uniqueMountainIds));

        List matchingMountains = qb.list();
        List<Long> matchingMountainIds = new ArrayList<>();
        for (int i = 0; i < matchingMountains.size(); i++) {
            Mountain mountain = (Mountain) matchingMountains.get(i);
            matchingMountainIds.add(mountain.getId());
        }

        memoList = myMemoDao.queryBuilder().where(MyMemoDao.Properties.MountainId.in(matchingMountainIds)).list();
        notifyDataSetChanged();
    }

    private Comparator<MyMemo> getNameComparitor() {
        return new Comparator<MyMemo>() {
            @Override
            public int compare(MyMemo lhs, MyMemo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getMountain().getTitleEnglish(), rhs.getMountain().getTitleEnglish());
            }
        };
    }

    private Comparator<MyMemo> getRatingComparitor() {
        return new Comparator<MyMemo>() {
            @Override
            public int compare(MyMemo lhs, MyMemo rhs) {
                Integer lhsRating = lhs.getRating();
                Integer rhsRating = rhs.getRating();

                if (lhsRating == null) lhsRating = 0;
                if (rhsRating == null) rhsRating = 0;

                if (lhsRating < rhsRating) {
                    return -1;
                } else if (lhsRating > rhsRating) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    private Comparator<MyMemo> getDateComparitor() {
        return new Comparator<MyMemo>() {
            @Override
            public int compare(MyMemo lhs, MyMemo rhs) {
                Long lhsDate = lhs.getDateTimeFrom();
                Long rhsDate = rhs.getDateTimeFrom();

                if (lhsDate == null) lhsDate = 0L;
                if (rhsDate == null) rhsDate = 0L;

                if (lhsDate < rhsDate) {
                    return -1;
                } else if (lhsDate > rhsDate) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    public void sort(MemoListColumn column) {
        if (currentSorting == column) {
            Collections.reverse(memoList);
            if (currentOrder == SortOrder.DESC) {
                currentOrder = SortOrder.ASC;
            } else {
                currentOrder = SortOrder.DESC;
            }
        } else {
            currentSorting = column;
            currentOrder = SortOrder.DESC;

            switch (column) {

                case NAME:
                    Collections.sort(memoList, getNameComparitor());
                    break;
                case RATING:
                    Collections.sort(memoList, getRatingComparitor());
                    break;
                case DATE:
                    Collections.sort(memoList, getDateComparitor());
                    break;
            }
        }

        notifyDataSetInvalidated();
    }

    public MemoListColumn getCurrentSorting() {
        return currentSorting;
    }

    public SortOrder getCurrentOrder() {
        return currentOrder;
    }

    static class ViewHolderItem {
        TextView name;
        TextView rating;
        TextView date;
    }
}
