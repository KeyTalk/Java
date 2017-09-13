package com.keytalk.nextgen5.core.security;

/*
 * Class  :  KeyTalkNoCertificateException
 * Description : An subclass for handle the exception during communication
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkNoCertificateException extends Exception {

    private static final long serialVersionUID = 1L;

    protected KeyTalkNoCertificateException() {}
    protected KeyTalkNoCertificateException(String msg) { super(msg); }

}
