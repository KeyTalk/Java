package com.keytalk.nextgen5.core;

import com.keytalk.nextgen5.core.security.Response;

/*
 * Class  :  LocalHandler
 * Description : Interface for update handler in thread queue
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public interface LocalHandler {
    void handleLocalMessage(Response<?> message);
}
