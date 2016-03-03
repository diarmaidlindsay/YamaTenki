package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;

import pulseanddecibels.jp.yamatenki.R;

/**
 * Created by Diarmaid Lindsay on 2015/12/10.
 * Copyright Pulse and Decibels 2015
 */
public class Constants {

    /**
     * SKUs for Google Play Store
     */
    public static final String SUBSCRIPTION_2016_Q1_1_MONTH_SKU = "subscription_1month";
    public static final String SUBSCRIPTION_2016_Q1_6_MONTH_SKU = "subscription_6month";
    public static final String SUBSCRIPTION_2016_Q1_1_YEAR_SKU = "subscription_1year";
    public static final String SUBSCRIPTION_1_MONTH_SKU = "new1month";
    public static final String SUBSCRIPTION_6_MONTH_SKU = "new6month";
    public static final String SUBSCRIPTION_1_YEAR_SKU = "new1year";

    public static final String DEFAULT_LANGUAGE_CODE = "df";
    public static final String ENGLISH_LANGUAGE_CODE = "en";
    public static final String JAPANESE_LANGUAGE_CODE = "jp";

    public static String getLanguageDisplayName(String code, Context context) {
        if(code.equals(Constants.ENGLISH_LANGUAGE_CODE)) {
            return context.getString(R.string.text_setting_language_subtitle_english);
        } else if (code.equals(Constants.JAPANESE_LANGUAGE_CODE)) {
            return context.getString(R.string.text_setting_language_subtitle_japanese);
        } else {
            return context.getString(R.string.text_setting_language_subtitle_default);
        }
    }
}
