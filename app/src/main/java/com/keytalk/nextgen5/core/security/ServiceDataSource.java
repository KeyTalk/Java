package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.util.Log;

import com.keytalk.nextgen5.core.Processor;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/*
 * Class  :  ServiceDataSource
 * Description : Http request class for rccd file
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ServiceDataSource  implements Processor {

    private static final int TIMEOUT_CONNECTION = 15000;
    private static final int TIMEOUT_SOCKET = 15000;
    private static final String GET_REQUEST = "GET";


    protected InputStream processRCCDFileImportRequest(String baseUrl, Context context) throws ServiceException {
        if(baseUrl.startsWith("https://")) {
            return processRCCDFileImportRequestWithHTTPS(baseUrl, context);
        } else {
            return processRCCDFileImportRequestWithHTTP(baseUrl, context);
        }
    }


    private InputStream processRCCDFileImportRequestWithHTTPS(String baseUrl, Context context) throws ServiceException {
        HttpsURLConnection httpsURLConnection = null;
        InputStream responseStream = null;
        try {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            URL url = new URL(baseUrl);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod(GET_REQUEST);
            httpsURLConnection.setConnectTimeout(TIMEOUT_CONNECTION);
            httpsURLConnection.setReadTimeout(TIMEOUT_SOCKET);
            httpsURLConnection.setHostnameVerifier(hostnameVerifier);
            int responseCode = httpsURLConnection.getResponseCode();
            RCCDFileUtil.e("ServiceDataSource","RCCD httpsrequest response code : "+responseCode);
            if (responseCode == 200) {
                responseStream = httpsURLConnection.getInputStream();
            } else {
                throw new ServiceException(503);
            }
        } catch (UnknownHostException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----UnknownHostException : "+e);
            throw new ServiceException(e); //503 Service Unavailable
        } catch (ConnectException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----ConnectException :"+e);
            throw new ServiceException(e);
        } catch (SocketException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE--SocketException :"+e);
            throw new ServiceException(e);
        } catch (SocketTimeoutException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----SocketTimeoutException :"+ e);
            throw new ServiceException(e);
        } catch (Exception e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE :"+e);
            throw new ServiceException(e);
        }
        return responseStream;
    }

    private InputStream processRCCDFileImportRequestWithHTTP(String baseUrl, Context context) throws ServiceException {
        HttpURLConnection httpURLConnection = null;
        InputStream responseStream = null;
        try {
            URL url = new URL(baseUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(GET_REQUEST);
            httpURLConnection.setConnectTimeout(TIMEOUT_CONNECTION);
            httpURLConnection.setReadTimeout(TIMEOUT_SOCKET);
            //httpURLConnection.setHostnameVerifier(hostnameVerifier);
            int responseCode = httpURLConnection.getResponseCode();
            RCCDFileUtil.e("ServiceDataSource","RCCD http request response code : "+responseCode);
            if (responseCode == 200) {
                responseStream = httpURLConnection.getInputStream();
            } else {
                throw new ServiceException(503);
            }
        } catch (UnknownHostException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----UnknownHostException : "+e);
            throw new ServiceException(e); //503 Service Unavailable
        } catch (ConnectException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----ConnectException :"+e);
            throw new ServiceException(e);
        } catch (SocketException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE--SocketException :"+e);
            throw new ServiceException(e);
        } catch (SocketTimeoutException e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE----SocketTimeoutException :"+ e);
            throw new ServiceException(e);
        } catch (Exception e) {
            RCCDFileUtil.e("SERVICE DATA SOURCE :"+e);
            throw new ServiceException(e);
        }
        return responseStream;
    }

















    private boolean copyInputStreamToFile(InputStream inputStream, File file) {
        boolean isFileSaved = false;
        try {
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                outputStream.write(buf,0,len);
            }
            if(outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if(inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            isFileSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "copyInputStreamToFile(): Exception :");
        } finally {
            return isFileSaved;
        }
    }

}
