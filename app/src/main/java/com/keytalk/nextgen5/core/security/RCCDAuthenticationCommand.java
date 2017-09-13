package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;
import com.keytalk.nextgen5.core.KeyTalkCredentialsConsumer;
import com.keytalk.nextgen5.core.KeyTalkExpiredCredentialConsumer;
import com.keytalk.nextgen5.core.KeyTalkUiCallback;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

/*
 * Class  :  ProtocolConstants
 * Description : Constants used in app
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDAuthenticationCommand <R extends Data, S extends Data> extends BaseCommand<R, S> implements KeyTalkUiCallback {

    protected RCCDAuthenticationCommand(Class<? extends R> requestClass, Class<? extends S> responseClass) {
        super(requestClass, responseClass);
        // TODO Auto-generated constructor stub
    }

    public void execute() {
        RCCDFileUtil.e("Command execute :"+request.getAction());
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
    }

    @Override
    public void requestCredentials(KeyTalkCredentials credentialsRequest,KeyTalkCredentialsConsumer consumer) {
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
        selectedRCCDFileResponseData.setKeyTalkCredentialsConsumer(consumer);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS);
        RCCDFileUtil.e("Command requestCredentials "+request.getAction()+","+response.getMessageType());
        processResponseData(response);
    }

    @Override
    public void displayError(Throwable t) {
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        if(t != null && t.getMessage() !=null && !t.getMessage().isEmpty()) {
            if(t.getMessage().trim().equals("Connection to http://keytalk.keytalk.com:80 refused") ||
                    t.getMessage().trim().startsWith("Unable to resolve host"))
                selectedRCCDFileResponseData.setErrorMessage("You are not connected to the internet. Please check your settings or try again later.");
            else
                selectedRCCDFileResponseData.setErrorMessage(t.getMessage());
        } else {
            selectedRCCDFileResponseData.setErrorMessage("Unknown error. Please try after some time.");
        }

        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_ERROR);
        RCCDFileUtil.e("Command displayError "+request.getAction()+","+response.getMessageType()+","+t.getMessage());
        processResponseData(response);
    }


    @Override
    public void reloadPage(String url, SSLContext sslContext, PrivateKey key, X509Certificate[] keyChain, String serviceName, String[] serverMsg) {
        // TODO Auto-generated method stub
        SelectedRCCDFileCertificateResponseData selectedRCCDFileCertificateResponseData = new SelectedRCCDFileCertificateResponseData();
        selectedRCCDFileCertificateResponseData.setKey(key);
        selectedRCCDFileCertificateResponseData.setKeyChain(keyChain);
        selectedRCCDFileCertificateResponseData.setSslContext(sslContext);
        selectedRCCDFileCertificateResponseData.setUrl(url);
        selectedRCCDFileCertificateResponseData.setServiceName(serviceName);
        selectedRCCDFileCertificateResponseData.setServerMsg(serverMsg);
        response.setMessageType(ResponseType.AUTH_RESPONCE_RELOAD_PAGE);
        RCCDFileUtil.e("Command reloadPage "+request.getAction()+","+response.getMessageType());
        response.setData((S) selectedRCCDFileCertificateResponseData);
        processResponseData(response);

    }



    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        // TODO Auto-generated method stub
        //AUTH_REQUEST_DELAY
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        selectedRCCDFileResponseData.setSeconds(seconds);
        selectedRCCDFileResponseData.setTryAgain(tryAgain);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_REQUEST_DELAY);
        RCCDFileUtil.e("Command credential delay "+request.getAction()+","+response.getMessageType()+","+seconds);
        processResponseData(response);
    }

    @Override
    public void requestResetCredentials(KeyTalkCredentials credentialsRequest,
                                        KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
        selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS);
        RCCDFileUtil.e("Command reset credential request "+request.getAction()+","+response.getMessageType());
        processResponseData(response);

    }

    @Override
    public void requestResetCredentialsDelay(int delaySeconds) {
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) response.getData();
        selectedRCCDFileResponseData.getKeyTalkCredentials().setNewPassword("");
        selectedRCCDFileResponseData.setSeconds(delaySeconds);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_DELAY);
        RCCDFileUtil.e("Command reset credential delay "+request.getAction()+","+response.getMessageType());
        processResponseData(response);
    }

    @Override
    public void resetCredentialOption(KeyTalkCredentials credentialsRequest,
                                      KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
        selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_OPTION);
        RCCDFileUtil.e("Command reset credential option "+request.getAction()+","+response.getMessageType());
        processResponseData(response);

    }

    @Override
    public void requestChallengeCredentials(KeyTalkCredentials credentialsRequest,
                                            KeyTalkExpiredCredentialConsumer expiredConsumer) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        SelectedRCCDFileResponseData selectedRCCDFileResponseData = new SelectedRCCDFileResponseData();
        selectedRCCDFileResponseData.setKeyTalkCredentials(credentialsRequest);
        selectedRCCDFileResponseData.setExpiredConsumer(expiredConsumer);
        response.setData((S) selectedRCCDFileResponseData);
        response.setMessageType(ResponseType.AUTH_REQUEST_CHALLENGE);
        RCCDFileUtil.e("Command reset credential option "+request.getAction()+","+response.getMessageType());
        processResponseData(response);

    }
}

