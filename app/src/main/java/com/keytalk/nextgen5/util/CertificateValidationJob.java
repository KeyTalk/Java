/*
 * Class  :  CertificateValidationJob
 * Description :
 *
 * Created By Jobin Mathew on 2019
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.util;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.keytalk.nextgen5.core.security.KeyTalkSettings;
import com.keytalk.nextgen5.view.util.AppConstants;

import static com.keytalk.nextgen5.core.security.RCCDAuthenticationCommand.mContext;

/**
 * Created by SrashtiG on 4/9/2019.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CertificateValidationJob extends JobService {
    boolean jobCancelled =false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("Job","Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
               // SelectedRCCDFileRequestData requestData = (SelectedRCCDFileRequestData) request.getData();
                Toast.makeText(getApplicationContext(), "Job Service running",
                        Toast.LENGTH_SHORT).show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
               for(int i=0;i<10;i++)
               {
                   KeyTalkSettings keyTalkSettings=new KeyTalkSettings();
                   SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
                   String someStringSet=null;
                   if(pref!=null)
                   {
                       someStringSet = pref.getString(AppConstants.REQUEST_KEY_LIST,"");
                   }
                   if(someStringSet!=null) {
                       String[] requestList = someStringSet.split(",");
                       for (int k = 0; k < requestList.length; k++) {
                           String requestUrl = pref.getString(requestList[k], "");
                           keyTalkSettings.validCertAvailable(requestUrl, someStringSet);
                       }
                   }
                   //Toast.makeText(getApplicationContext(),"Job Service running"+i ,Toast.LENGTH_SHORT).show();
                   Log.i("Job", "run :"+i);
                   if(jobCancelled)
                   {
                       return;
                   }
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               Log.i("Job","Job Finished");
               jobFinished(params,true);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("Job","Job cancelled before completion");
        jobCancelled=true;
        return true;
    }
}
