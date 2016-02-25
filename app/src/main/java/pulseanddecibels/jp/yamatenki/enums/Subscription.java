package pulseanddecibels.jp.yamatenki.enums;

import android.content.res.Resources;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.Constants;

/**
 * Created by Diarmaid Lindsay on 2015/12/11.
 * Copyright Pulse and Decibels 2015
 */
public enum Subscription {
    FREE("", 0),
    YEARLY(Constants.SUBSCRIPTION_1_YEAR_SKU, R.string.one_year),
    MONTH6(Constants.SUBSCRIPTION_6_MONTH_SKU, R.string.six_months),
    MONTHLY(Constants.SUBSCRIPTION_1_MONTH_SKU, R.string.one_month),
    //price changed middle of February 2016, we're keeping these around in case there are customers who bought at old price.
    YEARLY_Q1_2016(Constants.SUBSCRIPTION_2016_Q1_1_YEAR_SKU, R.string.one_year),
    MONTH6_Q1_2016(Constants.SUBSCRIPTION_2016_Q1_6_MONTH_SKU, R.string.six_months),
    MONTHLY_Q1_2016(Constants.SUBSCRIPTION_2016_Q1_1_MONTH_SKU, R.string.one_month);

    private String sku;
    private int displayTextId;

    Subscription(String sku, int displayTextId) {
        this.sku = sku;
        this.displayTextId = displayTextId;
    }

    public String getSku() {
        return sku;
    }

    public static Subscription getSubscriptionTypeForSKU(String sku) {
        for(Subscription sub : values()) {
            if(sub.getSku().equals(sku)) {
                return sub;
            }
        }
        return FREE;
    }

    public String getDisplaytext(Resources resources) {
        if (this.displayTextId == 0) return "";
        return resources.getString(this.displayTextId);
    }
}
