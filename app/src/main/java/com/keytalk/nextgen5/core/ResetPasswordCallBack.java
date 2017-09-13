package com.keytalk.nextgen5.core;

/*
 * Class  :  ResetPasswordCallBack
 * Description : Interface for password reset information to UI
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface ResetPasswordCallBack {
    void passwordResetError(String errorMessage);
    void passwordResetDelay(int seconds);
    void credentialRequest(String serviceUsers, boolean isUserNameRequested, boolean isPasswordRequested,String passwordText, boolean isPinRequested, boolean isResponseRequested, String challenge);

}
