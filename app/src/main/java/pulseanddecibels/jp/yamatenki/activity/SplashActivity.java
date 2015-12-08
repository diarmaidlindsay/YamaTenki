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
import android.view.Window;
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
    private boolean firstTimeDialogOpen = false;

    static {
        //change to Japan Time Zone
        DateUtils.setDefaultTimeZone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());
        goFullScreen();

        if (new Settings(this).isFirstTimeRun()) {
            new DatabaseSetupTask().execute();
            writeDefaultSettings();
            displayFirstTimeMessage();
        } else {
            displayNormalSplash();
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
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void displayFirstTimeMessage() {
        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
        helpText.setText(R.string.text_first_time_run_message);
        dialog.setCanceledOnTouchOutside(true);
        Drawable d = new ColorDrawable(ContextCompat.getColor(this, R.color.yama_brown));
        d.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.show();
        firstTimeDialogOpen = true;
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                firstTimeDialogOpen = false;
                if (!databaseTaskRunning) {
                    startMainActivity();
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
        Settings settings = new Settings(this);
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
            if (!firstTimeDialogOpen) {
                startMainActivity();
            }
        }
    }
}
