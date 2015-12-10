package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/11/20.
 * Copyright Pulse and Decibels 2015
 */
public class SettingsActivity extends Activity {

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(this);
        setContentView(R.layout.activity_settings);

        TextView header = (TextView) findViewById(R.id.text_settings_header);
        header.setTypeface(Utils.getHannariTypeFace(this));
        PercentRelativeLayout subscriptionSetting = (PercentRelativeLayout) findViewById(R.id.setting_subscription);
        initialiseSetting(subscriptionSetting, R.string.text_setting_subscription_title, "text_setting_subscription_subtitle_", "setting_subscription");
        PercentRelativeLayout displayWarningSetting = (PercentRelativeLayout) findViewById(R.id.setting_dont_display_warning);
        initialiseSettingCheckedNoSubtitle(displayWarningSetting, R.string.text_setting_title_warning, "setting_dont_display_warning");
        PercentRelativeLayout downloadMobileSetting = (PercentRelativeLayout) findViewById(R.id.setting_download_only_wifi);
        initialiseSettingCheckedNoSubtitle(downloadMobileSetting, R.string.text_setting_title_mobile, "setting_download_only_wifi");
        PercentRelativeLayout resetChecklistSetting = (PercentRelativeLayout) findViewById(R.id.setting_reset_checklist);
        initialiseSettingCheckedNoSubtitle(resetChecklistSetting, R.string.text_setting_title_checklist_reset, "setting_reset_checklist");
    }

    private void initialiseSetting(PercentRelativeLayout setting, int titleId, String subtitleIdString, String settingId) {
        boolean value = settings.getSetting(settingId);
        TextView title = (TextView) setting.findViewById(R.id.setting_title);
        title.setText(getResources().getString(titleId));
        TextView subTitle = (TextView) setting.findViewById(R.id.setting_subtitle);
        int subtitleId = getResources().getIdentifier(subtitleIdString + value, "string", getPackageName());
        subTitle.setText(getResources().getString(subtitleId));
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
}