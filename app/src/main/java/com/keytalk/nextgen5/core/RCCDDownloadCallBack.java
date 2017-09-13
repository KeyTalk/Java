package com.keytalk.nextgen5.core;

/*
 * Class  :  RCCDDownloadCallBack
 * Description : Interface for update the status of rccd download request
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface RCCDDownloadCallBack {
    /*
     * RCCD Download call back method in Activity
     */
    void rccdDownloadCallBack(String providerName, int serviceCount, int downloadStatus);

}
