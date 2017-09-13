package com.keytalk.nextgen5.view.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  SplashScreenActivity
 * Description : Splash Screen Activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class SplashScreenActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable delayRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }
    @Override
    protected void onResume() {
        intializeUI();
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(delayRunner);
        handler = null;
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(delayRunner);
    }

    private void intializeUI() {
        handler = new Handler();
        delayRunner = new Runnable() {

            @Override
            public void run() {
                if (KeyTalkCommunicationManager.isRCCDFileExist(SplashScreenActivity.this)) {
                    Intent intent = new Intent(SplashScreenActivity.this, ServiceListingActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                    startActivity(new Intent(SplashScreenActivity.this, RCCDImportScreenActivity.class));
                }
            }
        };
        handler.postDelayed(delayRunner, AppConstants.SPLASH_DELAY_FIRST_RUN);
    }
}

