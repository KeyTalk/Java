package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Actions;
import com.keytalk.nextgen5.core.Data;

/*
 * Class  :  Request
 * Description : Support class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class Request<R extends Data> {

    private R data;
    private Actions action;

    protected R getData() {
        return data;
    }

    protected void setData(R data) {
        this.data = data;
    }

    protected Actions getAction() {
        return action;
    }

    protected void setAction(Actions action) {
        this.action = action;
    }
}
