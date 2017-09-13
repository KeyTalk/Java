package com.keytalk.nextgen5.core.security;

import android.app.Activity;
import android.os.Handler;

/*
 * Class  :  CommunicationViewHelper
 * Description : Abstract class for keeping required information's during request
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public abstract class CommunicationViewHelper  extends Handler {

    private Activity context;

    protected CommunicationViewHelper() {

    }

    protected CommunicationViewHelper(Activity context) {
        this.context = context;
    }

    protected abstract void initialize();

    protected Activity getContext() {
        return context;
    }

    protected void setContext(Activity context) {
        this.context = context;
    }

}
