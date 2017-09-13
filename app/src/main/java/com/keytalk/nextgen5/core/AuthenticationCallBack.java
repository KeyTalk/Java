package com.keytalk.nextgen5.core;

/*
 * Class  :  AuthenticationCallBack
 * Description : Interface for update the status of certificate request to UI.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface AuthenticationCallBack {

    void credentialRequest(String serviceUsers, boolean isUserNameRequested, boolean isPasswordRequested,String passwordText, boolean isPinRequested, boolean isResponseRequested,String challenge);
    void displayError(String errorMessage);
    public void validCertificateAvailable();

}
