package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.enums.Subscription;
import pulseanddecibels.jp.yamatenki.interfaces.OnInAppBillingServiceSetupComplete;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.SubscriptionSingleton;
import pulseanddecibels.jp.yamatenki.utils.Utils;
import pulseanddecibels.jp.yamatenki.utils.billing.IabHelper;
import pulseanddecibels.jp.yamatenki.utils.billing.IabResult;
import pulseanddecibels.jp.yamatenki.utils.billing.Purchase;

/**
 * Created by Diarmaid Lindsay on 2015/11/20.
 * Copyright Pulse and Decibels 2015
 */
public class SettingsActivity extends Activity implements IabHelper.OnIabPurchaseFinishedListener, OnInAppBillingServiceSetupComplete {

    Settings settings;
    TextView subscriptionSubtitle;
    PercentRelativeLayout subscriptionSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(this);
        setContentView(R.layout.activity_settings);
        TextView header = (TextView) findViewById(R.id.text_settings_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        PercentRelativeLayout displayWarningSetting = (PercentRelativeLayout) findViewById(R.id.setting_dont_display_warning);
        initialiseSettingCheckedNoSubtitle(displayWarningSetting, R.string.text_setting_title_warning, "setting_dont_display_warning");
        PercentRelativeLayout downloadMobileSetting = (PercentRelativeLayout) findViewById(R.id.setting_download_only_wifi);
        initialiseSettingCheckedNoSubtitle(downloadMobileSetting, R.string.text_setting_title_mobile, "setting_download_only_wifi");
        PercentRelativeLayout resetChecklistSetting = (PercentRelativeLayout) findViewById(R.id.setting_reset_checklist);
        initialiseSettingCheckedNoSubtitle(resetChecklistSetting, R.string.text_setting_title_checklist_reset, "setting_reset_checklist");
        subscriptionSetting = (PercentRelativeLayout) findViewById(R.id.setting_subscription);
        initialiseSubscription(subscriptionSetting);
        SubscriptionSingleton.getInstance(this).initGoogleBillingApi(this, this);
    }

    private void initialiseSubscription(PercentRelativeLayout setting) {
        TextView title = (TextView) setting.findViewById(R.id.setting_title);
        title.setText(getResources().getString(R.string.text_setting_subscription_title));
        subscriptionSubtitle = (TextView) setting.findViewById(R.id.setting_subtitle);
        Button subscriptionPlanButton = (Button) setting.findViewById(R.id.button_view_subscription_plans);
        subscriptionPlanButton.setOnClickListener(getSubscriptionOnClickListener());
        setting.setOnClickListener(getSubscriptionOnClickListener());
    }

