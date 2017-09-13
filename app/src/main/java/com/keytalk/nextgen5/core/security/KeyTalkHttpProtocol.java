package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/*
 * Class  :  KeyTalkHttpProtocol
 * Description : Http/Https request class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkHttpProtocol {
    private final String mUrl;
    private  String receivedCookie = null;

    /**
     * Constructor to setup the HttpProtocol, used for sending a http message
     * @param url
     *            Http address
     */
    protected KeyTalkHttpProtocol(String url) {
        mUrl = url;
    }




    private String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }


    private SSLContext sslContext = null;
    protected boolean createSSLContext(Context context, String selectedRCCDFilePath) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String comcacertPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER
                    + SecurityConstants.RCCD_FOLDER_SEPERATOR + selectedRCCDFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR
                    + SecurityConstants.RCCD_CONTENT_FOLDER_PATH + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_COMCACERT;
            File rccdCommonFile = new File(comcacertPath);
            if (!rccdCommonFile.exists()) {
                return false;
            }
            InputStream caInputStream = new BufferedInputStream(new FileInputStream(comcacertPath));
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(caInputStream != null)
                    caInputStream.close();
                caInputStream = null;
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            //sslContext = SSLContext.getInstance("TLS");
            //sslContext.init(null, tmf.getTrustManagers(), null);

            sslContext  = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected String phase1HandshakeHello(final String jsonObject) throws  IOException {
        HttpsURLConnection httpsURLConnection = null;
        String serverMessage = null;
        try {
            URL url = new URL(mUrl + jsonObject);
            httpsURLConnection = (HttpsURLConnection)  url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setConnectTimeout(25000);
            httpsURLConnection.setReadTimeout(25000);
            //httpsURLConnection.connect();
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == 200) {
                Map<String, List<String>> map = httpsURLConnection.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    if (entry.getKey() == null)
                        continue;
                    if(entry.getKey().equals("Set-Cookie")) {
                        List<String> headerValues = entry.getValue();
                        Iterator<String> it = headerValues.iterator();
                        if (it.hasNext()) {
                            receivedCookie = it.next();
                        }
                        break;
                    }
                }
                serverMessage = inputStreamToString(httpsURLConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(httpsURLConnection != null) {
                httpsURLConnection.disconnect();
                httpsURLConnection = null;
            }
        }
        return serverMessage;
    }

    protected String phase1Handshake(final String jsonObject) throws  IOException {
        HttpsURLConnection httpsURLConnection = null;
        String serverMessage = null;
        try {
            URL url = new URL(mUrl + jsonObject);
            httpsURLConnection = (HttpsURLConnection)  url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setConnectTimeout(25000);
            httpsURLConnection.setReadTimeout(25000);
            httpsURLConnection.setRequestProperty("Cookie", receivedCookie);
            httpsURLConnection.connect();
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == 200) {
                serverMessage = inputStreamToString(httpsURLConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(httpsURLConnection != null) {
                httpsURLConnection.disconnect();
                httpsURLConnection = null;
            }
        }
        return serverMessage;
    }

    protected String[] phase3Certificate(final String jsonObject) throws  IOException {
        HttpsURLConnection httpsURLConnection = null;
        String serverMessage[] = null;
        try {
            URL url = new URL(mUrl + jsonObject);
            httpsURLConnection = (HttpsURLConnection)  url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setConnectTimeout(25000);
            httpsURLConnection.setReadTimeout(25000);
            httpsURLConnection.setRequestProperty("Cookie", receivedCookie);
            httpsURLConnection.connect();
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == 200) {
                serverMessage = new String[2];
                serverMessage[0] = receivedCookie;
                serverMessage[1] = inputStreamToString(httpsURLConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(httpsURLConnection != null) {
                httpsURLConnection.disconnect();
                httpsURLConnection = null;
            }
        }
        return serverMessage;
    }
}
