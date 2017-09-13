package com.keytalk.nextgen5.core.security;

import java.io.Serializable;

import com.keytalk.nextgen5.core.Data;

/*
 * Class  :  RCCDFileRequestData
 * Description : Support Class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDFileRequestData implements Data, Serializable {

    private static final long serialVersionUID = 1L;

    private String rccdRequestURL;

    protected void setURL(String rccdRequestURL) {
        this.rccdRequestURL = rccdRequestURL;
    }

    protected String getURL() {
        return this.rccdRequestURL;
    }

}
