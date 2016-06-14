package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;
import java.util.Locale;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class Utils {
    private static Typeface hannariFont;

    public static Typeface getHannariTypeFace(Context context) {
        if(hannariFont == null) {
            hannariFont = Typeface.createFromAsset(context.getAssets(), "fonts/Hannari.otf");
        }
        return hannariFont;
    }

    public static String num2DigitString(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static boolean isNumeric(String value) {
        return value.matches("\\d+");
    }

    public static boolean isKanji(char value) {
        return Character.UnicodeBlock.of(value)
                == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
    }

    public static boolean isKana(char value) {
        return Character.UnicodeBlock.of(value) == Character.UnicodeBlock.HIRAGANA ||
                Character.UnicodeBlock.of(value) == Character.UnicodeBlock.KATAKANA;
    }

    public static int getRandomInRange(int MIN, int MAX) {
        return MIN + (int) (Math.random() * ((MAX - MIN) + 1));
    }

    public static long[] toLongArray(List<Long> list) {
        long[] ret = new long[list.size()];
        int i = 0;
        for (Long e : list)
            ret[i++] = e;
        return ret;
    }

    public static boolean isCloseColorMatch (int color1, int color2) {
        final int tolerance = 50;

        if (Math.abs (Color.red(color1) - Color.red (color2)) > tolerance )
            return false;
        if (Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        return Math.abs(Color.blue(color1) - Color.blue(color2)) <= tolerance;
    } // end match

    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String getLocale(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * In Android System settings or override using YamaTenki settings.
     *
     * False = Japanese
     * True = English
     */
    public static boolean isEnglishLanguageSelected(Context context) {
        String language = new Settings(context).getLanguage();
        //if "Default" is selected, use the System Language
        if(language.equals(Constants.DEFAULT_LANGUAGE_CODE)) {
            //check the Android system language setting
            return getLocale(context).equals(Constants.ENGLISH_LANGUAGE_CODE);
        } else {
            //use the setting which the user picked in the Settings Screen
            return language.equals(Constants.ENGLISH_LANGUAGE_CODE);
        }
    }

    /**
     * http://stackoverflow.com/questions/6421657/how-to-force-language-in-android-application
     *
     * Force the language which was set in "Settings" to apply to the Context (Activity) provided
     */
    public static void setLocale(Context context){
        String languageCode = new Settings(context).getLanguage();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        //if "Default" then use the Android Locale, else use the Locale (Language) from settings
        if(languageCode.equals(Constants.DEFAULT_LANGUAGE_CODE)) {
            //can't use Locale.forLanguageTag because requires Android v21+
            String lastLocale = new Settings(context).getLastLocale();
            if(lastLocale.equals(Constants.ENGLISH_LANGUAGE_CODE)) {
                config.locale = Locale.ENGLISH;
            } else {
                //default to Japanese
                config.locale = Locale.JAPANESE;
            }
        } else {
            config.locale = locale;
        }
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
