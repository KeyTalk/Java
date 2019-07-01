/*
 * Class  :  SplashScreenActivity
 * Description :
 *
 * Created By Jobin Mathew on 2018
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.util.CertificateValidationJob;
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
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        context = getBaseContext();

    }
    private void scheduleJob() {
        try {
            ComponentName componentName = new ComponentName(this, CertificateValidationJob.class);
            JobInfo jobInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                jobInfo = new JobInfo.Builder(123, componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(15 * 60 * 1000)

                        .build();
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                int resultcode = scheduler.schedule(jobInfo);
                if (resultcode == JobScheduler.RESULT_SUCCESS) {
                    Log.i("Splash", "Job scheduled");
                } else {
                    Log.i("Splash", "Job scheduling failed");
                }
            }
        }catch (Exception e)
        {
            Log.i("Splash", e.getMessage());
        }

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
                    //Utilities.checkLanguage(getApplicationContext());
                    Intent intent = new Intent(SplashScreenActivity.this, ServiceListingActivity.class);
                    //Utilities.checkLanguage(getApplicationContext());
                    startActivity(intent);
                  //  scheduleJob();
                    finish();
                } else {
                    finish();
                    Intent intent=new Intent(SplashScreenActivity.this, RCCDImportScreenActivity.class);
                   // Utilities.checkLanguage(getApplicationContext());
                    intent.putExtra("REFRESH",true);
                   // scheduleJob();
                    startActivity(intent);
                }
            }
        };
        handler.postDelayed(delayRunner, AppConstants.SPLASH_DELAY_FIRST_RUN);
    }


}

