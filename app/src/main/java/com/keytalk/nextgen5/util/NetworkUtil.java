package com.keytalk.nextgen5.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Class  :  NetworkUtil
 * Description : Class which will return the status of network(mobile/wifi) availability.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public final class NetworkUtil {

    private static NetworkUtil networkUtilInstance;
    private Context context;
    private boolean wifiConnected;
    private boolean mobileNetworkConnected;

    private NetworkUtil(Context context) {
        this.context = context;
    }

    public static NetworkUtil getInstance(Context app) {
        if (networkUtilInstance == null) {
            networkUtilInstance = new NetworkUtil(app);
        }
        networkUtilInstance.refreshConnectionState();
        return networkUtilInstance;
    }

    public void refreshConnectionState() {
        mobileNetworkConnected = false;
        wifiConnected = false;
        checkMobileNetworkState();
        checkWifiNetworkState();
    }

    public boolean isWifiConnected(boolean refreshNow) {
        if (refreshNow) {
            checkMobileNetworkState();
        }
        return wifiConnected;
    }

    public boolean isMobileNetworkConnected(boolean refreshNow) {
        if (refreshNow) {
            checkMobileNetworkState();
        }
        return mobileNetworkConnected;
    }

    private void checkMobileNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            mobileNetworkConnected = true;
            wifiConnected = false;
        }
    }

    private void checkWifiNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            wifiConnected = true;
            mobileNetworkConnected = false;
        }
    }

    /**
     * Get the network state
     *
     * @return true if mobile(data) or wifi is connected.
     */
    public boolean isNetworkAvailable(boolean refreshNow) {
        if (refreshNow) {
            refreshConnectionState();
        }
        return wifiConnected || mobileNetworkConnected;
    }

}
