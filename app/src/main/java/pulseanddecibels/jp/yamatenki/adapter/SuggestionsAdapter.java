package pulseanddecibels.jp.yamatenki.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2016/03/04.
 * Copyright Pulse and Decibels 2016
 */
public class SuggestionsAdapter extends SimpleCursorAdapter {
    Context mContext;
    List<Mountain> allMountains;
    private List<String> suggestionsList;
    private String previousQuery;
    private String queryNow; //should never become null

    public SuggestionsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
        allMountains = mountainDao.loadAll();
        suggestionsList = new ArrayList<>();
    }

    public void populateSuggestions(String query) {
        final MatrixCursor cursor = new MatrixCursor(new String[]{ BaseColumns._ID, "mountainName" });

        query = query.toLowerCase().trim();
        queryNow = query;

        if(query.length() < 1) {
            suggestionsList.clear();
            previousQuery = null;
            changeCursor(cursor);
            return;
        }

        Set<String> suggestionsSet = new HashSet<>();

        //some text was deleted so we should revert to suggest from all mountains
        if(previousQuery == null || query.length() < previousQuery.length() ||
                //when entering kanji with a romaji keyboard, or entering kanji with hiragana, text gets completely replaced.
                //so if query does not contain previous query it means it was replaced.
                !query.contains(previousQuery) || suggestionsList.size() == 0) {
            for (Mountain mountain : allMountains) {
                String title = mountain.getTitle();
                String kana = mountain.getKana();
                String titleEnglish = mountain.getTitleEnglish();
                if (title.toLowerCase().contains(query)) {
                    suggestionsSet.add(title);
                } else if (kana.toLowerCase().contains(query)) {
                    suggestionsSet.add(kana);
                } else if (titleEnglish.toLowerCase().contains(query)) {
                    suggestionsSet.add(titleEnglish);
                }
            }
        } else {
            //search the subset of results from the previous query
            for(String text : suggestionsList) {
                if(text.toLowerCase().contains(query)) {
                    suggestionsSet.add(text);
                }
            }
        }

        suggestionsList = new ArrayList<>(suggestionsSet);
        //Only do sorting for Romaji
        if(!Utils.isKanji(query.charAt(0)) && !Utils.isKana(query.charAt(0))) {
            Collections.sort(suggestionsList, new SortIgnoreCase());
        }

        for(int i = 0; i < suggestionsList.size(); i++) {
            cursor.addRow(new Object[]{i, suggestionsList.get(i)});
        }
        previousQuery = query;
        changeCursor(cursor);
    }

    @Override
    public void setViewText(TextView v, String text) {
        v.setText(highlight(text, queryNow));
    }

    private CharSequence highlight(String originalText, String search) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = originalText;
        //only do for romaji
        if(!Utils.isKanji(search.charAt(0)) && !Utils.isKana(search.charAt(0))) {
            normalizedText = Normalizer
                    .normalize(originalText, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase(Locale.ENGLISH);
        }

        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());
                highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, android.R.color.holo_red_dark)),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }
            return highlighted;
        }
    }

    private class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
