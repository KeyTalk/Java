package com.keytalk.nextgen5.core.security;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import static com.keytalk.nextgen5.core.security.ProtocolConstants.SLASH;

/*
 * Class  :  PayloadBuilder
 * Description : PayloadBuilder is the class that builds up the entire payload that is sent through HttpProtocol.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class PayloadBuilder {

    protected SelectedRCCDFileRequestData selectedRCCDFileRequestData;

    /**
     * Calling appropriate methods will return the necessary PayloadEnvelope. If encodings and encryptions
     * are necessary, they will also be done.
     */
    protected PayloadBuilder(SelectedRCCDFileRequestData requestData) {
        this.selectedRCCDFileRequestData = requestData;
    }

    protected String getPhase1HandshakeHelloPayload() throws Exception {
        return ProtocolConstants.hello_with_version + URLEncoder.encode(ProtocolConstants.platform_name, "UTF-8");
    }

    protected String getPhase1HandshakePayload() throws Exception {
        return SLASH + ProtocolConstants.handshake + ProtocolConstants.caller_utc + getUTCTime();
    }

    protected String getPhase2AuthRequirementsPayload(String serviceName) throws Exception {
        return SLASH + ProtocolConstants.auth_requirements + ProtocolConstants.service + serviceName;
    }

    protected String getPhase2SupplyAuthenticationPayload(KeyTalkCredentials creds, String serviceName) throws Exception {
        String requstURL = SLASH + ProtocolConstants.authentication + ProtocolConstants.service + serviceName +
                ProtocolConstants.caller_hwdescription + URLEncoder.encode(ProtocolConstants.platform_name, "UTF-8");
        Log.e("TAG", "requstURL : requstURL1 :" + requstURL);
        if (creds.isUsernameRequested())
            requstURL = requstURL + ProtocolConstants.userid + /*URLEncoder.encode(*/creds.getUsername()/*, "UTF-8")*/;//creds.getUsername();

        if (creds.isHardwareSignatureRequested())
            requstURL = requstURL + ProtocolConstants.hardware + URLEncoder.encode(creds.getHardwareSignature(), "UTF-8");
        if (creds.isPasswordRequested())
            requstURL = requstURL + ProtocolConstants.passwords + /*URLEncoder.encode(*/creds.getPassword()/*, "UTF-8")*/;//creds.getPassword();
        if (creds.isPinRequested())
            requstURL = requstURL + ProtocolConstants.pins + URLEncoder.encode(creds.getPin(), "UTF-8");//creds.getPin();


        Log.e("TAG", "URL : 1 :" + requstURL);
        return requstURL;

    }

    protected String getPhase2ResetExpiredPasswordPayload(KeyTalkCredentials creds) throws Exception {
        return SLASH + ProtocolConstants.change_password + ProtocolConstants.old_password + creds.getPassword() + ProtocolConstants.new_password + creds.getNewPassword();
    }

    protected String getPhase3LastMessageFromServerPayload(final String timeStamp) throws Exception {
        String messagePayload = ProtocolConstants.SLASH + ProtocolConstants.last_messages;
        if (timeStamp != null)
            messagePayload = messagePayload + ProtocolConstants.from_utc + timeStamp;
        return messagePayload;
    }

    protected String getCertificateFormatPayload(String format) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        return SLASH + ProtocolConstants.cert + ProtocolConstants.cert_format + format;
    }

    private String getUTCTime() throws UnsupportedEncodingException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }
}
