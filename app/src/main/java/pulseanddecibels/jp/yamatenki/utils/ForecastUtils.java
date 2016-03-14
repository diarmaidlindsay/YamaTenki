package pulseanddecibels.jp.yamatenki.utils;

import android.util.SparseIntArray;

import pulseanddecibels.jp.yamatenki.R;

/**
 * Created by Diarmaid Lindsay on 2016/03/09.
 * Copyright Pulse and Decibels 2016
 */
public class ForecastUtils {
    public static int getWindImage(int direction, double velocity) {
        final SparseIntArray GREEN_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_green01);
                append(2, R.drawable.arrow_green02);
                append(3, R.drawable.arrow_green03);
                append(4, R.drawable.arrow_green04);
                append(5, R.drawable.arrow_green05);
                append(6, R.drawable.arrow_green06);
                append(7, R.drawable.arrow_green07);
                append(8, R.drawable.arrow_green08);
            }
        };
        final SparseIntArray BLUE_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_blue01);
                append(2, R.drawable.arrow_blue02);
                append(3, R.drawable.arrow_blue03);
                append(4, R.drawable.arrow_blue04);
                append(5, R.drawable.arrow_blue05);
                append(6, R.drawable.arrow_blue06);
                append(7, R.drawable.arrow_blue07);
                append(8, R.drawable.arrow_blue08);
            }
        };
        final SparseIntArray RED_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_red01);
                append(2, R.drawable.arrow_red02);
                append(3, R.drawable.arrow_red03);
                append(4, R.drawable.arrow_red04);
                append(5, R.drawable.arrow_red05);
                append(6, R.drawable.arrow_red06);
                append(7, R.drawable.arrow_red07);
                append(8, R.drawable.arrow_red08);
            }
        };
        if (velocity <= 0.3) {
            return GREEN_ARROWS.get(direction);
        } else if (velocity <= 10) {
            return BLUE_ARROWS.get(direction);
        } else {
            return RED_ARROWS.get(direction);
        }
    }

    public static int getDirectionFromDegrees(double degrees) {
        if (((degrees >= 337.5) && (degrees <= 360)) || (degrees >= 0 & degrees < 22.5)) {
            return 1;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            return 2;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            return 3;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            return 4;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            return 5;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            return 6;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            return 7;
        } else if (degrees >= 292.5 && degrees < 337.5) {
            return 8;
        }
        return 0;
    }
}
