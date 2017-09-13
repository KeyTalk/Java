package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

import java.io.InputStream;
import java.io.Serializable;

/*
 * Class  :  RCCDFileResponseData
 * Description : Holding all the details selected rccd file during communication
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDFileResponseData implements Data, Serializable {

    private static final long serialVersionUID = 1L;

    private ResponseHeader responseHeader;
    private InputStream emailRCCDFileInputStream;
    private String fileName;
    private IniResponseData iiniResponseData;

    protected RCCDFileResponseData() {
    }

    protected ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    protected void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    protected String getFileName() {
        return fileName;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected InputStream getEmailRCCDFileInputStream() {
        return emailRCCDFileInputStream;
    }

    protected void setEmailRCCDFileInputStream(InputStream emailRCCDFileInputStream) {
        this.emailRCCDFileInputStream = emailRCCDFileInputStream;
    }

    protected IniResponseData getIiniResponseData() {
        return iiniResponseData;
    }

    protected void setIiniResponseData(IniResponseData iiniResponseData) {
        this.iiniResponseData = iiniResponseData;
    }
}

