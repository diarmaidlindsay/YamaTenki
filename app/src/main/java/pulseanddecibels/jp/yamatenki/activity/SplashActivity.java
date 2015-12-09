package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.Settings;

/**
 * Created by Diarmaid Lindsay on 2015/09/24.
 * Copyright Pulse and Decibels 2015
 */
public class SplashActivity extends Activity {

    private boolean databaseTaskRunning = false;
    private boolean licenseAgreementDialogOpen = false;
    private boolean agreeButtonPressed = false;
    Settings settings;

    static {
        //change to Japan Time Zone
        DateUtils.setDefaultTimeZone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(this);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());
        goFullScreen();

        if (settings.isFirstTimeRun()) {
            new DatabaseSetupTask().execute();
            writeDefaultSettings();
        } else {
            displayNormalSplash();
        }

        if(!settings.isAgreedToLicense()) {
            displayUserLicenseAgreement();
        }
    }

    private void displayNormalSplash() {
        final int SPLASH_TIME_OUT = 3000;

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (!licenseAgreementDialogOpen && settings.isAgreedToLicense()) {
                    startMainActivity();
                } else if(!licenseAgreementDialogOpen) {
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    private void displayUserLicenseAgreement() {
        final Dialog dialog = new Dialog(SplashActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_license_agreement);
        dialog.setTitle(getString(R.string.text_license_agreement_title));
        dialog.setCanceledOnTouchOutside(false);
        Drawable d = new ColorDrawable(ContextCompat.getColor(this, R.color.yama_brown));
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.show();
        licenseAgreementDialogOpen = true;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!agreeButtonPressed) {
                    licenseAgreementDialogOpen = false;
                    settings.setAgreedToLicense(false);
                    if (!databaseTaskRunning) {
                        finish();
                    }
                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                licenseAgreementDialogOpen = false;
                settings.setAgreedToLicense(false);
                if (!databaseTaskRunning) {
                    finish();
                }
            }
        });

        final Button agreeButton = (Button) dialog.findViewById(R.id.agree_button);
        agreeButton.setEnabled(false);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setAgreedToLicense(true);
                agreeButtonPressed = true;
                dialog.dismiss();
                if (!databaseTaskRunning) {
                    startMainActivity();
                }
            }
        });
        Button disagreeButton = (Button) dialog.findViewById(R.id.disagree_button);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setAgreedToLicense(false);
                dialog.dismiss();
                if(!databaseTaskRunning) {
                    finish();
                }
            }
        });

        final CheckBox agreementCheckbox = (CheckBox) dialog.findViewById(R.id.agreement_checkbox);
        agreementCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox) v;
                if(checkbox.isChecked()) {
                    agreeButton.setVisibility(View.VISIBLE);
                    agreeButton.setEnabled(true);
                } else {
                    agreeButton.setVisibility(View.INVISIBLE);
                    agreeButton.setEnabled(false);
                }
            }
        });

        TextView agreementLabel = (TextView) dialog.findViewById(R.id.agreement_label);
        agreementLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreementCheckbox.setChecked(!agreementCheckbox.isChecked());
                if(agreementCheckbox.isChecked()) {
                    agreeButton.setVisibility(View.VISIBLE);
                    agreeButton.setEnabled(true);
                } else {
                    agreeButton.setVisibility(View.INVISIBLE);
                    agreeButton.setEnabled(false);
                }
            }
        });
    }

    private void goFullScreen() {
        if (Build.VERSION.SDK_INT < 19) { //19 or above api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for lower api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void writeDefaultSettings() {
        settings.setSetting("setting_display_warning", true);
        settings.setSetting("setting_download_mobile", true);
        settings.setSetting("setting_reset_checklist", false);
    }

    private void startMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private class DatabaseSetupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            databaseTaskRunning = true;
            Database.initialiseData(SplashActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            databaseTaskRunning = false;
            if (!licenseAgreementDialogOpen && settings.isAgreedToLicense()) {
                startMainActivity();
            } else if(!licenseAgreementDialogOpen) {
                finish();
            }
            //else do nothing, dialog is still open for user to make a choice
        }
    }
}
