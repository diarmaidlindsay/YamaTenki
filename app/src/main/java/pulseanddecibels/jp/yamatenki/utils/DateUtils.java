package pulseanddecibels.jp.yamatenki.utils;

import android.util.SparseArray;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private static final DateTimeZone JAPAN_TIME_ZONE = DateTimeZone.forOffsetHours(9);

    static final String[] TIME_INCREMENTS_AM = {"00", "03", "06", "09"}; // column headers
    static final String[] TIME_INCREMENTS_PM = {"12", "15", "18", "21"}; // column headers

    public static String getFormattedHeader(int index) {
        DateTime dateTime = new DateTime();
        //don't cause IndexOutOfBoundsException
        switch (index) {
            case 0:
                break; //today
            case 1:
                break; //today
            case 2: //tomorrow
            case 3:
                dateTime = dateTime.plusDays(1);
                break; //tomorrow
            case 4:
                return JAPANESE_DAY_NAMES[4]; // rest of the week
            default:
                return "";
        }
        String format = "%s %d/%d （%s）%s";
        String dayString = JAPANESE_DAY_NAMES[index];
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();
        int dayOfWeek = dateTime.getDayOfWeek();
        int timeOfDay;
        switch (index) {
            case 0: //AM
            case 2: //AM
                timeOfDay = 0;
                break;
            default:
                timeOfDay = 1; //PM
        }
        return String.format(format, dayString, month, day, JAPANESE_DAY_OF_WEEK.get(dayOfWeek), JAPANESE_TIME_OF_DAY[timeOfDay]);
    }

    public static DateTime getDateTimeFromForecast(String timeStampString) {
        //"timestamp" : "2015-09-181T00:00:00.+09:00"
        return parseDateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.JAPAN), timeStampString);
    }

    public static DateTime getDateTimeFromMemo(String dateTimeString) {
//        "2015年11月09日08:00"
        return parseDateTime(new SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.JAPAN), dateTimeString);
    }

    public static String getMemoDateFromMillis(long millis) {
        DateTime dateTime = new DateTime(millis);
        return String.format("%d/%s/%s", dateTime.getYear(), Utils.num2DigitString(dateTime.getMonthOfYear()), Utils.num2DigitString(dateTime.getDayOfMonth()));
    }

    public static String getMemoDateTimeFromDateTime(DateTime dateTime) {
        return String.format("%d年%s月%s日%s:%s",
                dateTime.getYear(), Utils.num2DigitString(dateTime.getMonthOfYear()), Utils.num2DigitString(dateTime.getDayOfMonth()),
                Utils.num2DigitString(dateTime.getHourOfDay()), Utils.num2DigitString(dateTime.getMinuteOfHour()));
    }

    public static String getMemoDateTimeFromMillis(long millis) {
        DateTime dateTime = new DateTime(millis);
        return getMemoDateTimeFromDateTime(dateTime);
    }

    public static String getActivityTimeFromMillis(long millis) {
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        int days = (int) (millis / (1000 * 60 * 60 * 24));

        String activityTime = "";

        if (days > 0) {
            activityTime += String.format("%d日", days);
        }
        if (hours > 0) {
            activityTime += String.format("%d時間", hours);
        }
        activityTime += String.format("%d分", minutes);

        return activityTime;
    }

    private static DateTime parseDateTime(SimpleDateFormat dateFormat, String dateTimeString) {
        //http://developer.android.com/reference/java/text/SimpleDateFormat.html
        DateTime dateTime = null;
        if (dateTimeString != null && dateTimeString.length() > 1) {
            try {
                Date date = dateFormat.parse(dateTimeString);
                dateTime = new DateTime(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateTime;
    }

    public static String getColumnHeadingForShortTermForecast(int scrollViewIndex, int columnId) {
        String[] timePeriod;
        switch (scrollViewIndex) {
            case 0 :
            case 2 : timePeriod = TIME_INCREMENTS_AM; break;
            case 1 :
            default :  timePeriod = TIME_INCREMENTS_PM; break;
        }

        return timePeriod[columnId];
    }

    /**
     * column index 0 is 2 days from now
     */
    public static String getColumnHeadingForLongTermForecast(int columnId) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(columnId + 2);
        return String.format("%s/%s", Utils.num2DigitString(dateTime.getMonthOfYear()), Utils.num2DigitString(dateTime.getDayOfMonth()));
    }

    /**
     * Get key to match a scroll view column with the correct forecast
     *
     * @param scrollViewIndex 0 = today AM, 1 = today PM, 2 = tomorrow AM, 3 = tomorrow PM
     * @param columnId corresponds to index of hour of the day : [00, 03, 06, 09] or [12, 15, 18, 21]
     */
    public static String getWidgetToForecastKey(int scrollViewIndex, int columnId) {
        DateTime dateTime = new DateTime();
        if (scrollViewIndex == 2 || scrollViewIndex == 3) {
            dateTime = dateTime.plusDays(1);
        }
        return
                Utils.num2DigitString(dateTime.getMonthOfYear()) +
                        "/" +
                        Utils.num2DigitString(dateTime.getDayOfMonth()) +
                        "-" +
                        getColumnHeadingForShortTermForecast(scrollViewIndex, columnId);
    }

    /**
     * Derive key for storing short term forecasts in HashMap from its TimeStamp
     */
    public static String getDateToShortTermForecastKey(DateTime dateTime) {
        return
                Utils.num2DigitString(dateTime.getMonthOfYear()) +
                        "/" +
                        Utils.num2DigitString(dateTime.getDayOfMonth()) +
                        "-" +
                        Utils.num2DigitString(dateTime.getHourOfDay());
    }

    /**
     * Derive key for storing long term forecasts in HashMap from its TimeStamp
     */
    public static String getDateToLongTermForecastKey(DateTime dateTime) {
        return
                Utils.num2DigitString(dateTime.getMonthOfYear()) +
                        "/" +
                        Utils.num2DigitString(dateTime.getDayOfMonth());
    }

    /**
     * All instances of Joda Time (DateTime) will use this Time Zone.
     * Should be called as early as possible during JVM instantiation
     */
    public static void setDefaultTimeZone() {
        DateTimeZone.setDefault(JAPAN_TIME_ZONE);
    }
}
