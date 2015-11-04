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
            append(1, "月");
            append(2, "火");
            append(3, "水");
            append(4, "木");
            append(5, "金");
            append(6, "土");
            append(7, "日");
        }
    };
    //0 == today, 1 == tomorrow etc
    private static final String[] JAPANESE_DAY_NAMES = {"本日", "本日", "明日", "明日", "週間予測"};
    private static final String[] JAPANESE_TIME_OF_DAY = {"午前", "午後"};
    private static DateTimeZone JAPAN_TIME_ZONE = DateTimeZone.forOffsetHours(9);

    public static String getFormattedHeader(int index) {
        DateTime dateTime = new DateTime();
        //don't cause IndexOutOfBoundsException
        switch (index) {
            case 0 : break; //today
            case 1 : break; //today
            case 2 : //tomorrow
            case 3 : dateTime = dateTime.plusDays(1); break; //tomorrow
            case 4 : return JAPANESE_DAY_NAMES[4]; // rest of the week
            default: return "";
        }
        String format = "%s %d/%d （%s）%s";
        String dayString = JAPANESE_DAY_NAMES[index];
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();
        int dayOfWeek = dateTime.getDayOfWeek();
        int timeOfDay;
        switch (index) {
            case 0 : //AM
            case 2 : //AM
                timeOfDay = 0; break;
            default : timeOfDay = 1; //PM
        }
        return String.format(format, dayString, month, day, JAPANESE_DAY_OF_WEEK.get(dayOfWeek), JAPANESE_TIME_OF_DAY[timeOfDay]);
    }

    public static String getHourFromTimestamp(Timestamp sqlTimeStamp) {
        DateTime dateTime = new DateTime(sqlTimeStamp.getTime());
        return Utils.num2DigitString(dateTime.getHourOfDay());
    }

    /**
     * Get key to match a scroll view column with the correct forecast
     *
     * @param dayIndex 0 = today, 1 = tomorrow etc
     * @param columnId corresponds to hours of the day : 00, 03, 06, 09 etc
     */
    public static String timeToMapKey(int dayIndex, String columnId) {
        DateTime dateTime = new DateTime();
        if (dayIndex > 0) {
            dateTime = dateTime.plusDays(dayIndex);
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

    /**
     * All instances of Joda Time (DateTime) will use this Time Zone.
     * Should be called as early as possible during JVM instantiation
     */
    public static void setDefaultTimeZone() {
        DateTimeZone.setDefault(JAPAN_TIME_ZONE);
    }
}
