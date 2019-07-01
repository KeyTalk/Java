package com.keytalk.nextgen5.core.security;

import android.content.Context;

import com.keytalk.nextgen5.util.PreferenceManager;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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


            // now I get the X509 certificate from the PEM string
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(caInputStream);
            String alias = "alias";//cert.getSubjectX500Principal().getName();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, certificate);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(trustStore, null);
            KeyManager[] keyManagers = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
                  /*  URL url = new URL(urlString);
                    conn = (HttpsURLConnection) url.openConnection();*/
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());




            /*Certificate ca = null;
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

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, null);
            KeyManager[] keyManagers = kmf.getKeyManagers();

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);*/





            //sslContext = SSLContext.getInstance("TLS");
            //sslContext.init(null, tmf.getTrustManagers(), null);

            //sslContext  = SSLContext.getInstance("TLSv1.2");
            // sslContext.init(keyManagers, tmf.getTrustManagers(), null);

           /* HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            Intent installIntent = KeyChain.createInstallIntent();
            installIntent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificate.getEncoded());
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);*/


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected String phase1HandshakeHello(final String jsonObject, Context mContext) throws  IOException {
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
                PreferenceManager.put(mContext, AppConstants.COOKIE,receivedCookie);
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
               RCCDFileUtil.e("Credential request successfully completed");
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

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            SSLContext sc = null;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            //HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(mUrl + jsonObject);
            httpsURLConnection = (HttpsURLConnection)  url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
            httpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
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
                RCCDFileUtil.e("Certificate:- ", RCCDFileUtil.getTime()+serverMessage[1] );
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
