package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.enums.Subscription;
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
public class SettingsActivity extends Activity implements IabHelper.OnIabPurchaseFinishedListener {

    Settings settings;
    TextView subscriptionSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(this);
        SubscriptionSingleton.getInstance(this).initGoogleBillingApi(this);
        setContentView(R.layout.activity_settings);

        TextView header = (TextView) findViewById(R.id.text_settings_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        PercentRelativeLayout subscriptionSetting = (PercentRelativeLayout) findViewById(R.id.setting_subscription);
        initialiseSubscription(subscriptionSetting);
        PercentRelativeLayout displayWarningSetting = (PercentRelativeLayout) findViewById(R.id.setting_dont_display_warning);
        initialiseSettingCheckedNoSubtitle(displayWarningSetting, R.string.text_setting_title_warning, "setting_dont_display_warning");
        PercentRelativeLayout downloadMobileSetting = (PercentRelativeLayout) findViewById(R.id.setting_download_only_wifi);
        initialiseSettingCheckedNoSubtitle(downloadMobileSetting, R.string.text_setting_title_mobile, "setting_download_only_wifi");
        PercentRelativeLayout resetChecklistSetting = (PercentRelativeLayout) findViewById(R.id.setting_reset_checklist);
        initialiseSettingCheckedNoSubtitle(resetChecklistSetting, R.string.text_setting_title_checklist_reset, "setting_reset_checklist");
    }

    private void initialiseSubscription(PercentRelativeLayout setting) {
        final IabHelper mBillingHelper = SubscriptionSingleton.getInstance(this).getIabHelperInstance(this);
        TextView title = (TextView) setting.findViewById(R.id.setting_title);
        title.setText(getResources().getString(R.string.text_setting_subscription_title));
        subscriptionSubtitle = (TextView) setting.findViewById(R.id.setting_subtitle);
        subscriptionSubtitle.setText(SubscriptionSingleton.getInstance(this).getSubscriptionStatus());
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.setContentView(R.layout.dialog_subscription);
                dialog.setCanceledOnTouchOutside(true);
                Button month1Sub = (Button) dialog.findViewById(R.id.button_1_month_sub);
                month1Sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBillingHelper.launchSubscriptionPurchaseFlow(SettingsActivity.this, Subscription.MONTHLY.getSku(), 100, SettingsActivity.this);
                        dialog.dismiss();
                    }
                });
                Button month6Sub = (Button) dialog.findViewById(R.id.button_6_month_sub);
                month6Sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBillingHelper.launchSubscriptionPurchaseFlow(SettingsActivity.this, Subscription.MONTH6.getSku(), 100, SettingsActivity.this);
                        dialog.dismiss();
                    }
                });
                Button year1Sub = (Button) dialog.findViewById(R.id.button_1_year_sub);
                year1Sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBillingHelper.launchSubscriptionPurchaseFlow(SettingsActivity.this, Subscription.YEARLY.getSku(), 100, SettingsActivity.this);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
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
            Toast.makeText(this, SubscriptionSingleton.getInstance(this).getSubscriptionStatus(), Toast.LENGTH_SHORT).show();
            if(subscriptionSubtitle != null) {
                subscriptionSubtitle.setText(SubscriptionSingleton.getInstance(this).getSubscriptionStatus());
            }
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionSingleton.getInstance(this).disposeIabHelperInstance(this);
    }
}