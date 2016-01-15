package pulseanddecibels.jp.yamatenki.enums;

import pulseanddecibels.jp.yamatenki.utils.Constants;

/**
 * Created by Diarmaid Lindsay on 2015/12/11.
 * Copyright Pulse and Decibels 2015
 */
public enum Subscription {
    FREE("", "No Subscription"),
    YEARLY(Constants.SUBSCRIPTION_1_YEAR_SKU, "1 year"),
    MONTH6(Constants.SUBSCRIPTION_6_MONTH_SKU, "6 month"),
    MONTHLY(Constants.SUBSCRIPTION_1_MONTH_SKU, "1 month");

    private String sku;
    private String displaytext;

    Subscription(String sku, String displayText) {
        this.sku = sku;
        this.displaytext = displayText;
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

    public String getDisplaytext() {
        return displaytext;
    }
}
