package pulseanddecibels.jp.yamatenki.interfaces;

import pulseanddecibels.jp.yamatenki.enums.Subscription;

/**
 * Created by Diarmaid Lindsay on 2015/12/24.
 * Copyright Pulse and Decibels 2015
 */
public interface OnInAppBillingServiceSetupComplete {
    void iabSetupCompleted(Subscription subscription);
}
