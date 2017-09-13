package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

/*
 * Class  :  SelectedRCCDFileCertificateResponseData
 * Description : Class which holding selected rccd file certificate details
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class SelectedRCCDFileCertificateResponseData implements Data, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String url;
    private SSLContext sslContext;
    private PrivateKey key;
    private X509Certificate[] keyChain;

    private String serviceName;

    private String[] serverMsg;

    /**
     * @return the url
     */
    protected String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    protected void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return the sslContext
     */
    protected SSLContext getSslContext() {
        return sslContext;
    }
    /**
     * @param sslContext the sslContext to set
     */
    protected void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    /**
     * @return the key
     */
    protected PrivateKey getKey() {
        return key;
    }
    /**
     * @param key the key to set
     */
    protected void setKey(PrivateKey key) {
        this.key = key;
    }
    /**
     * @return the keyChain
     */
    protected X509Certificate[] getKeyChain() {
        return keyChain;
    }
    /**
     * @param keyChain the keyChain to set
     */
    protected void setKeyChain(X509Certificate[] keyChain) {
        this.keyChain = keyChain;
    }
    protected String getServiceName() {
        return serviceName;
    }
    protected void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    protected String[] getServerMsg() {
        return serverMsg;
    }
    protected void setServerMsg(String[] serverMsg) {
        this.serverMsg = serverMsg;
    }


}

