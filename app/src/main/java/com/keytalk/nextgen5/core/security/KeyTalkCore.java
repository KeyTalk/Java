package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.keytalk.nextgen5.core.KeyTalkCertificateConsumer;
import com.keytalk.nextgen5.core.KeyTalkCredentialsConsumer;
import com.keytalk.nextgen5.core.KeyTalkExpiredCredentialConsumer;
import com.keytalk.nextgen5.core.KeyTalkUiCallback;
import com.keytalk.nextgen5.util.Keys;
import com.keytalk.nextgen5.util.PreferenceManager;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * Class  :  KeyTalkCore
 * Description : An class which checking the availability of certificate and initiate a new certificate request
 * if certificate not available or expired
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkCore {

    private KeyTalkSettings mSettings;
    private Context mContext;
    private SelectedRCCDFileRequestData selectedRCCDFileRequestData;
    protected KeyTalkCore(Context c,SelectedRCCDFileRequestData requestData ) {
        mContext = c;
        mSettings = new KeyTalkSettings(mContext);
        selectedRCCDFileRequestData = requestData;
    }

    protected void startURLAuthentication(final KeyTalkUiCallback keyTalkUiCallback) {
        try {
            if (mSettings.validCertAvailable(selectedRCCDFileRequestData.getServicesUri().trim(), selectedRCCDFileRequestData.getServicesName().trim())) {
                try {
                    RCCDFileUtil.e("KeyTalk","Valid certificate available for "+selectedRCCDFileRequestData.getServicesUri().trim());
                    CertificateInfo cert = mSettings.readKeyStore(selectedRCCDFileRequestData.getServicesUri().trim(),selectedRCCDFileRequestData.getServicesName().trim());
                    boolean isNative = PreferenceManager.getBoolean(mContext, Keys.PreferenceKeys.DEVICE_TYPE);
                    if(android.os.Build.VERSION.SDK_INT >= 14 && isNative) {
                        boolean isAddedToNativeKeyStore = KeyTalkCommunicationManager.getNativeKeyStoreInstallationStatus(mContext, selectedRCCDFileRequestData.getServicesUri().trim());
                        if(!isAddedToNativeKeyStore) {
                            mSettings.createPFXFile(cert);
                        }
                    }

                    keyTalkUiCallback.reloadPage(selectedRCCDFileRequestData.getServicesUri().trim(), cert.createSSLContext(),cert.getPrivateKey(), cert.getCertificateChain(),selectedRCCDFileRequestData.getServicesName().trim(), null);
                } catch (Exception e) {
                    RCCDFileUtil.e("KeyTalk","Valid certificate available, but exception "+e);
                    keyTalkUiCallback.displayError(e);
                }
            } else {
                RCCDFileUtil.e("KeyTalk","Valid certificate not available");
                mSettings.deleteCertificate(selectedRCCDFileRequestData.getServicesUri().trim(), selectedRCCDFileRequestData.getServicesName().trim());
                getNewCertFromServer(keyTalkUiCallback);
            }
        } catch (Exception e) {
            // TODO: handle exception
            mSettings.deleteCertificate(selectedRCCDFileRequestData.getServicesUri().trim(), selectedRCCDFileRequestData.getServicesName().trim());
            RCCDFileUtil.e("KeyTalk","certificate search getting exception "+e);
            getNewCertFromServer(keyTalkUiCallback);
        }
    }

    private void getNewCertFromServer(final KeyTalkUiCallback keyTalkUiCallback) {
        String providerURLFromIni = selectedRCCDFileRequestData.getProvidersServer();
        String tempString = providerURLFromIni;
        providerURLFromIni = (providerURLFromIni.replace(ProtocolConstants.HTTP, "")).replace(ProtocolConstants.HTTPS, "");
        if(providerURLFromIni.substring(providerURLFromIni.length() - 7).equals(ProtocolConstants.KEYWORD)) {
            providerURLFromIni = providerURLFromIni.substring(0,providerURLFromIni.length() - 7);
        } else if(providerURLFromIni.substring(providerURLFromIni.length() - 1).equals(ProtocolConstants.SEPERATOR)) {
            providerURLFromIni = providerURLFromIni.substring(0, providerURLFromIni.length() - 1);
        }
        if(providerURLFromIni.split(ProtocolConstants.COLOUMN).length == 1) {
            providerURLFromIni = providerURLFromIni + ProtocolConstants.FULL_KEYWORD_V2;
        } else {
            providerURLFromIni = providerURLFromIni + ProtocolConstants.SLASH + ProtocolConstants.RCDP + ProtocolConstants.SLASH + ProtocolConstants.protocolVersion2_0_0;
        }
        if(tempString.startsWith(ProtocolConstants.HTTPS)) {
            providerURLFromIni = ProtocolConstants.HTTPS + providerURLFromIni;
        } else if(tempString.startsWith(ProtocolConstants.HTTP)) {
            providerURLFromIni = providerURLFromIni.substring(ProtocolConstants.HTTP.length(),providerURLFromIni.length());
            providerURLFromIni = ProtocolConstants.HTTPS + providerURLFromIni;
        } else {
            providerURLFromIni = ProtocolConstants.HTTPS + providerURLFromIni;
        }

        KeyTalkCommunicationManager.addToLogFile("KeyTalk","server URL : "+providerURLFromIni);
        KeyTalkHttpProtocol http = new KeyTalkHttpProtocol(providerURLFromIni);
        PayloadBuilder payload = new PayloadBuilder(selectedRCCDFileRequestData);
        KeyTalkProtocol protocol    = new KeyTalkProtocol(http, payload, mContext);

        KeyTalkAsyncCertificateRequest request = new KeyTalkAsyncCertificateRequest(protocol, new KeyTalkCertificateConsumer() {
            @Override
            public void errorOccurred(Throwable t) {
                RCCDFileUtil.e("KeyTalk","Async getting error "+t.toString());
                keyTalkUiCallback.displayError(t);
            }

            @Override
            public void requestCredentials(KeyTalkCredentials creds, KeyTalkCredentialsConsumer consumer) {
                // Do already fill in hardware signature if requested
                //System.out.println("creds.isUsernameRequested()01 : "+creds.isHardwareSignatureRequested()+","+creds.getHardwareFormula());
                RCCDFileUtil.e("KeyTalk","requestCredentials got called");
                supplyHardwareInformation(creds);
                if (creds.isUserInputRequested()) {
                    // Pass request to client, let it handle requesting the credentials from the user
                    RCCDFileUtil.e("KeyTalk","User credentials required ");
                    keyTalkUiCallback.requestCredentials(creds, consumer);
                } else {
                    // Hardware signature suffices, continue immediately
                    RCCDFileUtil.e("KeyTalk","User credentials not required ");
                    consumer.supplyCredentials(creds);
                }
            }

            @Override
            public void certificateRetrieved(CertificateInfo cert, String selectedUserName, String[] serverMsg) {
                try {
                    boolean isINIUpdateSucess = false;
                    String formattedNewURL = null,formattedOldURL = null,serverURL = null;
                    if(selectedRCCDFileRequestData.getServicesNewUri() != null && !selectedRCCDFileRequestData.getServicesNewUri().isEmpty() &&
                            selectedRCCDFileRequestData.getServicesNewUri().length() > 0 ) {
                        formattedNewURL = selectedRCCDFileRequestData.getServicesNewUri();
                        formattedNewURL = (formattedNewURL.replace(ProtocolConstants.HTTP, "")).replace(ProtocolConstants.HTTPS, "");
                        formattedNewURL = formattedNewURL.split(ProtocolConstants.SEPERATOR)[0];
                    }
                    if(selectedRCCDFileRequestData.getServicesUri() != null && !selectedRCCDFileRequestData.getServicesUri().isEmpty() &&
                            selectedRCCDFileRequestData.getServicesUri().length() > 0 ) {
                        formattedOldURL = selectedRCCDFileRequestData.getServicesUri();
                        formattedOldURL = (formattedOldURL.replace(ProtocolConstants.HTTP, "")).replace(ProtocolConstants.HTTPS, "");
                        formattedOldURL = formattedOldURL.split("/")[0];
                    }

                    if(formattedNewURL == null || formattedNewURL.isEmpty() || formattedNewURL.equals(""))
                        serverURL = selectedRCCDFileRequestData.getServicesUri();
                    else {
                        if(formattedOldURL.indexOf(formattedNewURL) == -1) {
                            serverURL = selectedRCCDFileRequestData.getServicesNewUri().trim();
                            //Update ini file
                            RCCDFileUtil.e("KeyTalk","certificate retrived and updating target url :"+serverURL);
                            isINIUpdateSucess = new RCCDFileUtil().updateIniFile(mContext , selectedRCCDFileRequestData.getRccdFolderPath(),
                                    selectedRCCDFileRequestData.getChildPosition(), serverURL);

                        } else {
                            serverURL = selectedRCCDFileRequestData.getServicesUri().trim();
                        }
                    }

                    if(selectedUserName.trim() != null && !selectedUserName.trim().isEmpty()) {
                        new RCCDFileUtil().addUserNameToIniFile(mContext , selectedRCCDFileRequestData.getRccdFolderPath(),
                                selectedRCCDFileRequestData.getChildPosition(), selectedUserName.trim());
                    }

                    mSettings.writeKeyStore(cert,serverURL);
                    if(android.os.Build.VERSION.SDK_INT >= 14) {
                        mSettings.createPFXFile(cert);
                    }
                    if(serverMsg != null && serverMsg.length > 0 ) {
                        //New Code
                        try {
                            String timeStamp = null;
                            if(serverMsg[0] != null && !serverMsg[0].isEmpty()) {
                                timeStamp = serverMsg[0].trim();
                            }
                            int messageLength = serverMsg.length;
                            if(messageLength % 2 == 0) {
                                String tempTimeStamp = serverMsg[messageLength - 2];
                                if(tempTimeStamp != null && !tempTimeStamp.isEmpty()) {
                                    timeStamp = tempTimeStamp.trim();
                                }
                            }
                            SharedPreferences sharedPreference = mContext.getSharedPreferences(ProtocolConstants.MESSAGE_STAMP, 0);
                            SharedPreferences.Editor editor = sharedPreference.edit();
                            String dbTimeStamp = sharedPreference.getString(ProtocolConstants.TIMESTAMP, null);
                            if(timeStamp != null && !timeStamp.isEmpty()) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                                TimeZone tz = TimeZone.getTimeZone("UTC");
                                simpleDateFormat.setTimeZone(tz);
                                Date date = simpleDateFormat.parse(timeStamp);
                                date.setSeconds(date.getSeconds() + 1);
                                timeStamp = simpleDateFormat.format(date);
                                timeStamp = timeStamp.replace("UTC","");
                                if(!timeStamp.endsWith("+0000"))
                                    timeStamp = timeStamp + "+0000";
                                editor.putString(ProtocolConstants.TIMESTAMP, timeStamp);
                                editor.commit();
                            }
                        } catch (Exception e) { }
                    }
                    keyTalkUiCallback.reloadPage(serverURL, cert.createSSLContext(), cert.getPrivateKey(), cert.getCertificateChain(),selectedRCCDFileRequestData.getServicesName().trim(), serverMsg);
                } catch (Exception ex) {
                    errorOccurred(ex);
                }
            }

            @Override
            public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
                keyTalkUiCallback.invalidCredentialsDelay(seconds, tryAgain);
            }

            @Override
            public void requestResetCredentials(KeyTalkCredentials creds,KeyTalkExpiredCredentialConsumer expiredConsumer) {
                // TODO Auto-generated method stub
                keyTalkUiCallback.requestResetCredentials(creds, expiredConsumer);
            }

            @Override
            public void requestResetCredentialsDelay(int seconds) {
                // TODO Auto-generated method stub
                keyTalkUiCallback.requestResetCredentialsDelay(seconds);
            }

            @Override
            public void resetCredentialOption(KeyTalkCredentials creds,
                                              KeyTalkExpiredCredentialConsumer expiredConsumer) {
                // TODO Auto-generated method stub
                keyTalkUiCallback.resetCredentialOption(creds, expiredConsumer);
            }

            @Override
            public void requestChallengeCredentials(KeyTalkCredentials creds,
                                                    KeyTalkExpiredCredentialConsumer expiredConsumer) {
                // TODO Auto-generated method stub
                keyTalkUiCallback.requestChallengeCredentials(creds, expiredConsumer);

            }

        });
        request.start();
    }

    /**
     * Add the hardware signature to the credentials object if requested
     */
    private void supplyHardwareInformation(KeyTalkCredentials creds) {
        if (creds.isHardwareSignatureRequested()) {
            Log.e("TAG", "Hardware formula requested : "+creds.getHardwareFormula());
            creds.setHardwareSignature(KeyTalkUtils.getHwSig(creds.getHardwareFormula(), mContext));
        }
    }

}
