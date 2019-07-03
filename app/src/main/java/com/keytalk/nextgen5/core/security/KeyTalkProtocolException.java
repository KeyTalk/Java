package com.keytalk.nextgen5.core.security;

/*
 * Class  :  KeyTalkProtocolException
 * Description : An subclass for handle protocol exceptions
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkProtocolException extends Exception {
    private static final long serialVersionUID = 1L;

    protected KeyTalkProtocolException() {}
    public KeyTalkProtocolException(String msg) { super(msg); }
}