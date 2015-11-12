package pulseanddecibels.jp.yamatenki.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.joda.time.DateTime;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMemo;
import pulseanddecibels.jp.yamatenki.database.dao.MyMemoDao;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/11/05.
 * Copyright Pulse and Decibels 2015
 */
public class MemoDetailActivity extends FragmentActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    Long mountainId;
    Long memoId;
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
    Button buttonConfirm;
    DateTime initialDateTimeFrom = new DateTime();
    DateTime initialDateTimeUntil = new DateTime();
    TextView timeLabel;
    TextView activityTimeLabel;
    TextView activityTimeText;
    TextView weatherLabel;
    TextView ratingLabel;
    TextView memoLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);
        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");
        memoId = arguments.getLong("memoId");

        TextView header = (TextView) findViewById(R.id.memo_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        mountainSubtitle = (TextView) findViewById(R.id.text_memo_mountain_name);
        mountainSubtitle.setTypeface(Utils.getHannariTypeFace(this));

        dateFrom = (EditText) findViewById(R.id.memo_date_from);
        dateFrom.setKeyListener(null);
        dateUntil = (EditText) findViewById(R.id.memo_date_until);
        dateUntil.setKeyListener(null);

        weather = (EditText) findViewById(R.id.memo_weather);
        rating = (EditText) findViewById(R.id.memo_rating);
        rating.setOnClickListener(getRatingOnClickListener());
        rating.setKeyListener(null);
        memo = (EditText) findViewById(R.id.memo_memo);
        buttonConfirm = (Button) findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(getConfirmButtonOnClickListener(memoId));

        activityTimeText = (TextView) findViewById(R.id.memo_activity_time);

        //editing an existing memo, populate the fields, start in read-only mode (disable edittexts)
        if(memoId != 0L) {
            MyMemoDao memoDao = Database.getInstance(this).getMyMemoDao();
            MyMemo myMemo = memoDao.queryBuilder().where(MyMemoDao.Properties.Id.eq(memoId)).unique();
            mountainId = myMemo.getMountainId();

            Long memoDateTimeFrom = myMemo.getDateTimeFrom();
            Long memoDateTimeUntil = myMemo.getDateTimeUntil();

            if(memoDateTimeFrom != null) {
                initialDateTimeFrom = new DateTime(memoDateTimeFrom);
                dateFrom.setText(DateUtils.getMemoDateTimeFromMillis(memoDateTimeFrom));
            }
            if(memoDateTimeUntil != null) {
                initialDateTimeUntil = new DateTime(memoDateTimeUntil);
                dateUntil.setText(DateUtils.getMemoDateTimeFromMillis(memoDateTimeUntil));
            }

            if(memoDateTimeFrom != null && memoDateTimeUntil != null) {
                activityTimeText.setText(DateUtils.getActivityTimeFromMillis(memoDateTimeUntil - memoDateTimeFrom));
                activityTimeText.setVisibility(View.VISIBLE);
            }

            weather.setText(myMemo.getWeather());
            rating.setText(myMemo.getRating() == null ? "" : String.format("%d", myMemo.getRating()));
            memo.setText(myMemo.getMemo());
            enableWidgets(false);
        } else {
            enableWidgets(true);
        }

        dateUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = false;
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(MemoDetailActivity.this, initialDateTimeUntil.getYear(), initialDateTimeUntil.getMonthOfYear() - 1,
                                initialDateTimeUntil.getDayOfMonth());
                calendarDatePickerDialogFragment.setThemeCustom(R.style.CustomTimePickerDialogTheme);
                calendarDatePickerDialogFragment.show(getSupportFragmentManager(), "dateUntil");
            }
        });

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = true;
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(MemoDetailActivity.this, initialDateTimeFrom.getYear(), initialDateTimeFrom.getMonthOfYear() - 1,
                                initialDateTimeFrom.getDayOfMonth());
                calendarDatePickerDialogFragment.setThemeCustom(R.style.CustomTimePickerDialogTheme);
                calendarDatePickerDialogFragment.show(getSupportFragmentManager(), "dateFrom");
            }
        });

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        Mountain mountain =
                mountainDao.queryBuilder().where(MountainDao.Properties.Id.eq(mountainId)).unique();
        mountainSubtitle.setText(mountain.getTitle());
    }

    private void enableWidgets(boolean enable) {
        dateFrom.setEnabled(enable);
        dateUntil.setEnabled(enable);

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

    private View.OnClickListener getConfirmButtonOnClickListener(final Long memoId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime from = DateUtils.getDateTimeFromMemo(dateFrom.getText().toString());
                DateTime to = DateUtils.getDateTimeFromMemo(dateUntil.getText().toString());
                String weatherText = weather.getText().toString();
                Integer ratingText = rating.getText().toString().equals("") ? null : Integer.parseInt(rating.getText().toString());
                String memoText = memo.getText().toString();

                if(from != null && to != null && to.isBefore(from))
                {
                    //tell the user that from must be before to
                    Toast.makeText(MemoDetailActivity.this, R.string.error_memo_dates_reversed, Toast.LENGTH_SHORT).show();
                } else {

                    MyMemoDao dao = Database.getInstance(MemoDetailActivity.this).getMyMemoDao();
                    //editing existing
                    if(memoId != 0L) {
                        MyMemo memo = dao.queryBuilder().where(MyMemoDao.Properties.Id.eq(memoId)).unique();
                        memo.setDateTimeFrom(from == null ? null : from.getMillis());
                        memo.setDateTimeUntil(to == null ? null : to.getMillis());
                        memo.setWeather(weatherText);
                        memo.setRating(ratingText);
                        memo.setMemo(memoText);
                        dao.update(memo);
                        //going back to the memo list so we should trigger an update
                        Intent intent = getIntent();
                        intent.putExtra("changedMemo", memoId);
                        setResult(RESULT_OK, intent);
                    } else {
                        //new memo
                        MyMemo memo = new MyMemo(null, mountainId, from == null ? null : from.getMillis(), to == null ? null : to.getMillis(), weatherText, ratingText, memoText);
                        dao.insert(memo);
                    }
                    finish();
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
        RadialTimePickerDialogFragment timePickerDialogFrom = RadialTimePickerDialogFragment
                .newInstance(MemoDetailActivity.this, initialDateTimeFrom.getHourOfDay(), initialDateTimeFrom.getMinuteOfHour(), true);
        RadialTimePickerDialogFragment timePickerDialogUntil = RadialTimePickerDialogFragment
                .newInstance(MemoDetailActivity.this, initialDateTimeUntil.getHourOfDay(), initialDateTimeUntil.getMinuteOfHour(), true);
        timePickerDialogFrom.setThemeCustom(R.style.CustomTimePickerDialogTheme);
        timePickerDialogUntil.setThemeCustom(R.style.CustomTimePickerDialogTheme);

        if(from) {
            fromDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialogFrom.show(getSupportFragmentManager(), "timeFrom");
        } else {
            untilDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialogUntil.show(getSupportFragmentManager(), "timeUntil");
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
