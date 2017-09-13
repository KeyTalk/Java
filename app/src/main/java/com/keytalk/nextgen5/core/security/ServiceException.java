package com.keytalk.nextgen5.core.security;

import android.text.TextUtils;

/*
 * Class  :  ServiceException
 * Description : An subclass for service exceptions while rccd request
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    protected ServiceException() {
        super();
    }

    protected ServiceException(Exception exception) {
        super(exception);
    }

    protected ServiceException(String message) {
        super(message);
    }

    protected ServiceException(int errorCode) {
        super(String.valueOf(errorCode));
    }

    protected int getErrorCode() {
        String[] errorCodes = getMessage().split(":");
        if(errorCodes.length > 0) {
            String code = errorCodes[errorCodes.length - 1].trim();
            if (!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code)) {
                return Integer.parseInt(code);
            }
        }
        // return HttpStatus.SC_OK;
        return 200;
    }
}

