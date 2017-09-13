package com.keytalk.nextgen5.core.security;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 * Class  :  CertificateInfo
 * Description : Holding information of received certificate
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class CertificateInfo {
    private final X509Certificate[] certificateChain;
    private final KeyStore keyStore;
    private final String   certPassword;

    private boolean isURL = false;
    private String urlString = null;

    protected CertificateInfo(X509Certificate[] certificateChain, KeyStore keyStore, String certPassword) {
        this.certificateChain = certificateChain;
        this.keyStore         = keyStore;
        this.certPassword     = certPassword;
    }

    protected CertificateInfo(boolean isURL, String urlString) {
        this.setURL(isURL);
        this.setUrlString(urlString);
        this.certificateChain = null;
        this.keyStore         = null;
        this.certPassword     = null;
    }

    protected X509Certificate[] getCertificateChain()
    {
        return certificateChain;
    }

    protected KeyStore getKeyStore() {
        return keyStore;
    }

    protected String getCertPassword() {
        return certPassword;
    }

    protected PrivateKey getPrivateKey() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException{
        Enumeration<String> alias = keyStore.aliases();
        return (PrivateKey)keyStore.getKey(alias.nextElement(), certPassword.toCharArray());
    }

    /**
     * Create an SSL context from the given certificate information
     */
    protected SSLContext createSSLContext() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
        final String sslContextType = "TLS";

        String kmfa = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfa);
        kmf.init(keyStore, certPassword.toCharArray());
        KeyManager[] km = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance(sslContextType);
        sslContext.init(km, trustAllCerts, new SecureRandom());
        return sslContext;
    }

    protected boolean isURL() {
        return isURL;
    }

    protected void setURL(boolean isURL) {
        this.isURL = isURL;
    }

    protected String getUrlString() {
        return urlString;
    }

    protected void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    /**
     * Static handler that trusts all certificates
     */
    private static TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };
}
