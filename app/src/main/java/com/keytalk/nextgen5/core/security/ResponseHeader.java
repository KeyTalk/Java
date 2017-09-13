package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

import java.io.Serializable;

/*
 * Class  :  ResponseHeader
 * Description : Support class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ResponseHeader implements Data, Serializable {

    protected static final String MSG_OK = "OK";
    protected static final String MSG_INVALID_TOKEN = "INVALID_TOKEN";
    protected static final String MSG_MISSING_TOKEN = "MISSING_TOKEN";
    protected static final String MSG_INVALID_REQUEST_FORMAT = "INVALID_REQUEST_FORMAT";
    protected static final String MSG_UNKNOWN_REQUEST_PROPERTY_TYPE = "UNKNOWN_REQUEST_PROPERTY_TYPE";
    protected static final String MSG_UNKNOWN_REQUEST_SORT_FIELD = "UNKNOWN_REQUEST_SORT_FIELD";
    protected static final String MSG_INVALID_REQUEST_SEARCH_KEY_MUST_HAVE_ONE_CHILD = "INVALID_REQUEST_SEARCH_KEY_MUST_HAVE_ONE_CHILD";
    protected static final String MSG_INVALID_REQUEST_BOUNDING_BOX_TOO_BIG = "INVALID_REQUEST_BOUNDING_BOX_TOO_BIG";
    protected static final String MSG_INVALID_REQUEST_RADIUS_TOO_BIG = "INVALID_REQUEST_RADIUS_TOO_BIG";
    protected static final String MSG_SERVICE_FAILURE = "SERVICE_FAILURE";
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String fileOperationStatus;

    protected ResponseHeader() {

        success = false;

    }

    protected boolean isSuccess() {
        return success;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    protected String getFileOperationStatus() {
        return fileOperationStatus;
    }

    protected void setFileOperationStatus(String fileOperationStatus) {
        this.fileOperationStatus = fileOperationStatus;
    }


}

