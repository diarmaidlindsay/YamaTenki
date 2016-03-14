import org.junit.Test;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.ForecastUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Diarmaid Lindsay on 2016/03/09.
 * Copyright Pulse and Decibels 2016
 */
public class ForecastTests {

    /**
     0=北(North)
     90=東(East)
     180=南(South)
     270=西(West)
     */
    @Test
    public void testWindDirection() {
        assertEquals(ForecastUtils.getDirectionFromDegrees(337.5), 1);
        assertEquals(ForecastUtils.getDirectionFromDegrees(360), 1);
        assertEquals(ForecastUtils.getDirectionFromDegrees(22.4), 1);
        assertEquals(ForecastUtils.getDirectionFromDegrees(357.99), 1);

        assertEquals(ForecastUtils.getDirectionFromDegrees(22.5), 2);
        assertEquals(ForecastUtils.getDirectionFromDegrees(45), 2);
        assertEquals(ForecastUtils.getDirectionFromDegrees(67.4), 2);

        assertEquals(ForecastUtils.getDirectionFromDegrees(67.5), 3);
        assertEquals(ForecastUtils.getDirectionFromDegrees(90), 3);
        assertEquals(ForecastUtils.getDirectionFromDegrees(112.4), 3);

        assertEquals(ForecastUtils.getDirectionFromDegrees(112.5), 4);
        assertEquals(ForecastUtils.getDirectionFromDegrees(135), 4);
        assertEquals(ForecastUtils.getDirectionFromDegrees(157.4), 4);

        assertEquals(ForecastUtils.getDirectionFromDegrees(157.5), 5);
        assertEquals(ForecastUtils.getDirectionFromDegrees(180), 5);
        assertEquals(ForecastUtils.getDirectionFromDegrees(202.4), 5);

        assertEquals(ForecastUtils.getDirectionFromDegrees(202.5), 6);
        assertEquals(ForecastUtils.getDirectionFromDegrees(225), 6);
        assertEquals(ForecastUtils.getDirectionFromDegrees(247.4), 6);

        assertEquals(ForecastUtils.getDirectionFromDegrees(247.5), 7);
        assertEquals(ForecastUtils.getDirectionFromDegrees(270), 7);
        assertEquals(ForecastUtils.getDirectionFromDegrees(292.4), 7);

        assertEquals(ForecastUtils.getDirectionFromDegrees(292.5), 8);
        assertEquals(ForecastUtils.getDirectionFromDegrees(315), 8);
        assertEquals(ForecastUtils.getDirectionFromDegrees(337.4), 8);
    }

    public void testWindImage() {
        assertEquals(R.drawable.arrow_green01, ForecastUtils.getWindImage(1, 0));
        assertEquals(R.drawable.arrow_green01, ForecastUtils.getWindImage(1, 0.3));
        assertEquals(R.drawable.arrow_blue01, ForecastUtils.getWindImage(1, 1.3));
        assertEquals(R.drawable.arrow_blue01, ForecastUtils.getWindImage(1, 10));
        assertEquals(R.drawable.arrow_red01, ForecastUtils.getWindImage(1, 10.1));
        assertEquals(R.drawable.arrow_red01, ForecastUtils.getWindImage(1, 30));

        assertEquals(R.drawable.arrow_green02, ForecastUtils.getWindImage(2, 0));
        assertEquals(R.drawable.arrow_green02, ForecastUtils.getWindImage(2, 0.3));
        assertEquals(R.drawable.arrow_blue02, ForecastUtils.getWindImage(2, 1.3));
        assertEquals(R.drawable.arrow_blue02, ForecastUtils.getWindImage(2, 10));
        assertEquals(R.drawable.arrow_red02, ForecastUtils.getWindImage(2, 10.1));
        assertEquals(R.drawable.arrow_red02, ForecastUtils.getWindImage(2, 30));

        assertEquals(R.drawable.arrow_green03, ForecastUtils.getWindImage(3, 0));
        assertEquals(R.drawable.arrow_green03, ForecastUtils.getWindImage(3, 0.3));
        assertEquals(R.drawable.arrow_blue03, ForecastUtils.getWindImage(3, 1.3));
        assertEquals(R.drawable.arrow_blue03, ForecastUtils.getWindImage(3, 10));
        assertEquals(R.drawable.arrow_red03, ForecastUtils.getWindImage(3, 10.1));
        assertEquals(R.drawable.arrow_red03, ForecastUtils.getWindImage(3, 30));

        assertEquals(R.drawable.arrow_green04, ForecastUtils.getWindImage(4, 0));
        assertEquals(R.drawable.arrow_green04, ForecastUtils.getWindImage(4, 0.3));
        assertEquals(R.drawable.arrow_blue04, ForecastUtils.getWindImage(4, 1.3));
        assertEquals(R.drawable.arrow_blue04, ForecastUtils.getWindImage(4, 10));
        assertEquals(R.drawable.arrow_red04, ForecastUtils.getWindImage(4, 10.1));
        assertEquals(R.drawable.arrow_red04, ForecastUtils.getWindImage(4, 30));

        assertEquals(R.drawable.arrow_green05, ForecastUtils.getWindImage(5, 0));
        assertEquals(R.drawable.arrow_green05, ForecastUtils.getWindImage(5, 0.3));
        assertEquals(R.drawable.arrow_blue05, ForecastUtils.getWindImage(5, 1.3));
        assertEquals(R.drawable.arrow_blue05, ForecastUtils.getWindImage(5, 10));
        assertEquals(R.drawable.arrow_red05, ForecastUtils.getWindImage(5, 10.1));
        assertEquals(R.drawable.arrow_red05, ForecastUtils.getWindImage(5, 30));

        assertEquals(R.drawable.arrow_green06, ForecastUtils.getWindImage(6, 0));
        assertEquals(R.drawable.arrow_green06, ForecastUtils.getWindImage(6, 0.3));
        assertEquals(R.drawable.arrow_blue06, ForecastUtils.getWindImage(6, 1.3));
        assertEquals(R.drawable.arrow_blue06, ForecastUtils.getWindImage(6, 10));
        assertEquals(R.drawable.arrow_red06, ForecastUtils.getWindImage(6, 10.1));
        assertEquals(R.drawable.arrow_red06, ForecastUtils.getWindImage(6, 30));

        assertEquals(R.drawable.arrow_green07, ForecastUtils.getWindImage(7, 0));
        assertEquals(R.drawable.arrow_green07, ForecastUtils.getWindImage(7, 0.3));
        assertEquals(R.drawable.arrow_blue07, ForecastUtils.getWindImage(7, 1.3));
        assertEquals(R.drawable.arrow_blue07, ForecastUtils.getWindImage(7, 10));
        assertEquals(R.drawable.arrow_red07, ForecastUtils.getWindImage(7, 10.1));
        assertEquals(R.drawable.arrow_red07, ForecastUtils.getWindImage(7, 30));

        assertEquals(R.drawable.arrow_green08, ForecastUtils.getWindImage(8, 0));
        assertEquals(R.drawable.arrow_green08, ForecastUtils.getWindImage(8, 0.3));
        assertEquals(R.drawable.arrow_blue08, ForecastUtils.getWindImage(8, 1.3));
        assertEquals(R.drawable.arrow_blue08, ForecastUtils.getWindImage(8, 10));
        assertEquals(R.drawable.arrow_red08, ForecastUtils.getWindImage(8, 10.1));
        assertEquals(R.drawable.arrow_red08, ForecastUtils.getWindImage(8, 30));
    }
}
