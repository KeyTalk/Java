package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

import android.os.Message;

/*
 * Class  :  DataUtil
 * Description : Support class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

@SuppressWarnings("rawtypes")
public final class DataUtil {

    private DataUtil() {};

    protected static Response extractResponse(Message msg) {
        if (msg.obj instanceof Response) {
            return (Response)msg.obj;
        } else {
            return null;
        }
    }

    protected static Data extractDataFromResponse(Response response) {
        if (response.getData() instanceof Data) {
            return response.getData();
        } else {
            return null;
        }
    }

    protected static Data extractDataFromRequest(Request request) {
        if (request.getData() instanceof Data) {
            return request.getData();
        } else {
            return null;
        }
    }
}


