package pulseanddecibels.jp.yamatenki.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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

    private static Long memoId;
    private Long mountainId;
    private EditText dateFrom;
    private EditText dateUntil;
    private boolean from; //track which date box we're working with
    private DateHolder fromDateHolder;
    private DateHolder untilDateHolder;
    private EditText weather;
    private EditText rating;
    private EditText memo;
    private Button buttonConfirm;
    private Button buttonEdit;
    private Button buttonDelete;
    private DateTime initialDateTimeFrom = new DateTime(DateUtils.JAPAN_TIME_ZONE);
    private DateTime initialDateTimeUntil = new DateTime(DateUtils.JAPAN_TIME_ZONE);
    private TextView timeLabel;
    private TextView activityTimeLabel;
    private TextView activityTimeText;
    private TextView weatherLabel;
    private TextView ratingLabel;
    private TextView memoLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setLocale(this);
        setContentView(R.layout.activity_memo_edit);
        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");
        memoId = arguments.getLong("memoId");

        TextView header = (TextView) findViewById(R.id.memo_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        TextView mountainSubtitle = (TextView) findViewById(R.id.text_memo_mountain_name);
        mountainSubtitle.setTypeface(Utils.getHannariTypeFace(this));

        timeLabel = (TextView) findViewById(R.id.text_memo_time);
        activityTimeLabel = (TextView) findViewById(R.id.text_memo_activity_time);
        weatherLabel = (TextView) findViewById(R.id.text_memo_weather);
        ratingLabel = (TextView) findViewById(R.id.text_memo_rating);
        memoLabel = (TextView) findViewById(R.id.text_memo_memo);

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
        buttonConfirm.setOnClickListener(getConfirmButtonOnClickListener());

        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(getDeleteButtonOnClickListener());
        buttonEdit = (Button) findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(getEditButtonOnClickListener());

        activityTimeText = (TextView) findViewById(R.id.memo_activity_time);

        dateFrom.setHint(DateUtils.getMemoDateTimeFromDateTime(initialDateTimeFrom.withTime(6, 54, 0, 0)));
        dateUntil.setHint(DateUtils.getMemoDateTimeFromDateTime(initialDateTimeUntil.withTime(12, 34, 0, 0)));
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

        //editing an existing memo, populate the fields, start in read-only mode (disable edittexts)
        if (memoId != 0L) {
            MyMemoDao memoDao = Database.getInstance(this).getMyMemoDao();
            MyMemo myMemo = memoDao.queryBuilder().where(MyMemoDao.Properties.Id.eq(memoId)).unique();
            mountainId = myMemo.getMountainId();

            Long memoDateTimeFrom = myMemo.getDateTimeFrom();
            Long memoDateTimeUntil = myMemo.getDateTimeUntil();

            if (memoDateTimeFrom != null) {
                initialDateTimeFrom = new DateTime(memoDateTimeFrom, DateUtils.JAPAN_TIME_ZONE);
                dateFrom.setText(DateUtils.getMemoDateTimeFromMillis(memoDateTimeFrom));
            }
            if (memoDateTimeUntil != null) {
                initialDateTimeUntil = new DateTime(memoDateTimeUntil, DateUtils.JAPAN_TIME_ZONE);
                dateUntil.setText(DateUtils.getMemoDateTimeFromMillis(memoDateTimeUntil));
            }

            if (memoDateTimeFrom != null && memoDateTimeUntil != null) {
                activityTimeText.setText(DateUtils.getActivityTimeFromMillis(memoDateTimeUntil - memoDateTimeFrom));
                activityTimeText.setVisibility(View.VISIBLE);
            }

            weather.setText(myMemo.getWeather());
            rating.setText(myMemo.getRating() == null ? "" : String.format("%d", myMemo.getRating()));
            memo.setText(myMemo.getMemo());
            editMode(false);
        } else {
            editMode(true);
        }

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        Mountain mountain =
                mountainDao.queryBuilder().where(MountainDao.Properties.Id.eq(mountainId)).unique();
        if(Utils.isEnglishLanguageSelected(this)) {
            mountainSubtitle.setText(mountain.getTitleEnglish());
        } else {
            mountainSubtitle.setText(mountain.getTitle());
        }
    }

    /**
     * Switch between read-only and edit/new memo mode.
     * To enable switching with a single button press, all the layout parameters are set here.
     */
    private void editMode(boolean enable) {
        dateFrom.setEnabled(enable);
        dateUntil.setEnabled(enable);
        weather.setEnabled(enable);
        rating.setEnabled(enable);
        memo.setEnabled(enable);

        int yamaBackground = ContextCompat.getColor(this, R.color.yama_background);
        int yamaBrown = ContextCompat.getColor(this, R.color.yama_brown);
        int hintTextColor = ContextCompat.getColor(this, R.color.hint_text);
        int roundEditText = R.drawable.round_edittext;

        activityTimeLabel.setBackgroundColor(yamaBrown);
        activityTimeLabel.setTextColor(yamaBackground);

        PercentRelativeLayout.LayoutParams dateFromLayout = (PercentRelativeLayout.LayoutParams) dateFrom.getLayoutParams();
        PercentRelativeLayout.LayoutParams dateUntilLayout = (PercentRelativeLayout.LayoutParams) dateUntil.getLayoutParams();
        PercentRelativeLayout.LayoutParams weatherLayout = (PercentRelativeLayout.LayoutParams) weather.getLayoutParams();
        PercentRelativeLayout.LayoutParams ratingLayout = (PercentRelativeLayout.LayoutParams) rating.getLayoutParams();

        if (!enable) { //read-only mode
            dateFrom.setBackgroundColor(yamaBackground);
            dateFrom.setHintTextColor(yamaBackground); //hide hint text
            dateFromLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            dateFromLayout.getPercentLayoutInfo().widthPercent = -1.0F;
            dateFrom.setLayoutParams(dateFromLayout);
            dateUntil.setBackgroundColor(yamaBackground);
            dateUntil.setHintTextColor(yamaBackground);
            dateUntilLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            dateUntilLayout.getPercentLayoutInfo().widthPercent = -1.0F;
            dateUntil.setLayoutParams(dateUntilLayout);
            weather.setBackgroundColor(yamaBackground);
            weather.setHintTextColor(yamaBackground);
            weatherLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            weatherLayout.getPercentLayoutInfo().widthPercent = -1.0F;
            weather.setLayoutParams(weatherLayout);
            rating.setBackgroundColor(yamaBackground);
            rating.setHintTextColor(yamaBackground);
            ratingLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ratingLayout.getPercentLayoutInfo().widthPercent = -1.0F;
            rating.setLayoutParams(ratingLayout);
            memo.setBackgroundColor(yamaBackground);
            memo.setHintTextColor(yamaBackground);
            memoLabel.setBackgroundColor(yamaBrown);
            memoLabel.setTextColor(yamaBackground);
            ratingLabel.setBackgroundColor(yamaBrown);
            ratingLabel.setTextColor(yamaBackground);
            timeLabel.setBackgroundColor(yamaBrown);
            timeLabel.setTextColor(yamaBackground);
            weatherLabel.setBackgroundColor(yamaBrown);
            weatherLabel.setTextColor(yamaBackground);

            timeLabel.setText(getResources().getString(R.string.text_memo_time_view));
            weatherLabel.setText(getResources().getString(R.string.text_memo_weather_view));
            ratingLabel.setText(getResources().getString(R.string.text_memo_rating_view));
            memoLabel.setText(getResources().getString(R.string.text_memo_memo_view));
            activityTimeLabel.setVisibility(View.VISIBLE);
            activityTimeText.setVisibility(View.VISIBLE);
            buttonConfirm.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else { //edit or new mode
            dateFrom.setBackgroundResource(roundEditText);
            dateFrom.setHintTextColor(hintTextColor); //show hint text
            dateFromLayout.width = 0;
            dateFromLayout.getPercentLayoutInfo().widthPercent = .6F;
            dateFrom.setLayoutParams(dateFromLayout);
            dateUntil.setBackgroundResource(roundEditText);
            dateUntil.setHintTextColor(hintTextColor);
            dateUntilLayout.width = 0;
            dateUntilLayout.getPercentLayoutInfo().widthPercent = .6F;
            dateUntil.setLayoutParams(dateUntilLayout);
            weather.setBackgroundResource(roundEditText);
            weather.setHintTextColor(hintTextColor);
            weatherLayout.width = 0;
            weatherLayout.getPercentLayoutInfo().widthPercent = .4F;
            weather.setLayoutParams(weatherLayout);
            rating.setBackgroundResource(roundEditText);
            rating.setHintTextColor(hintTextColor);
            ratingLayout.width = 0;
            ratingLayout.getPercentLayoutInfo().widthPercent = .15F;
            rating.setLayoutParams(ratingLayout);
            memo.setBackgroundResource(roundEditText);
            memo.setHintTextColor(hintTextColor);
            memoLabel.setBackgroundColor(yamaBackground);
            memoLabel.setTextColor(yamaBrown);
            ratingLabel.setBackgroundColor(yamaBackground);
            ratingLabel.setTextColor(yamaBrown);
            timeLabel.setBackgroundColor(yamaBackground);
            timeLabel.setTextColor(yamaBrown);
            weatherLabel.setBackgroundColor(yamaBackground);
            weatherLabel.setTextColor(yamaBrown);

            timeLabel.setText(getResources().getString(R.string.text_memo_time_edit));
            weatherLabel.setText(getResources().getString(R.string.text_memo_weather_edit));
            ratingLabel.setText(getResources().getString(R.string.text_memo_rating_edit));
            memoLabel.setText(getResources().getString(R.string.text_memo_memo_edit));
            activityTimeLabel.setVisibility(View.INVISIBLE); //hide "time taken" field and label
            activityTimeText.setVisibility(View.INVISIBLE);
            buttonConfirm.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }

        float density = getResources().getDisplayMetrics().density;
        int lrPadDp = (int) (5 * density); //left and right padding 5dp
        int udPadDp = (int) (5 * density); //top and bottom padding 5dp

        dateFrom.setPadding(lrPadDp, udPadDp, lrPadDp, udPadDp);
        dateUntil.setPadding(lrPadDp, udPadDp, lrPadDp, udPadDp);
        weather.setPadding(lrPadDp, udPadDp, lrPadDp, udPadDp);
        rating.setPadding(lrPadDp, udPadDp, lrPadDp, udPadDp);
        memo.setPadding(lrPadDp, udPadDp, lrPadDp, udPadDp);
    }

    private View.OnClickListener getRatingOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MemoDetailActivity.this, R.style.RatingDialog);
                dialog.setContentView(R.layout.dialog_rating);
                dialog.setTitle(getResources().getString(R.string.text_memo_table_header_rating));

                final TextView currentRating = (TextView) dialog.findViewById(R.id.selected_rating);
                final SeekBar ratingSeek = (SeekBar) dialog.findViewById(R.id.rating_seekbar);
                //seekbar runs from 0-9, we want 1-10, so we have to do some -1 and +1s
                ratingSeek.setMax(9);
                ratingSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentRating.setText(String.format("%d", progress + 1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                String currentRatingValue = rating.getText().toString();
                ratingSeek.setProgress(currentRatingValue.equals("") ? 5 : Integer.parseInt(currentRatingValue) - 1);
                Button setButton = (Button) dialog.findViewById(R.id.button_confirm);
                setButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rating.setText(String.format("%d", ratingSeek.getProgress() + 1));
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
    }

    private View.OnClickListener getConfirmButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime from = DateUtils.getDateTimeFromMemo(dateFrom.getText().toString());
                DateTime until = DateUtils.getDateTimeFromMemo(dateUntil.getText().toString());
                String weatherText = weather.getText().toString();
                Integer ratingText = rating.getText().toString().equals("") ? null : Integer.parseInt(rating.getText().toString());
                String memoText = memo.getText().toString();

                if (from != null && until != null && until.isBefore(from)) {
                    //tell the user that from must be before until
                    Toast.makeText(MemoDetailActivity.this, R.string.error_memo_dates_reversed, Toast.LENGTH_SHORT).show();
                } else {

                    MyMemoDao dao = Database.getInstance(MemoDetailActivity.this).getMyMemoDao();
                    //editing existing
                    if (memoId != 0L) {
                        MyMemo memo = dao.queryBuilder().where(MyMemoDao.Properties.Id.eq(memoId)).unique();
                        memo.setDateTimeFrom(from == null ? null : from.getMillis());
                        memo.setDateTimeUntil(until == null ? null : until.getMillis());
                        memo.setWeather(weatherText);
                        memo.setRating(ratingText);
                        memo.setMemo(memoText);
                        dao.update(memo);
                        //going back until the memo list so we should trigger an update
                        Intent intent = getIntent();
                        intent.putExtra("changedMemo", memoId);
                        setResult(RESULT_OK, intent);
                    } else {
                        //new memo
                        MyMemo memo = new MyMemo(null, mountainId, from == null ? null : from.getMillis(), until == null ? null : until.getMillis(), weatherText, ratingText, memoText);
                        MemoDetailActivity.memoId = dao.insert(memo);
                    }
                    if (from != null && until != null) {
                        activityTimeText.setText(DateUtils.getActivityTimeFromMillis(until.getMillis() - from.getMillis()));
                    } else {
                        activityTimeText.setText("");
                    }

                    editMode(false);
                }
            }
        };
    }

    private View.OnClickListener getDeleteButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MemoDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_alert);
                TextView alertText = (TextView) dialog.findViewById(R.id.alert_text);
                alertText.setText(R.string.dialog_delete_message);
                dialog.setCanceledOnTouchOutside(true);
                Button yes = (Button) dialog.findViewById(R.id.yes_button);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMemoDao dao = Database.getInstance(MemoDetailActivity.this).getMyMemoDao();
                        MyMemo memo = dao.queryBuilder().where(MyMemoDao.Properties.Id.eq(memoId)).unique();
                        dao.delete(memo);
                        //going back to the memo list so we should trigger an update
                        Intent intent = getIntent();
                        intent.putExtra("changedMemo", memoId);
                        setResult(RESULT_OK, intent);
                        dialog.dismiss();
                        finish();
                    }
                });
                Button no = (Button) dialog.findViewById(R.id.no_button);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        };
    }

    private View.OnClickListener getEditButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode(true);
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

        if (from) {
            fromDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialogFrom.show(getSupportFragmentManager(), "timeFrom");
        } else {
            untilDateHolder = new DateHolder(year, monthOfYear + 1, dayOfMonth);
            timePickerDialogUntil.show(getSupportFragmentManager(), "timeUntil");
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if (from) {
            TimeHolder fromTimeHolder = new TimeHolder(hourOfDay, minute);
            dateFrom.setText(String.format("%s%s", fromDateHolder.getDate(), fromTimeHolder.getTime()));
        } else {
            TimeHolder untilTimeHolder = new TimeHolder(hourOfDay, minute);
            dateUntil.setText(String.format("%s%s", untilDateHolder.getDate(), untilTimeHolder.getTime()));
        }
    }

    private class DateHolder {
        private final int year;
        private final int month;
        private final int day;

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
        private final int hour;
        private final int minute;

        public TimeHolder(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public String getTime() {
            return String.format("%s:%s", Utils.num2DigitString(hour), Utils.num2DigitString(minute));
        }
    }
}
