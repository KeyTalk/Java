package com.keytalk.nextgen5.core;

import java.util.ArrayList;

/*
 * Class  :  AuthenticationCertificateCallBack
 * Description : Interface for update the status of certificate request internally.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface AuthenticationCertificateCallBack {
    void reloadPage();
    void displayError(String errorMessage);
    void invalidCredentialsDelay(int seconds, Runnable tryAgain);
    void credentialRequest(String serviceUsers, boolean isUserNameRequested, boolean isPasswordRequested,String passwordText,boolean isPinRequested, boolean isResponseRequested, String challenge);
    void resetCredentials(final String userName, final String expiredPassword);
    void resetCredentialsOption(int days);
    void requestChallange(boolean isTokenRequest,String[] challangeData,boolean isNewChallengeRequest, ArrayList<String[]> newChallengeData);

}
