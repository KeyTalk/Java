package com.keytalk.nextgen5.core.security;

import java.io.Serializable;

import com.keytalk.nextgen5.core.Data;

import android.graphics.Bitmap;

/*
 * Class  :  RCCDFileData
 * Description : Support class for RCCD file
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */


public class RCCDFileData implements Data, Serializable {

    private static final long serialVersionUID = 1L;
    private String rccdFileName;
    private String rccdFilePath;
    private Bitmap providerIcon;
    private IniResponseData serviceData;


    /**
     * @return the rccdFileName
     */
    public String getRccdFileName() {
        return rccdFileName;
    }

    /**
     * @param rccdFileName
     *            the rccdFileName to set
     */
    protected void setRccdFileName(String rccdFileName) {
        this.rccdFileName = rccdFileName;
    }

    /**
     * @return the providerIcon
     */
    public Bitmap getProviderIcon() {
        return providerIcon;
    }

    /**
     * @param providerIcon
     *            the providerIcon to set
     */
    protected void setProviderIcon(Bitmap providerIcon) {
        this.providerIcon = providerIcon;
    }

    /**
     * @return the serviceData
     */
    public IniResponseData getServiceData() {
        return serviceData;
    }

    /**
     * @param serviceData
     *            the serviceData to set
     */
    protected void setServiceData(IniResponseData serviceData) {
        this.serviceData = serviceData;
    }

    /**
     * @return the rccdFilePath
     */
    public String getRccdFilePath() {
        return rccdFilePath;
    }

    /**
     * @param rccdFilePath
     *            the rccdFilePath to set
     */
    protected void setRccdFilePath(String rccdFilePath) {
        this.rccdFilePath = rccdFilePath;
    }

}

