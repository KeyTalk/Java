package com.keytalk.nextgen5.core.security;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.security.KeyChain;
import android.util.Log;

import com.keytalk.nextgen5.util.PreferenceManager;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by SrashtiG on 6/5/2019.
 */

public class GetCAFromHttps extends AsyncTask<String, Void, String> {
    private  GetCAFromHttps getCAFromHttps;
    static Context context;
    IniResponseData mIniResponseData;
    String hotUrl;
    @Override
    protected String doInBackground(String... params) {

        try {
            getCertificate("Primary", "https://"+hotUrl+":8443/ca/1.0.0/primary");
        } catch (InterruptedException e) {
            Thread.interrupted();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        new GetSigningCA().execute("");
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    public  void  getCertificate(String name, String url) throws Exception {
        String formatResponse[] = processRCCDFileImportCARequestWithHTTPS(url, context, name);
        RCCDFileUtil.e("KeyTalk", "Phase3 getCertificate response header : " + formatResponse);
        try {
            RCCDFileUtil.e("KeyTalk", "Client cert response from server :" + ProtocolConstants.cert);
            trustInstalledCertificate(context, name);

        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while certificate validation :" + e.toString());
            throw new KeyTalkProtocolException("Unable to retrieve data from server");
        }
    }

    private  String[] processRCCDFileImportCARequestWithHTTPS(String baseUrl, Context context, String name) throws ServiceException {
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


            URL url = new URL(baseUrl);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
            httpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setConnectTimeout(25000);
            httpsURLConnection.setReadTimeout(25000);
            httpsURLConnection.connect();
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == 200) {
                serverMessage = new String[2];
                serverMessage[0] = PreferenceManager.getString(context, AppConstants.COOKIE);
                String path = "/sdcard/keytalk";
                BufferedInputStream inputStream = new BufferedInputStream(
                        httpsURLConnection.getInputStream());
                File directory = new File(path);

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(path + "/" + name + ".pem"));
                byte data[] = new byte[1024];
                long total = 0;
                int count = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                RCCDFileUtil.e("Certificate:- ", RCCDFileUtil.getTime() + serverMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
                httpsURLConnection = null;
            }
        }
        return serverMessage;
    }

    private  void trustInstalledCertificate(Context context, String name) {

        try {

            File f_path = new File("/sdcard/keytalk/" + name + ".pem");
            InputStream fis = null;
            fis = new BufferedInputStream(new FileInputStream(f_path));
            installCertificate(fis, context, name);

        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private  boolean installCertificate(InputStream inputStream, Context context, String name) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
            String alias = "alias";

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, certificate);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(trustStore, null);
            KeyManager[] keyManagers = kmf.getKeyManagers();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, trustManagers, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            Intent installIntent = KeyChain.createInstallIntent();
            installIntent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificate.getEncoded());
            installIntent.putExtra(KeyChain.EXTRA_NAME, name);
            Handler mainHandler = new Handler(context.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                } // This is your code
            };
            mainHandler.post(myRunnable);
            context.startActivity(installIntent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    public GetCAFromHttps execute(Context mContext, ProgressDialog progressBar, IniResponseData providerData) {
        GetCAFromHttps object=intializeClass();

        object.context = mContext;
        object.mIniResponseData=providerData;
        object.hotUrl=providerData.getStringValue("Server");
        return object;
    }

   public GetCAFromHttps intializeClass()
   {
       getCAFromHttps=new GetCAFromHttps();
       return getCAFromHttps;

   }
    class GetSigningCA extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                getCertificate("Signing", "https://"+hotUrl+":8443/ca/1.0.0/signing");
            } catch (InterruptedException e) {
                Thread.interrupted();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {

            }catch (Exception e)
            {
              Log.e("",e.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}