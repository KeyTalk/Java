package com.keytalk.nextgen5.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.keytalk.nextgen5.core.security.CommunicationLooper;

/*
 * Class  :  KeyTalkApplication
 * Description : Application class which initiate thread queue when application starts by user.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkApplication extends MultiDexApplication {


    private float density;

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
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.appHttpQueue.start();
        theInstance = this;
        density = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.appHttpQueue.requestStop();
    }

    public float getDensity() {
        return density;
    }

}
