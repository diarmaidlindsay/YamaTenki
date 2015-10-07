package pulseanddecibels.jp.yamatenki.utils;

import android.util.SparseArray;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;

/**
 * Created by Diarmaid Lindsay on 2015/10/05.
 * Copyright Pulse and Decibels 2015
 */
public class DateUtils {

    /**
     * Calendar.DAY_OF_WEEK to Japanese
     */
    private static final SparseArray<String> JAPANESE_DAY_OF_WEEK = new SparseArray<String>() {
        {
            append(1, "日");
            append(2, "月");
            append(3, "火");
            append(4, "水");
            append(5, "木");
            append(6, "金");
            append(7, "土");
        }
    };

    //0 == today, 1 == tomorrow etc
    private static final String[] JAPANESE_DAY_NAMES =  { "本日", "明日", "", "", "", "", "" };

    public static String getFormattedHeader(int index) {
        DateTime dateTime = new DateTime();
        //don't cause IndexOutOfBoundsException
        if(index > 0 && index < JAPANESE_DAY_NAMES.length - 1) {
            dateTime.plusDays(index);
        }
        String format = "%s %d/%d （%s）";
        String dayString = JAPANESE_DAY_NAMES[index];
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();
        int dayOfWeek = dateTime.getDayOfWeek();
        return String.format(format, dayString, month, day, JAPANESE_DAY_OF_WEEK.get(dayOfWeek));
    }

    public static String getHourFromTimestamp(Timestamp sqlTimeStamp) {
        DateTime dateTime = new DateTime(sqlTimeStamp.getTime());
        return Utils.num2DigitString(dateTime.getHourOfDay());
    }

    /**
     * Get key to match a scroll view column with the correct forecast
     * @param dayIndex 0 = today, 1 = tomorrow etc
     * @param columnId corresponds to hours of the day : 00, 03, 06, 09 etc
     */
    public static String timeToMapKey(int dayIndex, String columnId) {
        DateTime dateTime = new DateTime();
        if(dayIndex > 0) {
            dateTime.plusDays(dayIndex);
        }
        return
            Utils.num2DigitString(dateTime.getMonthOfYear()) +
            "/" +
            Utils.num2DigitString(dateTime.getDayOfMonth()) +
            "-" +
            columnId;
    }

    /**
     * Derive key for storing forecasts in HashMap from its TimeStamp
     */
    public static String timeStampToMapKey(Timestamp sqlTimeStamp) {
        DateTime dateTime = new DateTime(sqlTimeStamp.getTime());
        return
            Utils.num2DigitString(dateTime.getMonthOfYear()) +
            "/" +
            Utils.num2DigitString(dateTime.getDayOfMonth()) +
            "-" +
            Utils.num2DigitString(dateTime.getHourOfDay());
    }
}
