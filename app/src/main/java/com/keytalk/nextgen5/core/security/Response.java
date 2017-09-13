package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Actions;
import com.keytalk.nextgen5.core.Data;

/*
 * Class  :  Response
 * Description : Support class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class Response<S extends Data> {

    private S data;
    private String message;
    private Actions action;
    private ResponseType messageType;

    protected Response() {}

    protected Actions getAction() {
        return action;
    }

    protected void setAction(Actions action) {
        this.action = action;
    }

    protected S getData() {
        return data;
    }

    protected void setData(S data) {
        this.data = data;
    }

    protected String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the messageType
     */
    protected ResponseType getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    protected void setMessageType(ResponseType messageType) {
        this.messageType = messageType;
    }
}