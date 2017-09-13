package com.keytalk.nextgen5.core;

import com.keytalk.nextgen5.core.security.KeyTalkCredentials;

/*
 * Class  :  KeyTalkCredentialsConsumer
 * Description : Interface for an object that consumes credentials
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface KeyTalkCredentialsConsumer {

    /**
     * Supply the credentials to the consumer
     */
    void supplyCredentials(KeyTalkCredentials credentials);
}
