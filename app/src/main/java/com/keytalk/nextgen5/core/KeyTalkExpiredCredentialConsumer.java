package com.keytalk.nextgen5.core;

import com.keytalk.nextgen5.core.security.KeyTalkCredentials;

/*
 * Class  :  KeyTalkExpiredCredentialConsumer
 * Description : Interface for status request which pass internally
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface KeyTalkExpiredCredentialConsumer {

    /**
     * Supply the credentials to the consumer
     */
    void supplyNewCredentials(KeyTalkCredentials credentials);

    void supplyChallengeCredentials(KeyTalkCredentials credentials);

    void isPasswordShouldReset(boolean isResetPassword);

}
