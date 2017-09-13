package com.keytalk.nextgen5.core;

import com.keytalk.nextgen5.core.security.CertificateInfo;
import com.keytalk.nextgen5.core.security.KeyTalkCredentials;

/*
 * Class  :  KeyTalkCertificateConsumer
 * Description : Interface for update the status of  new certificate request internally.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface KeyTalkCertificateConsumer {
    /**
     * Called when the retrieval operation succeeds
     */
    public void certificateRetrieved(CertificateInfo cert, String selectedUserName, String[] serverMsg);

    /**
     * Called when retrieval fails
     */
    public void errorOccurred(Throwable t);

    /**
     * Called when additional credentials should be supplied
     * The given object contains the set of credentials that should be supplied.
     * When the client has filled the credentials object, it should supply it to
     * the given consumer.
     */
    public void requestCredentials(KeyTalkCredentials creds,KeyTalkCredentialsConsumer consumer);

    /**
     * Called when the credentials are wrong and the user should wait a couple
     * of seconds before trying again.
     * After waiting, the client should invoke the tryAgain() runnable to get a
     * new challenge.
     */
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain);


    /**
     * Called when reset credentials should be supplied
     * The given object contains the set of credentials that should be supplied.
     * When the client has filled the credentials object, it should supply it to
     * the given consumer.
     */

    public void resetCredentialOption(KeyTalkCredentials creds, KeyTalkExpiredCredentialConsumer expiredConsumer);


    public void requestResetCredentials(KeyTalkCredentials creds,KeyTalkExpiredCredentialConsumer expiredConsumer);

    public void requestChallengeCredentials(KeyTalkCredentials creds,KeyTalkExpiredCredentialConsumer expiredConsumer);


    public void requestResetCredentialsDelay(int seconds);



}

