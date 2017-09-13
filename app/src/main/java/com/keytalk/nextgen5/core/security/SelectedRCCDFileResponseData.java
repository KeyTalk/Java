package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;
import com.keytalk.nextgen5.core.KeyTalkCredentialsConsumer;
import com.keytalk.nextgen5.core.KeyTalkExpiredCredentialConsumer;
import java.io.Serializable;

/*
 * Class  :  SelectedRCCDFileResponseData
 * Description : Class which holding selected rccd file response details
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class SelectedRCCDFileResponseData implements Data, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private KeyTalkCredentials KeyTalkCredentials;
    private KeyTalkCredentialsConsumer keyTalkCredentialsConsumer;
    private KeyTalkExpiredCredentialConsumer expiredConsumer;
    private String errorMessage = null;

    private int seconds = 0;
    private Runnable tryAgain;
    /**
     * @return the keyTalkCredentialsConsumer
     */
    protected KeyTalkCredentialsConsumer getKeyTalkCredentialsConsumer() {
        return keyTalkCredentialsConsumer;
    }
    /**
     * @param keyTalkCredentialsConsumer the keyTalkCredentialsConsumer to set
     */
    protected void setKeyTalkCredentialsConsumer(KeyTalkCredentialsConsumer keyTalkCredentialsConsumer) {
        this.keyTalkCredentialsConsumer = keyTalkCredentialsConsumer;
    }
    /**
     * @return the keyTalkCredentials
     */
    protected KeyTalkCredentials getKeyTalkCredentials() {
        return KeyTalkCredentials;
    }
    /**
     * @param keyTalkCredentials the keyTalkCredentials to set
     */
    protected void setKeyTalkCredentials(KeyTalkCredentials keyTalkCredentials) {
        KeyTalkCredentials = keyTalkCredentials;
    }
    /**
     * @return the errorMessage
     */
    protected String getErrorMessage() {
        return errorMessage;
    }
    /**
     * @param errorMessage the errorMessage to set
     */
    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    protected int getSeconds() {
        return seconds;
    }
    protected void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    protected Runnable getTryAgain() {
        return tryAgain;
    }
    protected void setTryAgain(Runnable tryAgain) {
        this.tryAgain = tryAgain;
    }
    protected KeyTalkExpiredCredentialConsumer getExpiredConsumer() {
        return expiredConsumer;
    }
    protected void setExpiredConsumer(KeyTalkExpiredCredentialConsumer expiredConsumer) {
        this.expiredConsumer = expiredConsumer;
    }


}
