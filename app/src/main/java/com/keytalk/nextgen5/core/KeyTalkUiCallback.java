package com.keytalk.nextgen5.core;

import com.keytalk.nextgen5.core.security.KeyTalkCredentials;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

/*
 * Class  :  KeyTalkUiCallback
 * Description : Interface for update the status of new certificate request
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface KeyTalkUiCallback {

    public void reloadPage(String url, SSLContext sslContext, PrivateKey key,
                           X509Certificate[] keyChain, String serviceName, String[] serverMsg);

    /**
     * This will be called when credentials need to be supplied.
     */
    public void requestCredentials(KeyTalkCredentials credentialsRequest,
                                   KeyTalkCredentialsConsumer consumer);

    /**
     * Display an error
     */
    public void displayError(Throwable t);

    /**
     * Called when the supplied credentials are invalid and the user should wait
     * a couple of seconds before trying again.
     *
     * After waiting, invoke tryAgain to get a new challenge.
     */
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain);

    /**
     * This will be called when credentials need to be reset.
     */
    public void requestResetCredentials(KeyTalkCredentials credentialsRequest,
                                        KeyTalkExpiredCredentialConsumer expiredConsumer);

    public void requestResetCredentialsDelay(int delaySeconds);

    public void resetCredentialOption(KeyTalkCredentials credentialsRequest,
                                      KeyTalkExpiredCredentialConsumer expiredConsumer);

    public void requestChallengeCredentials(KeyTalkCredentials credentialsRequest,
                                            KeyTalkExpiredCredentialConsumer expiredConsumer);

}

