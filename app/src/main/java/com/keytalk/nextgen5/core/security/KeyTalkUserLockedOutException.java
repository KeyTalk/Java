package com.keytalk.nextgen5.core.security;

/*
 * Class  :  KeyTalkUserLockedOutException
 * Description : An subclass for user locked exception
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkUserLockedOutException extends Exception {
    private static final long serialVersionUID = 1L;

    protected KeyTalkUserLockedOutException() {}
    protected KeyTalkUserLockedOutException(String msg) { super(msg); }
}