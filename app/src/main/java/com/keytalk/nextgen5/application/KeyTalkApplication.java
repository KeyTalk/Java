package com.keytalk.nextgen5.application;

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.keytalk.nextgen5.core.security.CommunicationLooper;
import com.keytalk.nextgen5.view.util.LocaleHelper;

/*
 * Class  :  KeyTalkApplication
 * Description : Application class which initiate thread queue when application starts by user.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkApplication extends MultiDexApplication {


    private float density;
    private static Context mContext;
    private static final CommunicationLooper appHttpQueue = new CommunicationLooper();
    private static KeyTalkApplication theInstance;
    public static KeyTalkApplication getApp() {
        return theInstance;
    }
    public static CommunicationLooper getCommunicationLooper() {
        return appHttpQueue;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, LocaleHelper.getLanguage(base)));
        //super.attachBaseContext(base);
        MultiDex.install(this);
        //Utilities.checkLanguage(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.appHttpQueue.start();
        theInstance = this;
        mContext = this;
        density = getResources().getDisplayMetrics().density;
        //Utilities.checkLanguage(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.appHttpQueue.requestStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // Utilities.checkLanguage(getApplicationContext());
    }
    public float getDensity() {
        return density;
    }
    public static Context getContext(){
        return mContext;
    }

}
