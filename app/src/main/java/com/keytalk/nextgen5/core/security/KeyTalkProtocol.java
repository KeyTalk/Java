package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.view.View.X;

/*
 * Class  :  KeyTalkProtocol
 * Description : An support class for pass all the request to server and process the response
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkProtocol {
    private static final long mLoadTime = System.currentTimeMillis();


    private final PayloadBuilder mPayload;
    private final KeyTalkHttpProtocol endPoint;
    private String mServiceName;
    private Context mContext;
    private KeyStore mKeyStore;
    private X509Certificate[] mCertChain = new X509Certificate[1];

    /**
     * Create a KMProtocol instance for the given service
     */
    protected KeyTalkProtocol(KeyTalkHttpProtocol endPoint, PayloadBuilder payload,Context c) {
        this.endPoint      = endPoint;
        this.mPayload      = payload;
        this.mServiceName = mPayload.selectedRCCDFileRequestData.getServicesName();
        this.mContext = c;
    }

    protected void phase1HandshakeHello() throws Exception {
        RCCDFileUtil.e("KeyTalk","phase1HandshakeHello");
        boolean isSSLCertAvailable = endPoint.createSSLContext(mContext, mPayload.selectedRCCDFileRequestData.getRccdFolderPath());
        String helloResponse = endPoint.phase1HandshakeHello(mPayload.getPhase1HandshakeHelloPayload());
        RCCDFileUtil.e("KeyTalk","phase1HandshakeHello response : "+helloResponse);
        handlePhase1HandshakeHello(helloResponse);
    }

    protected void phase1Handshake() throws Exception {
        RCCDFileUtil.e("KeyTalk","Phase1 phase1Handshake starting ");
        String handshakeResponse = endPoint.phase1Handshake(mPayload.getPhase1HandshakePayload());
        RCCDFileUtil.e("KeyTalk","Phase1 handshake response header : "+handshakeResponse);
        handlePhase1Handshake(handshakeResponse);
    }

    protected String phase2AuthRequirements() throws Exception {
        RCCDFileUtil.e("KeyTalk","Phase2 phase2AuthRequirements starting");
        String authRequirementsResponse = endPoint.phase1Handshake(mPayload.getPhase2AuthRequirementsPayload(mServiceName));
        RCCDFileUtil.e("KeyTalk","phase2AuthRequirements response header : "+authRequirementsResponse);
        handlePhase2AuthRequirements(authRequirementsResponse);
        return authRequirementsResponse;
    }

    protected AuthResult phase2SupplyAuthentication(KeyTalkCredentials creds) throws Exception {
        RCCDFileUtil.e("KeyTalk","phase2SupplyAuthentication sending credentials to server : ");
        String authRespResponse = endPoint.phase1Handshake(mPayload.getPhase2SupplyAuthenticationPayload(creds,mServiceName));
        RCCDFileUtil.e("KeyTalk","phase2SupplyAuthentication response header : "+authRespResponse);
        return handlePhase2SupplyAuthenticationResult(authRespResponse);
    }

    protected AuthResult phase2ResetExpiredPassword(KeyTalkCredentials creds) throws Exception {
        RCCDFileUtil.e("KeyTalk","phase2ResetExpiredPassword sending credentials to server : ");
        String authRespResponse = endPoint.phase1Handshake(mPayload.getPhase2ResetExpiredPasswordPayload(creds));
        RCCDFileUtil.e("KeyTalk","phase2ResetExpiredPassword response header : "+authRespResponse);
        return handlePhase2ResetExpiredPasswordResult(authRespResponse);
    }

    protected String[] getPhase3LastMessageFromServer() throws Exception {
        String keyWord = null;
        try {
            SharedPreferences sharedPreference = mContext.getSharedPreferences(ProtocolConstants.MESSAGE_STAMP, 0);
            String timeStamp = sharedPreference.getString(ProtocolConstants.TIMESTAMP, null);
            if(timeStamp != null && !timeStamp.isEmpty()) {
                keyWord = timeStamp.trim();
                if(keyWord.endsWith("GMT+00:00+0000")) {
                    keyWord = keyWord.replace("GMT+00:00+0000", "+0000");
                }
            }
        } catch(Exception e) { }
        keyWord = null;
        RCCDFileUtil.e("KeyTalk","Phase3 last received message timestamp : "+keyWord);
        String lastMessageResponse = endPoint.phase1Handshake(mPayload.getPhase3LastMessageFromServerPayload(keyWord));
        RCCDFileUtil.e("KeyTalk","Phase3 getMessageFromServer response header : "+lastMessageResponse);
        return handleServerMessageResultNew(lastMessageResponse);
    }

    protected CertificateInfo getCertificate() throws Exception {
        String formatResponse[] = endPoint.phase3Certificate(mPayload.getCertificateFormatPayload("P12"));
        RCCDFileUtil.e("KeyTalk","Phase3 getCertificate response header : "+formatResponse);
        try {
            JSONObject serverMessageJSONObject = new JSONObject(formatResponse[1]);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.cert)) {
                RCCDFileUtil.e("KeyTalk", "Client cert response from server :"+ProtocolConstants.cert);
                String certificateString = serverMessageJSONObject.getString(ProtocolConstants.cert);
                byte[] certificateByteArray = Base64.decode(certificateString.getBytes("UTF-8"), Base64.NO_WRAP);
                Tuple<byte[], String> cert = new Tuple<byte[], String>(certificateByteArray, mServiceName);
                mKeyStore = KeyStore.getInstance("PKCS12","BC");
                String hexKey = formatResponse[0];
                RCCDFileUtil.e("KeyTalk","Phase3  response header  hexKey.substring(0,30) : "+ hexKey+","+hexKey.substring("keytalkcookie=".length(),44));
                mKeyStore.load(new ByteArrayInputStream((byte[]) cert.getFirst()), hexKey.substring("keytalkcookie=".length(),44).toCharArray());
                Enumeration<String> alias = mKeyStore.aliases();
                // TODO: get the key as well
                mCertChain[0] = (X509Certificate)mKeyStore.getCertificate(alias.nextElement());
                return new CertificateInfo(mCertChain, mKeyStore, hexKey.substring("keytalkcookie=".length(),44));
            } else if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while certicifate request :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server "+serverMessageJSONObject.getString(ProtocolConstants.status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while certificate validation :"+e.toString());
            throw new KeyTalkProtocolException("Unable to retrieve data from server");
        }
    }

    private void handlePhase1HandshakeHello (String serverMessage) throws KeyTalkProtocolException, Exception {
        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.hello)) {
                RCCDFileUtil.e("KeyTalk", "Client hello response from server :"+ProtocolConstants.hello);
            } else if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while hello :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server "+serverMessageJSONObject.getString(ProtocolConstants.status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while hello :"+e.toString());
            throw new KeyTalkProtocolException("Unable to retrieve data from server");
        }
    }

    private void handlePhase1Handshake (String serverMessage) throws Exception {
        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.handshake)) {
                RCCDFileUtil.e("KeyTalk", "Client handshake response from server :"+ProtocolConstants.handshake);
            } else if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||
                    isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while handshake :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server");
            } else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while handshake :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server "+serverMessageJSONObject.getString(ProtocolConstants.status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while handshake :"+e.toString());
            throw new KeyTalkProtocolException("Unable to retrive data from server");
        }
    }

    private void handlePhase2AuthRequirements (String serverMessage) throws KeyTalkProtocolException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.auth_requirements)) {
                RCCDFileUtil.e("KeyTalk", "Client AuthReq response from server :" + ProtocolConstants.auth_requirements);
            } else if(isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while Auth :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while Auth :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server " + serverMessageJSONObject.getString(ProtocolConstants.status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while Auth :"+e.toString());
            throw new KeyTalkProtocolException("Unable to retrive data from server");
        }
    }

    private AuthResult handlePhase2SupplyAuthenticationResult(String serverMessage) throws KeyTalkProtocolException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {//String type, byte[] data) {
        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.auth_result)) {
                RCCDFileUtil.e("KeyTalk", "Client AuthReq response from server :" + ProtocolConstants.auth_result);
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status), ProtocolConstants.OK)) {
                    int expiryDay = -1;
                    if(serverMessageJSONObject.has(ProtocolConstants.password_validity)) {
                        try {
                            String validString = serverMessageJSONObject.getString(ProtocolConstants.password_validity);
                            if(validString != null && !validString.isEmpty() && !validString.trim().equals("-1")) {
                                expiryDay = Integer.parseInt(validString)/60/60/24;
                            }
                            return AuthResult.OK(expiryDay);
                        } catch(Exception e) {
                            return AuthResult.OK(expiryDay);
                        }
                    } else {
                        return AuthResult.OK(expiryDay);
                    }
                }
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.LOCKED))
                    return AuthResult.Locked();
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.DELAY)) {
                    String delayTime = serverMessageJSONObject.getString(ProtocolConstants.delay);
                    int delay  = 10;
                    try {
                        delay = Integer.parseInt(delayTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return AuthResult.Delay(delay);
                }
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.EXPIRED))
                    return AuthResult.Expired();
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.CHALLENGE)) {
                    RCCDFileUtil.e("KeyTalkProtocol", "Auth Result got challange response  : ");
                    if(serverMessageJSONObject.has(ProtocolConstants.challenges) && serverMessageJSONObject.has(ProtocolConstants.response_names)) {
                        /*String[] challengData = new String[2];
                        JSONArray challangeJSONArray = serverMessageJSONObject.getJSONArray(ProtocolConstants.challenges);
                        challengData[0] = challangeJSONArray.toString();
                        challangeJSONArray = serverMessageJSONObject.getJSONArray(ProtocolConstants.response_names);
                        challengData[1] = challangeJSONArray.toString();
                        return AuthResult.AuthReqChallange(challengData[1]);*/
                        throw new KeyTalkProtocolException("Server requested for UMTS/AKA and GSM/SIM challenge-response authentication which is not supported in this build");
                    } else if(serverMessageJSONObject.has(ProtocolConstants.challenges)) {
                        String[] challengData = null;
                        JSONArray challangeJSONArray = serverMessageJSONObject.getJSONArray(ProtocolConstants.challenges);
                        if(challangeJSONArray != null && challangeJSONArray.length() > 0) {
                            challengData = new String[challangeJSONArray.length() * 2];
                            for(int i=0; i<challangeJSONArray.length(); i = i +2) {
                                try {
                                    JSONObject obj = challangeJSONArray.getJSONObject(i);
                                    challengData[i] = obj.getString("name");
                                    challengData[i+1] = obj.getString("value");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return AuthResult.Challenge(challengData);
                    } else {
                        return AuthResult.Challenge(null);
                    }
                    /*return AuthResult.AuthReqChallange(null);
                    String[] responseNames = result[1].split(":");
				//String responseName = null;
				if(responseNames.length == 1) {
					//responseName = responseNames[1];
					String [] challengData = responseNames[0].split("#");
					challengData[0] = new String(Base64.decode(challengData[0].getBytes(), Base64.NO_WRAP)).trim();
					challengData[1] = new String(Base64.decode(challengData[1].getBytes(), Base64.NO_WRAP)).trim();
					return AuthResult.Challenge(challengData);
				} else if(responseNames.length == 2) {
					return AuthResult.AuthReqChallange(result[1]);

				} */
                    //throw new KeyTalkProtocolException("Server requested for challenge which is not supported in this build");
                }

            } else if(isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while credential validation :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server after credential validation");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while credential validation :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server after credential validation");
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while credential Auth :"+e.toString());
            throw new KeyTalkProtocolException("Authentication failed and please try after some time!");
        }
        return null;
    }

    private AuthResult handlePhase2ResetExpiredPasswordResult(String serverMessage) throws KeyTalkProtocolException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {//String type, byte[] data) {
        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.auth_result)) {
                RCCDFileUtil.e("KeyTalk", "Client AuthReq response from server :" + ProtocolConstants.auth_result);
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status), ProtocolConstants.OK)) {
                    int expiryDay = -1;
                    return AuthResult.OK(expiryDay);
                }
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.LOCKED))
                    return AuthResult.Locked();
                if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.auth_status),ProtocolConstants.DELAY)) {
                    String delayTime = serverMessageJSONObject.getString(ProtocolConstants.delay);
                    int delay  = 10;
                    try {
                        delay = Integer.parseInt(delayTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return AuthResult.Delay(delay);
                }
            } else if(isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while credential validation :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server after credential validation");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while credential validation :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server after credential validation");
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while credential Auth :"+e.toString());
            throw new KeyTalkProtocolException("Authentication failed and please try after some time!");
        }
        return null;
    }






    private String[] handleServerMessageResultNew(String serverMessage) throws KeyTalkProtocolException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {//String type, byte[] data) {

        try {
            JSONObject serverMessageJSONObject = new JSONObject(serverMessage);
            if (isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.last_messages)) {
                RCCDFileUtil.e("KeyTalk", "Client AuthReq response from server :" + ProtocolConstants.last_messages);
                String[] msgResultContent = null;
                if(serverMessageJSONObject.has(ProtocolConstants.messages) && serverMessageJSONObject.getString(ProtocolConstants.messages) != null && !TextUtils.isEmpty(serverMessageJSONObject.getString(ProtocolConstants.messages))) {
                    JSONArray credArray = serverMessageJSONObject.getJSONArray(ProtocolConstants.messages);
                    if(credArray != null && credArray.length() > 0) {
                        msgResultContent = new String[credArray.length() * 2];
                        for(int i=0; i<credArray.length(); i = i +2) {
                            try {
                                JSONObject obj = credArray.getJSONObject(i);
                                //msgResultContent[i] = obj.getString("text") +"\n" + obj.getString("utc");
                                msgResultContent[i] = obj.getString("text");
                                msgResultContent[i+1] = obj.getString("utc");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                return msgResultContent;
            } else if(isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.eoc) ||isTypeMatch(serverMessageJSONObject.getString(ProtocolConstants.status), ProtocolConstants.error)) {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while Auth :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server");
            }  else {
                RCCDFileUtil.e("KeyTalk", "Communication terminated by server while Auth :"+serverMessageJSONObject.getString(ProtocolConstants.status));
                throw new KeyTalkProtocolException("Communication terminated by server " + serverMessageJSONObject.getString(ProtocolConstants.status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RCCDFileUtil.e("KeyTalk", "Communication terminated by server due to exception while Auth :"+e.toString());
            throw new KeyTalkProtocolException("Unable to retrive data from server");
        }
    }

    private boolean isTypeMatch(String serverMessage, String type){
        return serverMessage.equals(type);
    }


    /**
     * Class representing the possible authentication results
     */
    static public class AuthResult {
        private final int result;
        private final int seconds;
        private final int expirySeconds;
        private final String[] challengeData;
        private final String authReqString;

        private AuthResult(int result, int seconds, int expirySecond,String[] data, String authReqStringData) {
            this.result  = result;
            this.seconds = seconds;
            this.expirySeconds = expirySecond;
            this.challengeData = data;
            this.authReqString = authReqStringData;
        }

        private AuthResult(int result) {
            this(result, 0, -1,null,null);
        }

        private AuthResult(int result, String authReqData) {
            this(result, 0, -1,null,authReqData);
        }


        private AuthResult(int result, String[] data) {
            this(result, 0, -1 , data,null);
        }
        protected boolean isOK()     { return result == 1; }
        protected boolean isLocked() { return result == 2; }
        protected boolean isDelay()  { return result == 3; }
        protected boolean isExpired()  { return result == 4; }
        protected boolean isChallenge()  { return result == 5; }
        protected boolean isAuthReqChallenge()  { return result == 6; }

        protected int getSeconds()   {
            assert(isDelay());
            return seconds;
        }

        protected int getExpirySeconds() {
            assert(isOK());
            return expirySeconds;
        }

        protected String[] getChallengeData() {
            assert(isChallenge());
            return challengeData;
        }

        protected String getAuthReqChallengeData() {
            assert(isAuthReqChallenge());
            return authReqString;
        }

        protected static AuthResult OK(int expiryDay) {
            return new AuthResult(1,0,expiryDay,null,null);
        }
        protected static AuthResult Locked() { return new AuthResult(2); }
        protected static AuthResult Delay(int seconds) {
            assert(seconds >= 0);
            return new AuthResult(3, seconds,-1,null,null);
        }
        protected static AuthResult Expired() { return new AuthResult(4); }
        protected static AuthResult Challenge(String[] data) { return new AuthResult(5,data); }
        protected static AuthResult AuthReqChallange(String authReqData) { return new AuthResult(6,authReqData); }
    }
}

