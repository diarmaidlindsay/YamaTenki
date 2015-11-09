package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.List;

/**
 * Created by Diarmaid Lindsay on 2015/09/30.
 * Copyright Pulse and Decibels 2015
 */
public class Utils {
    public static Typeface getHannariTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Hannari.otf");
    }

    public static String num2DigitString(int number) {
        return number < 10 ? "0" + number : "" + number;
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

    public static long[] toLongArray(List<Long> list)  {
        long[] ret = new long[list.size()];
        int i = 0;
        for (Long e : list)
            ret[i++] = e;
        return ret;
    }
}
