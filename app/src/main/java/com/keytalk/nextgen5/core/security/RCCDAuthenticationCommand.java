package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.keytalk.nextgen5.core.Data;
import com.keytalk.nextgen5.core.KeyTalkCredentialsConsumer;
import com.keytalk.nextgen5.core.KeyTalkExpiredCredentialConsumer;
import com.keytalk.nextgen5.core.KeyTalkUiCallback;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.keytalk.nextgen5.view.util.AppConstants;

import javax.net.ssl.SSLContext;

/*
 * Class  :  ProtocolConstants
 * Description : Constants used in app
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDAuthenticationCommand <R extends Data, S extends Data> extends BaseCommand<R, S> implements KeyTalkUiCallback {

    public static Request lrequest;
    public static Context mContext;
    protected RCCDAuthenticationCommand(Class<? extends R> requestClass, Class<? extends S> responseClass) {
        super(requestClass, responseClass);
        // TODO Auto-generated constructor stub
    }

    public void execute() {
        try{
        RCCDFileUtil.e("Command execute :"+request.getAction());
        lrequest=request;
        mContext=contex;
        if(request.getAction() == ServiceActions.MLS_RCCD_CONTENT_AUTH_REQUEST_TO_SERVER) {
            SelectedRCCDFileRequestData requestData = (SelectedRCCDFileRequestData) request.getData();
            if(requestData == null) {
                //Error
                RCCDFileUtil.e("Command execute Request data is null");
                response.setMessageType(ResponseType.AUTH_REQUEST_DATA_NOT_AVAILABLE);
                response.setAction(request.getAction());
                response.setMessage(null);
                processResponseData(response);
            } else {
                RCCDFileUtil.e("Command execute public key data is null  : "+requestData.getRccdFolderPath());
                KeyTalkCore keyTalkCore = new KeyTalkCore(contex,requestData);
                keyTalkCore.startURLAuthentication(this);
            }
        }
        }catch (Exception e) {
                RCCDFileUtil.e("KeyTalkTest", e.getMessage());
            }
    }

    public static void storeRequest()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
       // SharedPreferences pref =contex.getSharedPreferences("", Context.MODE_PRIVATE);
        String someStringSet=null;
        SelectedRCCDFileRequestData requestData = (SelectedRCCDFileRequestData) lrequest.getData();
        if(pref!=null)
        {
         someStringSet = pref.getString(AppConstants.REQUEST_KEY_LIST,"");
        }
        if(someStringSet!=null||someStringSet!="")
        {
            someStringSet=someStringSet+","+requestData.getServicesName();
        }
        else {
            someStringSet=requestData.getServicesName();
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstants.REQUEST_KEY_LIST, someStringSet);
        Gson gson = new Gson();
        String json = gson.toJson(lrequest);
        editor.putString(requestData.getServicesName(),json);
        editor.apply();
    }

    @Override
    public void requestCredentials(KeyTalkCredentials credentialsRequest,KeyTalkCredentialsConsumer consumer) {
        // TODO Auto-generated method stub
        try {
            SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
            selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
            selectedRCCDFileResponseData.setKeyTalkCredentialsConsumer(consumer);
            response.setData((S) selectedRCCDFileResponseData);
            response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS);
            RCCDFileUtil.e("Command requestCredentials " + request.getAction() + "," + response.getMessageType());
            processResponseData(response);
        }catch (Exception e) {
            RCCDFileUtil.e("KeyTalkTest", e.getMessage());
        }
    }

    @Override
    public void displayError(Throwable t,Context context) {
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        if(t != null && t.getMessage() !=null && !t.getMessage().isEmpty()) {
            if(t.getMessage().trim().equals("Connection to http://keytalk.keytalk.com:80 refused") ||
                    t.getMessage().trim().startsWith("Unable to resolve host"))
                selectedRCCDFileResponseData.setErrorMessage(context.getResources().getString(com.keytalk.nextgen5.R.string.no_network_message));
            else
                selectedRCCDFileResponseData.setErrorMessage(t.getMessage());
        } else {
            String unknown_error_try_again = context.getResources().getString(com.keytalk.nextgen5.R.string.unknown_error_try_again);
            selectedRCCDFileResponseData.setErrorMessage(unknown_error_try_again);
        }

        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_ERROR);
        RCCDFileUtil.e("Command displayError "+request.getAction()+","+response.getMessageType()+","+t.getMessage());
        processResponseData(response);
    }


    @Override
    public void reloadPage(String url, SSLContext sslContext, PrivateKey key, X509Certificate[] keyChain, String serviceName, String[] serverMsg) {
        // TODO Auto-generated method stub
      try {
          SelectedRCCDFileCertificateResponseData selectedRCCDFileCertificateResponseData = new SelectedRCCDFileCertificateResponseData();
          selectedRCCDFileCertificateResponseData.setKey(key);
          selectedRCCDFileCertificateResponseData.setKeyChain(keyChain);
          selectedRCCDFileCertificateResponseData.setSslContext(sslContext);
          selectedRCCDFileCertificateResponseData.setUrl(url);
          selectedRCCDFileCertificateResponseData.setServiceName(serviceName);
          selectedRCCDFileCertificateResponseData.setServerMsg(serverMsg);
          response.setMessageType(ResponseType.AUTH_RESPONCE_RELOAD_PAGE);
          RCCDFileUtil.e("Command reloadPage " + request.getAction() + "," + response.getMessageType());
          response.setData((S) selectedRCCDFileCertificateResponseData);
          processResponseData(response);
      }catch (Exception e) {
          RCCDFileUtil.e("KeyTalkTest", e.getMessage());
      }
    }



    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        // TODO Auto-generated method stub
        //AUTH_REQUEST_DELAY
        try {
            SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
            selectedRCCDFileResponseData.setSeconds(seconds);
            selectedRCCDFileResponseData.setTryAgain(tryAgain);
            response.setData((S) selectedRCCDFileResponseData);
            response.setMessageType(ResponseType.AUTH_REQUEST_DELAY);
            RCCDFileUtil.e("Command credential delay " + request.getAction() + "," + response.getMessageType() + "," + seconds);
            processResponseData(response);
        }catch (Exception e) {
            RCCDFileUtil.e("KeyTalkTest", e.getMessage());
        }
    }

    @Override
    public void requestResetCredentials(KeyTalkCredentials credentialsRequest,
                                        KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        try {
            SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
            selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
            selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
            response.setData((S) selectedRCCDFileResponseData);
            response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS);
            RCCDFileUtil.e("Command reset credential request " + request.getAction() + "," + response.getMessageType());
            processResponseData(response);
        }catch (Exception e) {
            RCCDFileUtil.e("KeyTalkTest", e.getMessage());
        }
    }

    @Override
    public void requestResetCredentialsDelay(int delaySeconds) {
        // TODO Auto-generated method stub
      try {
          SelectedRCCDFileResponseData selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) response.getData();
          selectedRCCDFileResponseData.getKeyTalkCredentials().setNewPassword("");
          selectedRCCDFileResponseData.setSeconds(delaySeconds);
          response.setData((S) selectedRCCDFileResponseData);
          response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_DELAY);
          RCCDFileUtil.e("Command reset credential delay " + request.getAction() + "," + response.getMessageType());
          processResponseData(response);
      }catch (Exception e) {
          RCCDFileUtil.e("KeyTalkTest", e.getMessage());
      }
    }

    @Override
    public void resetCredentialOption(KeyTalkCredentials credentialsRequest,
                                      KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
      try {
          SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
          selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
          selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
          response.setData((S) selectedRCCDFileResponseData);
          response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_OPTION);
          RCCDFileUtil.e("Command reset credential option " + request.getAction() + "," + response.getMessageType());
          processResponseData(response);
      }catch (Exception e) {
          RCCDFileUtil.e("KeyTalkTest", e.getMessage());
      }
    }

    @Override
    public void requestChallengeCredentials(KeyTalkCredentials credentialsRequest,
                                            KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
       try {
           SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
           selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
           selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
           response.setData((S) selectedRCCDFileResponseData);
           response.setMessageType(ResponseType.AUTH_REQUEST_CHALLENGE);
           RCCDFileUtil.e("Command reset credential option " + request.getAction() + "," + response.getMessageType());
           processResponseData(response);
       }catch (Exception e) {
           RCCDFileUtil.e("KeyTalkTest", e.getMessage());
       }
    }
}

