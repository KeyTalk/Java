package com.keytalk.nextgen5.core.security;

/*
 * Class  :  Tuple
 * Description : Support class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class Tuple<T, U> {
    private final T mFirst;
    private final U mSecond;

    protected Tuple(T first, U second){
        mFirst = first;
        mSecond = second;
    }

    protected T getFirst(){
        return mFirst;
    }

    protected U getSecond(){
        return mSecond;
    }
}