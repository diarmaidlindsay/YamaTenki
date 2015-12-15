package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.enums.Subscription;
import pulseanddecibels.jp.yamatenki.utils.billing.IabHelper;
import pulseanddecibels.jp.yamatenki.utils.billing.IabResult;
import pulseanddecibels.jp.yamatenki.utils.billing.Inventory;
import pulseanddecibels.jp.yamatenki.utils.billing.Purchase;

/**
 * Keep track of whether the user has a subscription or not
 * <p/>
 * Created by Diarmaid Lindsay on 2015/12/11.
 * Copyright Pulse and Decibels 2015
 */
public class SubscriptionSingleton {
    private static SubscriptionSingleton mInstance = null;
    private static Context mContext;
    private static String YAMA_TENKI_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyp8cuwPkJKUBqYcwbBXRivCGS2LaffdnStAItWh0Oofa2azNexmSFiyDsq2E4HP5VGFIHO6qaNaZ7vU3xql9bgmEy/bWIxVW3aXgP5RCZCxkVweWQhvWCrTaD4P/iQ/Bb7jMlgZi0xNpUVTFNGAzolMpNcB7LrseemkoSJzBaag18ulPPHQENvd2Ows1QJOP41uYjfrrE8Nrhiqdm0zDhlbgspaeETkmKsC0RGeGq3xTiJFh3qCY0qBg/h5LR28EGc4nMZS9W+ZT3jwObOceDpwcFFjwHCZuaSxloh/hV/ZNBazMRUEIXlfyBQk9xjyuZJCtZzhOD45s0pseUhuUEwIDAQAB";

    private Subscription subscription = Subscription.NONE;
    private Purchase purchase;
    private Map<Context, IabHelper> mBillingHelpers = new HashMap<>();

    private SubscriptionSingleton() {

    }

    public static SubscriptionSingleton getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new SubscriptionSingleton();
        }
        return mInstance;
    }

    public IabHelper getIabHelperInstance(Context context) {
        if (mBillingHelpers.get(context) == null) {
            mBillingHelpers.put(context, new IabHelper(context, YAMA_TENKI_KEY));
        }
        return mBillingHelpers.get(context);
    }

    public void disposeIabHelperInstance(Context context) {
        Log.d("SubscriptionSingleton", "disposeIabHelperInstance()");
        if (mBillingHelpers.get(context) != null) {
            mBillingHelpers.get(context).dispose();
            mBillingHelpers.remove(context);
        }
    }

    public Subscription getSubscription() {
        return this.subscription;
    }

    public SubscriptionSingleton setSubscription(Subscription value) {
        subscription = value;
        return mInstance;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public SubscriptionSingleton setPurchase(Purchase purchase) {
        this.purchase = purchase;
        return mInstance;
    }

    public String getSubscriptionStatus() {
        if (subscription == Subscription.NONE) {
            return "You are currently not subscribed";
        }
        String template = "You currently have a %s subscription which you purchased on %s";
        return String.format(template, subscription.getDisplaytext(), DateUtils.getMemoDateFromMillis(purchase.getPurchaseTime()));
    }

    public void initGoogleBillingApi(final Context context) {
        //TODO : Disable for production
        // enable debug logging (for a production application, you should set this to false).
        getIabHelperInstance(context).enableDebugLogging(true);
        getIabHelperInstance(context).startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(context.getClass().getSimpleName(), "Problem setting up In-app Billing: " + result);
                } else {
                    // IAB is fully set up!
                    getIabHelperInstance(context).queryInventoryAsync(mGotInventoryListener);
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
            if (getIabHelperInstance(mContext) == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(mContext.getClass().getSimpleName(), "Failed to query inventory: " + result);
                return;
            }

            Log.d(mContext.getClass().getSimpleName(), "Query inventory was successful.");
            for (Subscription subscriptionType : Subscription.values()) {
                //don't bother querying google for our made up "NONE" subscription type
                if (subscriptionType != Subscription.NONE) {
                    Purchase purchase = inventory.getPurchase(subscriptionType.getSku());
                    if (purchase != null) {
                        setSubscription(subscriptionType)
                                .setPurchase(purchase);
                        //Toast.makeText(mContext, "Subscription check finished, user subscribed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            if (getSubscription() == Subscription.NONE) {
                //Toast.makeText(mContext, "Subscription check finished, user NOT subscribed", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
