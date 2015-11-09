package pulseanddecibels.jp.yamatenki.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.joda.time.DateTime;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/11/05.
 * Copyright Pulse and Decibels 2015
 */
public class MemoDetailActivity extends FragmentActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    long mountainId;
    TextView mountainSubtitle;
    EditText dateFrom;
    EditText dateUntil;
    boolean from; //track which date box we're working with
    DateHolder fromDateHolder;
    TimeHolder fromTimeHolder;
    DateHolder untilDateHolder;
    TimeHolder untilTimeHolder;
    EditText weather;
    EditText rating;
    EditText memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);
        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        Mountain mountain =
                mountainDao.queryBuilder().where(MountainDao.Properties.Id.eq(mountainId)).unique();

        TextView header = (TextView) findViewById(R.id.memo_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        mountainSubtitle = (TextView) findViewById(R.id.text_memo_mountain_name);
        mountainSubtitle.setTypeface(Utils.getHannariTypeFace(this));
        mountainSubtitle.setText(mountain.getTitle());

        final DateTime now = new DateTime();

        dateFrom = (EditText) findViewById(R.id.memo_date_from);
        dateFrom.setKeyListener(null);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = true;
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(MemoDetailActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialogFragment.setThemeCustom(R.style.CustomTimePickerDialogTheme);
                calendarDatePickerDialogFragment.show(getSupportFragmentManager(), "dateFrom");
            }
        });
        dateUntil = (EditText) findViewById(R.id.memo_date_until);
        dateUntil.setKeyListener(null);
        dateUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = false;
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(MemoDetailActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialogFragment.setThemeCustom(R.style.CustomTimePickerDialogTheme);
                calendarDatePickerDialogFragment.show(getSupportFragmentManager(), "dateUntil");
            }
        });

        weather = (EditText) findViewById(R.id.memo_weather);
        rating = (EditText) findViewById(R.id.memo_rating);
        rating.setOnClickListener(getRatingOnClickListener());
        rating.setKeyListener(null);
        memo = (EditText) findViewById(R.id.memo_memo);
    }

    private View.OnClickListener getRatingOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MemoDetailActivity.this);
                dialog.setContentView(R.layout.dialog_rating);
                dialog.show();
                dialog.setTitle("評価選択");
                for(int i=1; i < 11; i++) {
                    Button button = (Button) dialog.findViewById(getResources().getIdentifier("button" + i, "id", getPackageName()));
                    if(button != null) {
                        final String value = Integer.toString(i);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rating.setText(value);
                                dialog.dismiss();
                            }
                        });
                    }
                }

            }
        };
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment dateFromFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag("dateFrom");
        if (dateFromFragment != null) {
            dateFromFragment.setOnDateSetListener(this);
        }

        CalendarDatePickerDialogFragment dateUntilFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag("dateUntil");
        if (dateUntilFragment != null) {
            dateUntilFragment.setOnDateSetListener(this);
        }

        RadialTimePickerDialogFragment timeFromFragment = (RadialTimePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag("timeFrom");
        if (timeFromFragment != null) {
            timeFromFragment.setOnTimeSetListener(this);
        }

        RadialTimePickerDialogFragment timeUntilFragment = (RadialTimePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag("timeFrom");
        if (timeUntilFragment != null) {
            timeUntilFragment.setOnTimeSetListener(this);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        final DateTime now = new DateTime();
        RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment
                .newInstance(MemoDetailActivity.this, now.getHourOfDay(), now.getMinuteOfHour(), true);
        timePickerDialog.setThemeCustom(R.style.CustomTimePickerDialogTheme);

        if(from) {
            fromDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialog.show(getSupportFragmentManager(), "timeFrom");
        } else {
            untilDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialog.show(getSupportFragmentManager(), "timeUntil");
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if(from) {
            fromTimeHolder = new TimeHolder(hourOfDay, minute);
            dateFrom.setText(String.format("%s%s", fromDateHolder.getDate(), fromTimeHolder.getTime()));
        } else {
            untilTimeHolder = new TimeHolder(hourOfDay, minute);
            dateUntil.setText(String.format("%s%s", untilDateHolder.getDate(), untilTimeHolder.getTime()));
        }
    }

    private class DateHolder {
        private int year;
        private int month;
        private int day;

        public DateHolder(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public String getDate() {
            return String.format("%d年%s月%s日", year, Utils.num2DigitString(month), Utils.num2DigitString(day));
        }
    }

    private class TimeHolder {
        private int hour;
        private int minute;

        public TimeHolder(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public String getTime() {
            return String.format("%s:%s", Utils.num2DigitString(hour), Utils.num2DigitString(minute));
        }
    }
}