    private View.OnClickListener getSubscriptionOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_subscription);
                dialog.setCanceledOnTouchOutside(true);
                Drawable d = new ColorDrawable(ContextCompat.getColor(SettingsActivity.this, R.color.yama_brown));
                d.setAlpha(200);
                dialog.getWindow().setBackgroundDrawable(d);

                ViewGroup month1Sub = (ViewGroup) dialog.findViewById(R.id.item_1_month_sub);
                ViewGroup month6Sub = (ViewGroup) dialog.findViewById(R.id.item_6_month_sub);
                ViewGroup yearSub = (ViewGroup) dialog.findViewById(R.id.item_1_year_sub);

                initialiseSubscriptionListItem(month1Sub, getString(R.string.one_month), "240", "240", R.drawable.discount0, R.color.sub_1month);
                initialiseSubscriptionListItem(month6Sub, getString(R.string.six_months), "120", "720", R.drawable.discount50, R.color.sub_6month);
                initialiseSubscriptionListItem(yearSub, getString(R.string.one_year), "100", "1,200", R.drawable.discount58, R.color.sub_year);

                month1Sub.setOnClickListener(getSubButtonOnClickListener(Subscription.MONTHLY.getSku(), dialog));
                month6Sub.setOnClickListener(getSubButtonOnClickListener(Subscription.MONTH6.getSku(), dialog));
                yearSub.setOnClickListener(getSubButtonOnClickListener(Subscription.YEARLY.getSku(), dialog));

                //show google help on how to unsubscribe when "Help" is pressed in the TextView
                Pattern pattern;
                TextView subscriptionHelp = (TextView) dialog.findViewById(R.id.subscription_help);

                //Make the "help" text clickable
                if(Utils.isEnglishLocale(SettingsActivity.this)) {
                    pattern = Pattern.compile("Help");
                    Linkify.addLinks(subscriptionHelp, pattern, "https://support.google.com/googleplay/answer/2476088?hl=en");
                } else {
                    pattern = Pattern.compile("ヘルプ");
                    Linkify.addLinks(subscriptionHelp, pattern, "https://support.google.com/googleplay/answer/2476088?hl=ja");
                }

                dialog.show();
            }
        };
    }

    private View.OnClickListener getSubButtonOnClickListener(final String sku, final Dialog dialog) {
        final IabHelper mBillingHelper = SubscriptionSingleton.getInstance(this).getIabHelperInstance(this);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBillingHelper.subscriptionsSupported()) {
                    mBillingHelper.launchSubscriptionPurchaseFlow(SettingsActivity.this, sku, 100, SettingsActivity.this);
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_google_account_not_setup), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        };
    }

    private void initialiseSubscriptionListItem(ViewGroup subItem, String periodText, String monthlyPriceText, String periodPriceText, Integer discountImageId, Integer colorId) {
        TextView period = (TextView) subItem.findViewById(R.id.period);
        TextView monthlyPrice = (TextView) subItem.findViewById(R.id.price1);
        TextView periodPrice = (TextView) subItem.findViewById(R.id.price2);

        if(Utils.isEnglishLocale(this)) {
            period.setText(periodText);
            LinearLayout subscriptionBlock = (LinearLayout) subItem.findViewById(R.id.subscription_block);
            subscriptionBlock.setBackgroundColor(ContextCompat.getColor(this, colorId));
        } else {
            LinearLayout subscriptionPeriod = (LinearLayout) subItem.findViewById(R.id.subscription_period);
            subscriptionPeriod.setBackgroundColor(ContextCompat.getColor(this, colorId));
            //number was smaller than kanji so I used span to even it out
            final SpannableString periodSpan = new SpannableString(periodText);
            periodSpan.setSpan(new TextAppearanceSpan(this, R.style.LargestSizeText), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            period.setText(periodSpan, TextView.BufferType.SPANNABLE);
        }

        if(discountImageId == null) {
            monthlyPriceText = String.format(getString(R.string.text_monthly_cost), monthlyPriceText);
        } else {
            monthlyPriceText = String.format(getString(R.string.text_monthly_cost), monthlyPriceText);
        }
        final SpannableString monthlyPriceTextSpan = new SpannableString(monthlyPriceText);
        //all prices different lengths so...
        int indexOfYen = monthlyPriceText.indexOf("¥");
        int indexOfSlash = monthlyPriceText.indexOf("/");
        monthlyPriceTextSpan.setSpan(new TextAppearanceSpan(this, R.style.VeryLargeText), indexOfYen + 1, indexOfSlash, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        monthlyPrice.setText(monthlyPriceTextSpan, TextView.BufferType.SPANNABLE);


        periodPriceText = String.format(getString(R.string.text_payment_amount), periodPriceText);
        if(Utils.isEnglishLocale(this)) {
            periodPriceText = periodPriceText.replace("¥", "\n¥");
        }
        periodPrice.setText(periodPriceText);
        if(discountImageId != null) {
            ImageView imageDiscount = (ImageView) subItem.findViewById(R.id.image_discount);
            imageDiscount.setImageResource(discountImageId);
            imageDiscount.setScaleType(ImageView.ScaleType.FIT_START);
        }
    }

    /**
     * Settings with checkboxes and no subtitle
     */
    private void initialiseSettingCheckedNoSubtitle(PercentRelativeLayout setting, int titleId, final String settingId) {
        final boolean value = settings.getSetting(settingId);
        final CheckBox checkbox = (CheckBox) setting.findViewById(R.id.setting_checkbox);
        checkbox.setChecked(value);
        TextView title = (TextView) setting.findViewById(R.id.setting_title);
        title.setText(getResources().getString(titleId));
        View.OnClickListener toggleCheck = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setSetting(settingId, !settings.getSetting(settingId));
                checkbox.setChecked(settings.getSetting(settingId));
            }
        };
        setting.setOnClickListener(toggleCheck);
        checkbox.setOnClickListener(toggleCheck);
    }

    /**
     * Settings with checkboxes and subtitle
     */
    private void initialiseSettingChecked(PercentRelativeLayout setting, int titleId, final String subtitleIdString, final String settingId) {
        final boolean value = settings.getSetting(settingId);
        final CheckBox checkbox = (CheckBox) setting.findViewById(R.id.setting_checkbox);
        checkbox.setChecked(value);
        TextView title = (TextView) setting.findViewById(R.id.setting_title);
        title.setText(getResources().getString(titleId));
        final TextView subTitle = (TextView) setting.findViewById(R.id.setting_subtitle);
        int subtitleId = getResources().getIdentifier(subtitleIdString + value, "string", getPackageName());
        subTitle.setText(getResources().getString(subtitleId));
        View.OnClickListener toggleCheck = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setSetting(settingId, !settings.getSetting(settingId));
                checkbox.setChecked(settings.getSetting(settingId));
                int subtitleId = getResources().getIdentifier(subtitleIdString + settings.getSetting(settingId), "string", getPackageName());
                subTitle.setText(getResources().getString(subtitleId));
            }
        };
        setting.setOnClickListener(toggleCheck);
        checkbox.setOnClickListener(toggleCheck);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!SubscriptionSingleton.getInstance(this).getIabHelperInstance(this).handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
            Log.d(this.getClass().getSimpleName(), "onActivityResult not handled by IABUtil.");
        }
        else {
            Log.d(this.getClass().getSimpleName(), "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if(result.isSuccess()) {
            SubscriptionSingleton.getInstance(this).setPurchase(info);
            SubscriptionSingleton.getInstance(this).setSubscription(Subscription.getSubscriptionTypeForSKU(info.getSku()));
            if(Settings.isDebugMode())
            Toast.makeText(this, SubscriptionSingleton.getInstance(this).getSubscriptionStatus(), Toast.LENGTH_SHORT).show();
            if(subscriptionSubtitle != null) {
                subscriptionSubtitle.setText(SubscriptionSingleton.getInstance(this).getSubscriptionStatus());
            }
        } else {
            if(Settings.isDebugMode())
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionSingleton.getInstance(this).disposeIabHelperInstance(this);
    }

    @Override
    public void iabSetupCompleted(Subscription subscription) {
        subscriptionSubtitle.setText(SubscriptionSingleton.getInstance(this).getSubscriptionStatus());

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            boolean viewSub = arguments.getBoolean("view_subscription");
            //if user came here from restrictions dialog intent, open subscription screen directly
            if(viewSub) {
                subscriptionSetting.callOnClick();
            }
        }

    }
}