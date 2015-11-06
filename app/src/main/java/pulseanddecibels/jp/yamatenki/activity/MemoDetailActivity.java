package pulseanddecibels.jp.yamatenki.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

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
public class MemoDetailActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    long mountainId;
    TextView mountainSubtitle;
    EditText dateFrom;
    EditText dateUntil;
    boolean from; //track which date box we're working with
    DateHolder fromDateHolder;
    TimeHolder fromTimeHolder;
    DateHolder untilDateHolder;
    TimeHolder untilTimeHolder;
    TimePickerDialog fromTimeDialog;
    TimePickerDialog untilTimeDialog;
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
        final DatePickerDialog fromDatePickerDialog = DatePickerDialog.newInstance(MemoDetailActivity.this, now.getYear()-1, now.getMonthOfYear()+1, now.getDayOfYear(), false);
        final DatePickerDialog untilDatePickerDialog = DatePickerDialog.newInstance(MemoDetailActivity.this, now.getYear()-1, now.getMonthOfYear()+1, now.getDayOfYear(), false);
        fromTimeDialog = TimePickerDialog.newInstance(MemoDetailActivity.this, now.getHourOfDay(), 0, true, false);
        fromTimeDialog.setVibrate(false);
        fromTimeDialog.setCloseOnSingleTapMinute(false);
        untilTimeDialog = TimePickerDialog.newInstance(MemoDetailActivity.this, now.getHourOfDay(), 0, true, false);
        untilTimeDialog.setVibrate(false);
        untilTimeDialog.setCloseOnSingleTapMinute(false);

        dateFrom = (EditText) findViewById(R.id.memo_date_from);
        dateFrom.setKeyListener(null);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = true;
                fromDatePickerDialog.setVibrate(false);
                fromDatePickerDialog.setYearRange(1985, now.getYear());
                fromDatePickerDialog.setCloseOnSingleTapDay(false);
                fromDatePickerDialog.show(getSupportFragmentManager(), "fromDate");
            }
        });
        dateUntil = (EditText) findViewById(R.id.memo_date_until);
        dateUntil.setKeyListener(null);
        dateUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = false;
                untilDatePickerDialog.setVibrate(false);
                untilDatePickerDialog.setYearRange(1985, now.getYear());
                untilDatePickerDialog.setCloseOnSingleTapDay(false);
                untilDatePickerDialog.show(getSupportFragmentManager(), "untilDate");
            }
        });

        if(savedInstanceState != null) {
            DatePickerDialog fromDate = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag("fromDate");
            if (fromDate != null) {
                fromDate.setOnDateSetListener(this);
            }

            DatePickerDialog untilDate = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag("untilDate");
            if (untilDate != null) {
                untilDate.setOnDateSetListener(this);
            }

            TimePickerDialog fromTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("fromTime");
            if (fromTime != null) {
                fromTime.setOnTimeSetListener(this);
            }

            TimePickerDialog untilTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("untilTime");
            if (untilTime != null) {
                untilTime.setOnTimeSetListener(this);
            }
        }

        weather = (EditText) findViewById(R.id.memo_weather);
        rating = (EditText) findViewById(R.id.memo_rating);
        rating.setKeyListener(null);
        memo = (EditText) findViewById(R.id.memo_memo);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if(from) {
            fromDateHolder = new DateHolder(year, month, day);
            fromTimeDialog.show(getSupportFragmentManager(), "fromTime");
        } else {
            untilDateHolder = new DateHolder(year, month, day);
            untilTimeDialog.show(getSupportFragmentManager(), "untilTime");
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
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
