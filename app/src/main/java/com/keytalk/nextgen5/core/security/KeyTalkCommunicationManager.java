/*
 * Class  :  KeyTalkCommunicationManager
 * Description :
 *
 * Created By Jobin Mathew on 2018
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.core.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.security.KeyChain;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.AuthenticationCallBack;
import com.keytalk.nextgen5.core.AuthenticationCertificateCallBack;
import com.keytalk.nextgen5.core.RCCDDownloadCallBack;
import com.keytalk.nextgen5.core.ResetPasswordCallBack;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

/*
 * Class  :  KeyTalkCommunicationManager
 * Description : An subclass for handle all communication between UI and communication queue
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkCommunicationManager extends CommunicationViewHelper {

    //Certificate
    private static SelectedRCCDFileRequestData selectedRCCDFileRequestData = null;
    private static SelectedRCCDFileResponseData selectedRCCDFileResponseData = null;
    private static SelectedRCCDFileCertificateResponseData selectedRCCDFileCertificateResponseData = null;
    private static RCCDAuthenticationCommand<SelectedRCCDFileRequestData, SelectedRCCDFileResponseData> keyTalkAuthenticationCommand;
    //SDK
    private RCCDDownloadCallBack rccdFileDownloadCallBack = null;
    private AuthenticationCallBack authenticationCallBack = null;
    private static AuthenticationCertificateCallBack authenticationCertificateCallBack = null;
    private static ResetPasswordCallBack resetPasswordCallBack = null;
    Context context;


    public KeyTalkCommunicationManager(Object requestedInstance) {
        super((Activity)requestedInstance);
        if (requestedInstance instanceof RCCDDownloadCallBack)
            this.rccdFileDownloadCallBack = (RCCDDownloadCallBack) requestedInstance;
        else
            rccdFileDownloadCallBack = null;
        if(requestedInstance instanceof AuthenticationCallBack)
            this.authenticationCallBack = (AuthenticationCallBack) requestedInstance;
        else
            authenticationCallBack = null;
        authenticationCertificateCallBack = null;
        resetPasswordCallBack = null;
        selectedRCCDFileRequestData = null;
        selectedRCCDFileResponseData = null;
        keyTalkAuthenticationCommand = null;
    }


    @Override
    protected void initialize() {
        // TODO Auto-generated method stub
    }

    //Check any RCCD file available or not
    public static boolean isRCCDFileExist(Context context) {
        return new RCCDFileUtil().checkAnyRCCDFileExist(context.getFilesDir());
    }

    //Downloading RCCD file from Email and save it in to internal database
    public void getRCCDFileFromEmail(InputStream inputStream, String rccdFileName) {
        String providerName = null;
        int serviceCount = 0;
        int processStatus = 0;
        RCCDFileUtil rccdFileUtil = new RCCDFileUtil();
        String fileOperationStatus[] = rccdFileUtil.addRCCDFileToInternalStorage(getContext(), inputStream, rccdFileName);
        if (fileOperationStatus == null || fileOperationStatus[0].equals("") || fileOperationStatus[0] != SecurityConstants.SUCESS_FILE_OPERATIONS || !fileOperationStatus[0].equals(SecurityConstants.SUCESS_FILE_OPERATIONS)) {
            // Failed
            processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_EMAIL;
        } else {
            providerName = null;
            serviceCount = 0;
            IniResponseData iIniResponseData = null;
            if(fileOperationStatus[1] != null && !fileOperationStatus[1].isEmpty() && !fileOperationStatus[1].equals("")) {
                iIniResponseData = rccdFileUtil.readRCCDFile(getContext(), fileOperationStatus[1]);
            } else {
                iIniResponseData = rccdFileUtil.readRCCDFile(getContext(), rccdFileName);
            }
            ArrayList<IniResponseData> provider_list = iIniResponseData.getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_TEXT);
            if (provider_list != null && provider_list.size() > 0) {
                providerName = provider_list.get(0).getStringValue(SecurityConstants.INI_FILE_PROVIDER_NAME_TEXT);
                ArrayList<IniResponseData> services_list = provider_list.get(0).getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_SERVICE_TEXT);
                if (services_list != null && services_list.size() > 0) {
                    serviceCount = services_list.size();
                    // Sucess
                    processStatus = SecurityConstants.DIALOG_SUCESS_IMPORT_RCCD_FILE;
                } else {
                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                }
            } else {
                processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
            }
        }
        rccdFileDownloadCallBack.rccdDownloadCallBack(providerName, serviceCount,processStatus);
    }

    //Add log comments to application
    public static void addToLogFile(String tag, String message) {
        RCCDFileUtil.e(tag, message);
    }

    public static void addToLogFile(String message) {
        RCCDFileUtil.e(message);
    }

    //Remove all rccd files
    public static void removeAllRCCDFiles(Context context) {
        RCCDFileUtil rccdFileUtil = new RCCDFileUtil();
        rccdFileUtil.revomeAllRCCDFolders(context);
        rccdFileUtil.removeAllCertificates(context);
    }

    //Remove all certificates
    public static void removeAllCertificate(Context context) {
        new RCCDFileUtil().removeAllCertificates(context);
    }

    public static boolean isLogFileAvailable(Context context) {
        return RCCDFileUtil.saveLogcatToFile(context);
    }

    public static String getLogContents(Context context) {
        return RCCDFileUtil.getLogContents(context);
    }

    public static Uri getLogDetailsAsUri(Context context) {
        //return Uri.parse("file:/"+Environment.getExternalStorageDirectory().getAbsolutePath()+SecurityConstants.KEYTALK_LOGFILE_NAME);
        return RCCDFileUtil.getLogDetailsAsUri(context);
    }

    //Get all rccd file contents
    public static ArrayList<RCCDFileData> getAllRCCDFileContents(Context context) {
        addToLogFile("KeyTalkCommunicationManager.","Reading all RCCD files");
        return new RCCDFileUtil().getAllRCCDFileContents(context);
    }

    public static boolean updateServerURL(Context context, final String rccdFilePath,final String updatedURL) {
        return new RCCDFileUtil().updateIniServerURL(context,rccdFilePath,updatedURL);
    }

    //Remove all reference
    public static void updateNativeKeyStoreInstallationStatus(Context context, String keyValue, boolean status) {
        RCCDFileUtil.updateNativeKeyStoreInstallationStatus(context, keyValue, status);
    }

    //Remove all reference
    public static boolean getNativeKeyStoreInstallationStatus(Context context, String keyValue) {
        return RCCDFileUtil.getNativeKeyStoreInstallationStatus(context, keyValue);
    }


    public static void removeCertificatePreparedForNativeKeyChain(Context context) {
        if(android.os.Build.VERSION.SDK_INT >= 14) {
            final String serviceName = KeyTalkCommunicationManager.getServiceName().trim();
            new RCCDFileUtil().deletePFXFile(context, serviceName+".pfx");
        }
    }

    public static boolean loadURL(Context context, WebView mWebView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(context,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(context, selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(Context context, WebView mWebView,ProgressBar progressBar) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(context, progressBar,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(context, progressBar,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(WebView mWebView,TextView textView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(WebView mWebView,ProgressBar progressBar,TextView textView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(progressBar,textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(progressBar,textView, selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    private static void androidApi10Hack(SSLContext sslContext) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            try {
                // hack to change ssl socket factory for HttpsConnection class
                // this is not how SandroB makes it
                Class c = Class.forName("android.net.http.HttpsConnection");
                Field[] fieldlist = c.getDeclaredFields();
                for (int i = 0; i < fieldlist.length; i++) {
                    Field fld = fieldlist[i];
                    if (fld.getName().equals("mSslSocketFactory")) {
                        fld.setAccessible(true);
                        fld.set(null, sslContext.getSocketFactory());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean installCertificateonNativeKeyChain(Activity activity, int onActivityStatusID) {
        final String serviceName = KeyTalkCommunicationManager.getServiceName().trim();
        if(serviceName == null || serviceName.isEmpty()) {
            return false;
        } else {
            byte[] p12 = new RCCDFileUtil().getPFXFile(activity, serviceName+".pfx");
            if(p12 != null)  {
                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_PKCS12, p12);
                activity.startActivityForResult(intent, onActivityStatusID);
                return true;
            } else {
                return false;
            }
        }
    }

    public static String[] getServerMessage() {
        return selectedRCCDFileCertificateResponseData.getServerMsg();
    }

    public static String getServiceName() {
        return selectedRCCDFileCertificateResponseData.getServiceName().trim();
    }

    public static String getUrl() {
        return selectedRCCDFileCertificateResponseData.getUrl();
    }

    public static void setPasswordForNativeKeyStore(final String password) {
        KeyTalkSettings.setUserDefinedKeyStorePassword(password);
    }

    //sending user credentials - username

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username) Exception e" + e);
            return false;
        }
    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password) Exception e" + e);
            return false;
        }
    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, final String pinNumber,AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPin(pinNumber);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password,pin) Exception e" + e);
            return false;
        }

    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, final String pinNumber, final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPin(pinNumber);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setResponse(response);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password,pin,challenge) Exception e" + e);
            return false;
        }
    }

    public static boolean resetPassword(final String expiredPassword, final String newPassword, ResetPasswordCallBack resetPasswordCallBacks) {
        try {
            resetPasswordCallBack = resetPasswordCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RESET_PASSWORD_TO_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(expiredPassword);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setNewPassword(newPassword);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyNewCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch(Exception e) {
            addToLogFile("Communication Manager resetPassword Exception e"+e);
            return false;
        }
    }

    public static boolean resetPasswordLater() {
        try {
            selectedRCCDFileResponseData.getExpiredConsumer().isPasswordShouldReset(false);
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager resetPasswordLater Exception e" + e);
            return false;
        }
    }

    public static boolean resetPasswordNow() {
        try {
            selectedRCCDFileResponseData.getExpiredConsumer().isPasswordShouldReset(true);
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager resetPasswordNow Exception e" + e);
            return false;
        }
    }

    public static boolean restartAfterDelay(Runnable tryAgain) {
        try {
            tryAgain.run();
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager restartAfterDelay Exception e" + e);
            return false;
        }
    }

    public static boolean getCertificateWithChallange(final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(response);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyChallengeCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager getCertificateWithUserNamePasswordPinResponse Exception e"
                    + e);
            return false;
        }
    }

    public static boolean getCertificateWithNewChallange(final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setNewResponse(response);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyChallengeCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager getCertificateWithUserNamePasswordPinResponse Exception e"
                    + e);
            return false;
        }
    }


    public static void addUserNameTemp(final String selectedUserName) {
        String tempUserName = selectedRCCDFileRequestData.getServicesUsers();
        if(tempUserName != null && !tempUserName.isEmpty() && tempUserName.length() > 0 && !tempUserName.equals("")) {
            tempUserName = tempUserName.replace("\"", "").replace("[", "").replace("]", "").trim();
            String[] userNamesArray=tempUserName.split(",");
            if(userNamesArray != null && userNamesArray.length > 0 && !userNamesArray[0].toString().trim().isEmpty()) {
                tempUserName = "[";
                for(int i=0; i<userNamesArray.length; i++) {
                    tempUserName = tempUserName +userNamesArray[i] +",";
                }
                tempUserName = tempUserName + selectedUserName +"]";

                selectedRCCDFileRequestData.setServicesUsers(tempUserName);
            } else {
                tempUserName = "["+selectedUserName+"]";
                selectedRCCDFileRequestData.setServicesUsers(tempUserName);
            }
        } else {
            tempUserName = "["+selectedUserName+"]";
            selectedRCCDFileRequestData.setServicesUsers(tempUserName);
        }
    }

    //Downloading RCCD file from server through URL
    public void getRCCDFileFromURL(CommunicationLooper communicationLooper, String requestedURL) {
        RCCDFileRequestData rccdFileRequestData = new RCCDFileRequestData();
        rccdFileRequestData.setURL(requestedURL);
        RCCDFileImportCommand<RCCDFileRequestData, RCCDFileResponseData> rccdFileImportCommand = new RCCDFileImportCommand<RCCDFileRequestData, RCCDFileResponseData>(
                RCCDFileRequestData.class, RCCDFileResponseData.class);
        Request<RCCDFileRequestData> rccdRequest = new Request<RCCDFileRequestData>();
        rccdRequest.setAction(ServiceActions.MLS_RCCD_FILE_IMPORT_FROM_SERVER);
        rccdRequest.setData(rccdFileRequestData);
        rccdFileImportCommand.request = rccdRequest;
        rccdFileImportCommand.handler = this;
        rccdFileImportCommand.contex = getContext();
        addToLogFile("KeyTalkCommunicationManager","RCCD action code MLS_RCCD_FILE_IMPORT_FROM_SERVER");
        //KeyTalkApplication.getApp().processRequest(rccdFileImportCommand);
        processRequest(communicationLooper,rccdFileImportCommand);
    }

    public void initiateAuthenticationProcess(CommunicationLooper communicationLooper, RCCDFileData selectedRCCDFileData, int groupPosition, int childPosition) {
        selectedRCCDFileRequestData = new SelectedRCCDFileRequestData();
        selectedRCCDFileRequestData.setRccdFolderPath(selectedRCCDFileData.getRccdFilePath());
        IniResponseData iniResponseData = selectedRCCDFileData.getServiceData();
        selectedRCCDFileRequestData.setGroupPosition(groupPosition);
        selectedRCCDFileRequestData.setChildPosition(childPosition);
        selectedRCCDFileRequestData.setIniResponseData(iniResponseData);
        selectedRCCDFileRequestData.setConfigVersion(iniResponseData.getStringValue(SecurityConstants.CONFIGVERSIONNAME));
        selectedRCCDFileRequestData.setLatestProvider(iniResponseData.getStringValue(SecurityConstants.LATEST_PROVIDER));
        selectedRCCDFileRequestData.setLatestService(iniResponseData.getStringValue(SecurityConstants.LATEST_SERVICE));
        IniResponseData providerData = iniResponseData.getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_TEXT).get(0);
        selectedRCCDFileRequestData.setProvidersName(providerData.getStringValue(SecurityConstants.INI_FILE_PROVIDER_NAME_TEXT));
        selectedRCCDFileRequestData.setProvidersContentVersion(providerData.getStringValue(SecurityConstants.CONTENTVERSIONNAME));
        selectedRCCDFileRequestData.setProvidersCAs(providerData.getStringValue(SecurityConstants.CAS));
        selectedRCCDFileRequestData.setProvidersLogLevel(providerData.getStringValue(SecurityConstants.LOG_LEVEL));
        selectedRCCDFileRequestData.setProvidersServer(providerData.getStringValue(SecurityConstants.SERVER));
        IniResponseData servicedata = providerData.getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_SERVICE_TEXT).get(childPosition);
        selectedRCCDFileRequestData.setServicesCertValidPercent(servicedata.getStringValue(SecurityConstants.CERT_VALID_PERCENT));
        selectedRCCDFileRequestData.setServicesCertFormat(servicedata.getStringValue(SecurityConstants.CERT_FORMAT));
        selectedRCCDFileRequestData.setServicesCertChain(servicedata.getStringValue(SecurityConstants.CERT_CHAIN));
        selectedRCCDFileRequestData.setServicesName(servicedata.getStringValue(SecurityConstants.INI_FILE_PROVIDER_NAME_TEXT));
        selectedRCCDFileRequestData.setServicesUri(servicedata.getStringValue(SecurityConstants.URI));
        selectedRCCDFileRequestData.setServicesProxySettings(servicedata.getStringValue(SecurityConstants.PROXY_SETTINGS));
        selectedRCCDFileRequestData.setServicesUsers(servicedata.getStringValue(SecurityConstants.USERS));
        keyTalkAuthenticationCommand = new RCCDAuthenticationCommand<SelectedRCCDFileRequestData, SelectedRCCDFileResponseData>(
                SelectedRCCDFileRequestData.class, SelectedRCCDFileResponseData.class);
        Request<SelectedRCCDFileRequestData> authenticationRequest = new Request<SelectedRCCDFileRequestData>();
        authenticationRequest.setAction(ServiceActions.MLS_RCCD_CONTENT_AUTH_REQUEST_TO_SERVER);
        authenticationRequest.setData(selectedRCCDFileRequestData);
        keyTalkAuthenticationCommand.request = authenticationRequest;
        keyTalkAuthenticationCommand.handler = this;
        keyTalkAuthenticationCommand.contex = getContext();
        processRequest(communicationLooper,keyTalkAuthenticationCommand);
    }

    private synchronized void processRequest(CommunicationLooper appHttpQueue, BaseCommand<?, ?> command) {
        if (command.isHandleUsingsThread()) {
            if (command.putAtFrontOfQueue) {
                appHttpQueue.enqueCommandAtFrontOfQueue(command);
            } else {
                if (command.delayInMillis > 0) {
                    appHttpQueue.enqueCommandWithDelay(command,
                            command.delayInMillis);
                } else {
                    appHttpQueue.enqueueCommand(command);
                }
            }
        } else {
            command.execute();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
        // super.handleMessage(msg);
        if (msg != null) {
            Response<?> responseData = DataUtil.extractResponse(msg);
            if (responseData != null) {
                if (responseData.getAction() == ServiceActions.MLS_RCCD_FILE_IMPORT_FROM_SERVER	&& rccdFileDownloadCallBack != null) {
                    processRCCDImportResponse(responseData);
                } else if (responseData.getAction() == ServiceActions.MLS_RCCD_CONTENT_AUTH_REQUEST_TO_SERVER) {
                    if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_ERROR)) {
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        addToLogFile("Communication manger handle data Error Message : "+ selectedRCCDFileResponseData.getErrorMessage());
                        if (authenticationCallBack != null) {
                            authenticationCallBack.displayError(selectedRCCDFileResponseData.getErrorMessage());
                        } else {
                            // error
                            //should dismiss dialog
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS)) {
                        //Credential request
                        addToLogFile("Communication Manager Requesting Credentials..");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCallBack != null) {
                            String userName = "",challenge="",passwordText="";
                            if(selectedRCCDFileRequestData.getServicesUsers() != null && !selectedRCCDFileRequestData.getServicesUsers().isEmpty()) {
                                userName = selectedRCCDFileRequestData.getServicesUsers();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge().isEmpty()) {
                                challenge = selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText().isEmpty()) {
                                passwordText = selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText();
                            }
                            authenticationCallBack.credentialRequest(userName, selectedRCCDFileResponseData.getKeyTalkCredentials().isUsernameRequested(), selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested(), passwordText,selectedRCCDFileResponseData.getKeyTalkCredentials().isPinRequested(),selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested(),challenge);
                        }
                    }
                    else if (responseData.getMessageType().equals(ResponseType.AUTH_REQUEST_DATA_NOT_AVAILABLE)) {
                        //Error - Request Data not avialable
                        if (authenticationCallBack != null) {
                            authenticationCallBack.displayError( context.getString(R.string.sorry_please_try_after));
                        } else {
                            // error
                            //should dismiss dialog
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_REQUEST_PUBLIC_KEY_NOT_AVAILABLE)) {
                        //Error - public key not avialabe
                        if (authenticationCallBack != null) {
                            authenticationCallBack.displayError(context.getString(R.string.sorry_no_publickey_get_latest_RCCD_file_try_again));
                        } else {
                            // error
                            //should dismiss dialog
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_RELOAD_PAGE)) {
                        addToLogFile("Communication Manager Key Available..AUTH_RESPONCE_RELOAD_PAGE");
                        selectedRCCDFileCertificateResponseData = (SelectedRCCDFileCertificateResponseData) responseData.getData();
                        authenticationCallBack.validCertificateAvailable();
                    }

                } else if (responseData.getAction() == ServiceActions.MLS_RCCD_CERT_FROM_SERVER) {
                    //After credentials
                    if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_RELOAD_PAGE)) {
                        addToLogFile("Communication Manager Get Response credentials..AUTH_RESPONCE_RELOAD_PAGE");
                        selectedRCCDFileCertificateResponseData = (SelectedRCCDFileCertificateResponseData) responseData.getData();
                        authenticationCertificateCallBack.reloadPage();
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_ERROR)) {
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCallBack != null) {
                            authenticationCertificateCallBack.displayError(selectedRCCDFileResponseData.getErrorMessage());
                        } else {
                            // error
                            //should dismiss dialog
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_REQUEST_DELAY)) {
                        addToLogFile("Communication Manager Get Response credentials..AUTH_REQUEST_DELAY");
                        SelectedRCCDFileResponseData selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        authenticationCertificateCallBack.invalidCredentialsDelay(selectedRCCDFileResponseData.getSeconds(), selectedRCCDFileResponseData.getTryAgain());
                    }  else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS)) {
                        //Credential request
                        addToLogFile("Communication Manager Requesting Credentials.. after delay");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCertificateCallBack != null) {
                            String userName = "",challenge="",passwordText="";
                            if(selectedRCCDFileRequestData.getServicesUsers() != null && !selectedRCCDFileRequestData.getServicesUsers().isEmpty()) {
                                userName = selectedRCCDFileRequestData.getServicesUsers();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge().isEmpty()) {
                                challenge = selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText().isEmpty()) {
                                passwordText = selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText();
                            }
                            authenticationCertificateCallBack.credentialRequest(userName, selectedRCCDFileResponseData.getKeyTalkCredentials().isUsernameRequested(), selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested(), passwordText, selectedRCCDFileResponseData.getKeyTalkCredentials().isPinRequested(),selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested(),challenge);
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS)) {
                        //Credential Reset request
                        addToLogFile("Communication Manager Requesting reset Credentials.. ");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCertificateCallBack != null && selectedRCCDFileResponseData.getKeyTalkCredentials().isNewPasswordRequested()) {
                            String userName = null,expiredPassword= null;

                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().getUsername() != null && !selectedRCCDFileResponseData.getKeyTalkCredentials().getUsername().isEmpty()) {
                                userName = selectedRCCDFileResponseData.getKeyTalkCredentials().getUsername();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().getPassword() != null && !selectedRCCDFileResponseData.getKeyTalkCredentials().getPassword().isEmpty()) {
                                expiredPassword = selectedRCCDFileResponseData.getKeyTalkCredentials().getPassword();
                            }

                            if(userName != null && !userName.isEmpty() && expiredPassword != null && !expiredPassword.isEmpty()) {
                                authenticationCertificateCallBack.resetCredentials(userName, expiredPassword);
                            } else {
                                authenticationCertificateCallBack.displayError("We are not able to process your request now. Please try later");
                            }
                        } else {
                            //didmiss dialog
                        }
                    }

                    else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_OPTION)) {
                        //Credential Reset request option
                        addToLogFile("Communication Manager Requesting reset Credentials option.. ");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCertificateCallBack != null) {
                            authenticationCertificateCallBack.resetCredentialsOption(selectedRCCDFileResponseData.getKeyTalkCredentials().getExpiryDate());
                        } else {
                            //error dismiss dialog
                        }
                    }

                    //Get challange
                    else if (responseData.getMessageType().equals(ResponseType.AUTH_REQUEST_CHALLENGE)) {
                        //Credential Reset request option
                        addToLogFile("Communication Manager Requesting challange option.. ");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (authenticationCertificateCallBack != null) {
                            authenticationCertificateCallBack.requestChallange(selectedRCCDFileResponseData.getKeyTalkCredentials().isChallengeRequested(),
                                    selectedRCCDFileResponseData.getKeyTalkCredentials().getChallengeData(),
                                    selectedRCCDFileResponseData.getKeyTalkCredentials().isNewAuthReqChallengeRequested(),
                                    selectedRCCDFileResponseData.getKeyTalkCredentials().getNewAuthReqChallengeData());
                        } else {
                            //error dismiss dialog
                        }
                    }


                } else if (responseData.getAction() == ServiceActions.MLS_RESET_PASSWORD_TO_SERVER) {
                    addToLogFile("Communication Manager Reset Password data handling.....");
                    if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_DELAY)) {
                        addToLogFile("Communication Manager Get reset Response credentials..AUTH_RESPONCE_REQUIRE_RESET_CREDENTIALS_DELAY");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (resetPasswordCallBack != null && selectedRCCDFileResponseData.getKeyTalkCredentials().isNewPasswordRequested()) {
                            resetPasswordCallBack.passwordResetDelay(selectedRCCDFileResponseData.getSeconds());
                        } else {
                            //error
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_ERROR)) {
                        addToLogFile("Communication Manager Get reset Response credentials..AUTH_RESPONCE_ERROR");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (resetPasswordCallBack != null) {
                            addToLogFile("Communication Manager Get reset Response credentials 1 ..AUTH_RESPONCE_ERROR");
                            resetPasswordCallBack.passwordResetError(selectedRCCDFileResponseData.getErrorMessage());
                        } else {
                            addToLogFile("Communication Manager Get reset Response credentials 2 ..AUTH_RESPONCE_ERROR");
                            // error
                            //should dismiss dialog
                        }
                    } else if (responseData.getMessageType().equals(ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS)) {
                        //Credential request
                        addToLogFile("Communication Manager Requesting Credentials.. after reset");
                        selectedRCCDFileResponseData = (SelectedRCCDFileResponseData) responseData.getData();
                        if (resetPasswordCallBack != null) {
                            String userName = "",challenge="", passwordText="";
                            if(selectedRCCDFileRequestData.getServicesUsers() != null && !selectedRCCDFileRequestData.getServicesUsers().isEmpty()) {
                                userName = selectedRCCDFileRequestData.getServicesUsers();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge().isEmpty()) {
                                challenge = selectedRCCDFileResponseData.getKeyTalkCredentials().getChallenge();
                            }
                            if(selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested() && !selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText().isEmpty()) {
                                passwordText = selectedRCCDFileResponseData.getKeyTalkCredentials().getPasswordText();
                            }
                            resetPasswordCallBack.credentialRequest(userName, selectedRCCDFileResponseData.getKeyTalkCredentials().isUsernameRequested(), selectedRCCDFileResponseData.getKeyTalkCredentials().isPasswordRequested(),passwordText,selectedRCCDFileResponseData.getKeyTalkCredentials().isPinRequested(),selectedRCCDFileResponseData.getKeyTalkCredentials().isResponseRequested(),challenge);
                        }
                    }
                }
            } else {
                // Error
            }
        } else {
            // Error
        }
    }

    //process received rccd file status
    private void processRCCDImportResponse(Response<?> response) {
        String providerName = null;
        int serviceCount = 0;
        int processStatus = 0;
        if (response != null) {
            if (response.getAction() == ServiceActions.MLS_RCCD_FILE_IMPORT_FROM_SERVER) {
                if (response.getMessageType().equals(ResponseType.RESPONSE_SUCCESS)) {
                    RCCDFileResponseData rccdFileImportResponseData = (RCCDFileResponseData) response.getData();
                    if (rccdFileImportResponseData.getResponseHeader().isSuccess()
                            && rccdFileImportResponseData.getResponseHeader().getFileOperationStatus().equals(SecurityConstants.SUCESS_FILE_OPERATIONS)) {
                        if (rccdFileImportResponseData.getIiniResponseData() != null) {
                            providerName = null;
                            serviceCount = 0;
                            IniResponseData iIniResponseData = (IniResponseData) rccdFileImportResponseData.getIiniResponseData();
                            ArrayList<IniResponseData> provider_list = iIniResponseData.getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_TEXT);
                            if (provider_list != null&& provider_list.size() > 0) {
                                providerName = provider_list.get(0).getStringValue(SecurityConstants.INI_FILE_PROVIDER_NAME_TEXT);
                                ArrayList<IniResponseData> services_list = provider_list.get(0).getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_SERVICE_TEXT);
                                if (services_list != null&& services_list.size() > 0) {
                                    serviceCount = services_list.size();
                                    addToLogFile("Communication Manager providerNae and count :"+ providerName + ","+ serviceCount);
                                    // Sucess
                                    processStatus = SecurityConstants.DIALOG_SUCESS_IMPORT_RCCD_FILE;
                                } else {
                                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                                }
                            } else {
                                processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                            }
                        } else {
                            processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                        }
                    } else {
                        // failed something went wrong
                        if(rccdFileImportResponseData.getResponseHeader().getFileOperationStatus().equals(SecurityConstants.ERROR_VALIDATION_OPERATIONS)) {
                            processStatus = SecurityConstants.DIALOG_RCCD_INVALID_CA_FILE_RESPONSE;
                        } else if(rccdFileImportResponseData.getResponseHeader().getFileOperationStatus().equals(SecurityConstants.ERROR_UNZIP_OPERATIONS)) {
                            processStatus = SecurityConstants.DIALOG_RCCD_INVALID_ZIP_FILE_RESPONSE;
                        } else {
                            processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                        }
                    }
                } else if (response.getMessageType().equals(ResponseType.RESPONSE_FAILURE)) {
                    // failed Bad response
                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                } else if (response.getMessageType().equals(ResponseType.SC_SERVICE_UNAVAILABLE)) {
                    // failed Service unavailable
                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                } else {
                    // failed
                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                }
            }
        } else {
            processStatus = SecurityConstants.DIALOG_NO_DATA_IN_RESPONSE;
        }
        rccdFileDownloadCallBack.rccdDownloadCallBack(providerName, serviceCount,processStatus);
    }


    /*










    //RCCD file phase 1














    //Downloading RCCD file from Email and save it in to internal database
    public void getRCCDFileFromEmail(InputStream inputStream, String rccdFileName) {
        String providerName = null;
        int serviceCount = 0;
        int processStatus = 0;
        RCCDFileUtil rccdFileUtil = new RCCDFileUtil();
        String fileOperationStatus[] = rccdFileUtil.addRCCDFileToInternalStorage(getContext(), inputStream, rccdFileName);
        if (fileOperationStatus == null || fileOperationStatus[0].equals("") || fileOperationStatus[0] != SecurityConstants.SUCESS_FILE_OPERATIONS || !fileOperationStatus[0].equals(SecurityConstants.SUCESS_FILE_OPERATIONS)) {
            // Failed
            processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_EMAIL;
        } else {
            providerName = null;
            serviceCount = 0;
            IniResponseData iIniResponseData = null;
            if(fileOperationStatus[1] != null && !fileOperationStatus[1].isEmpty() && !fileOperationStatus[1].equals("")) {
                iIniResponseData = rccdFileUtil.readRCCDFile(getContext(), fileOperationStatus[1]);
            } else {
                iIniResponseData = rccdFileUtil.readRCCDFile(getContext(), rccdFileName);
            }
            ArrayList<IniResponseData> provider_list = iIniResponseData.getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_TEXT);
            if (provider_list != null && provider_list.size() > 0) {
                providerName = provider_list.get(0).getStringValue(SecurityConstants.INI_FILE_PROVIDER_NAME_TEXT);
                ArrayList<IniResponseData> services_list = provider_list.get(0).getIniArrayValue(SecurityConstants.INI_FILE_PROVIDER_SERVICE_TEXT);
                if (services_list != null && services_list.size() > 0) {
                    serviceCount = services_list.size();
                    // Sucess
                    processStatus = SecurityConstants.DIALOG_SUCESS_IMPORT_RCCD_FILE;
                } else {
                    processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
                }
            } else {
                processStatus = SecurityConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE;
            }
        }
        rccdFileDownloadCallBack.rccdDownloadCallBack(providerName, serviceCount,processStatus);
    }


    //Add log comments to application
    public static void addToLogFile(String tag, String message) {
        RCCDFileUtil.e(tag, message);
    }

    public static boolean isLogFileAvailable(Context context) {
        return RCCDFileUtil.saveLogcatToFile(context);
    }

    public static Uri getLogDetailsAsUri(Context context) {
        //return Uri.parse("file:/"+Environment.getExternalStorageDirectory().getAbsolutePath()+SecurityConstants.KEYTALK_LOGFILE_NAME);
        return RCCDFileUtil.getLogDetailsAsUri(context);
    }

    //Remove all rccd files
    public static void removeAllRCCDFiles(Context context) {
        RCCDFileUtil rccdFileUtil = new RCCDFileUtil();
        rccdFileUtil.revomeAllRCCDFolders(context);
        rccdFileUtil.removeAllCertificates(context);
    }

    //Remove all certificates
    public static void removeAllCertificate(Context context) {
        new RCCDFileUtil().removeAllCertificates(context);
    }

    //Remove all reference
    public static void updateNativeKeyStoreInstallationStatus(Context context, String keyValue, boolean status) {
        RCCDFileUtil.updateNativeKeyStoreInstallationStatus(context, keyValue, status);
    }

    //Remove all reference
    public static boolean getNativeKeyStoreInstallationStatus(Context context, String keyValue) {
        return RCCDFileUtil.getNativeKeyStoreInstallationStatus(context, keyValue);
    }

    //Get all rccd file contents
    public static ArrayList<RCCDFileData> getAllRCCDFileContents(Context context) {
        addToLogFile("KeyTalkCommunicationManager.","Reading all RCCD files");
        return new RCCDFileUtil().getAllRCCDFileContents(context);
    }

    //sending user credentials - username

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username) Exception e" + e);
            return false;
        }
    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password) Exception e" + e);
            return false;
        }
    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, final String pinNumber,AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPin(pinNumber);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password,pin) Exception e" + e);
            return false;
        }

    }

    public static boolean sendUserCredentialsForCertificate(final String selectedUserName, final String password, final String pinNumber, final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setUsername(selectedUserName);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(password);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPin(pinNumber);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setResponse(response);
            selectedRCCDFileResponseData.getKeyTalkCredentialsConsumer().supplyCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager sendUserCredentialsForCertificate(username,password,pin,challenge) Exception e" + e);
            return false;
        }
    }

    public static boolean resetPassword(final String expiredPassword, final String newPassword, ResetPasswordCallBack resetPasswordCallBacks) {
        try {
            resetPasswordCallBack = resetPasswordCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RESET_PASSWORD_TO_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(expiredPassword);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setNewPassword(newPassword);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyNewCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch(Exception e) {
            addToLogFile("Communication Manager resetPassword Exception e"+e);
            return false;
        }
    }

    public static boolean resetPasswordLater() {
        try {
            selectedRCCDFileResponseData.getExpiredConsumer().isPasswordShouldReset(false);
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager resetPasswordLater Exception e" + e);
            return false;
        }
    }

    public static boolean resetPasswordNow() {
        try {
            selectedRCCDFileResponseData.getExpiredConsumer().isPasswordShouldReset(true);
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager resetPasswordNow Exception e" + e);
            return false;
        }
    }

    public static boolean restartAfterDelay(Runnable tryAgain) {
        try {
            tryAgain.run();
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager restartAfterDelay Exception e" + e);
            return false;
        }
    }

    public static boolean getCertificateWithChallange(final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setPassword(response);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyChallengeCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager getCertificateWithUserNamePasswordPinResponse Exception e"
                    + e);
            return false;
        }
    }

    public static boolean getCertificateWithNewChallange(final String response, AuthenticationCertificateCallBack authenticationCertificateCallBacks) {
        try {
            authenticationCertificateCallBack = authenticationCertificateCallBacks;
            keyTalkAuthenticationCommand.request.setAction(ServiceActions.MLS_RCCD_CERT_FROM_SERVER);
            selectedRCCDFileResponseData.getKeyTalkCredentials().setNewResponse(response);
            selectedRCCDFileResponseData.getExpiredConsumer().supplyChallengeCredentials(selectedRCCDFileResponseData.getKeyTalkCredentials());
            return true;
        } catch (Exception e) {
            addToLogFile("Communication Manager getCertificateWithUserNamePasswordPinResponse Exception e"
                    + e);
            return false;
        }
    }

    public static void addUserNameTemp(final String selectedUserName) {
        String tempUserName = selectedRCCDFileRequestData.getServicesUsers();
        if(tempUserName != null && !tempUserName.isEmpty() && tempUserName.length() > 0 && !tempUserName.equals("")) {
            tempUserName = tempUserName.replace("\"", "").replace("[", "").replace("]", "").trim();
            String[] userNamesArray=tempUserName.split(",");
            if(userNamesArray != null && userNamesArray.length > 0 && !userNamesArray[0].toString().trim().isEmpty()) {
                tempUserName = "[";
                for(int i=0; i<userNamesArray.length; i++) {
                    tempUserName = tempUserName +userNamesArray[i] +",";
                }
                tempUserName = tempUserName + selectedUserName +"]";

                selectedRCCDFileRequestData.setServicesUsers(tempUserName);
            } else {
                tempUserName = "["+selectedUserName+"]";
                selectedRCCDFileRequestData.setServicesUsers(tempUserName);
            }
        } else {
            tempUserName = "["+selectedUserName+"]";
            selectedRCCDFileRequestData.setServicesUsers(tempUserName);
        }
    }

    public static void addToLogFile(String message) {
        RCCDFileUtil.e(message);
    }

    public static boolean updateServerURL(Context context, final String rccdFilePath,final String updatedURL) {
        return new RCCDFileUtil().updateIniServerURL(context,rccdFilePath,updatedURL);
    }

    public static void removeCertificatePreparedForNativeKeyChain(Context context) {
        if(android.os.Build.VERSION.SDK_INT >= 14) {
            final String serviceName = KeyTalkCommunicationManager.getServiceName().trim();
            new RCCDFileUtil().deletePFXFile(context, serviceName+".pfx");
        }
    }

    public static boolean loadURL(Context context, WebView mWebView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(context,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(context, selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(Context context, WebView mWebView,ProgressBar progressBar) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(context, progressBar,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(context, progressBar,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(WebView mWebView,TextView textView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadURL(WebView mWebView,ProgressBar progressBar,TextView textView) {
        if(selectedRCCDFileCertificateResponseData != null) {
            if (selectedRCCDFileCertificateResponseData.getSslContext() != null) {
                androidApi10Hack(selectedRCCDFileCertificateResponseData.getSslContext());
            }
            mWebView.loadUrl(selectedRCCDFileCertificateResponseData.getUrl().trim());
            if (android.os.Build.VERSION.SDK_INT <= 16) {
                mWebView.setWebViewClient(new KMWebViewClient(progressBar,textView,selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            } else {
                mWebView.setWebViewClient( new KMWebViewClientForJellyBean(progressBar,textView, selectedRCCDFileCertificateResponseData.getKey(), selectedRCCDFileCertificateResponseData.getKeyChain()));
            }
            return true;
        } else {
            return false;
        }
    }

    private static void androidApi10Hack(SSLContext sslContext) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            try {
                // hack to change ssl socket factory for HttpsConnection class
                // this is not how SandroB makes it
                Class c = Class.forName("android.net.http.HttpsConnection");
                Field[] fieldlist = c.getDeclaredFields();
                for (int i = 0; i < fieldlist.length; i++) {
                    Field fld = fieldlist[i];
                    if (fld.getName().equals("mSslSocketFactory")) {
                        fld.setAccessible(true);
                        fld.set(null, sslContext.getSocketFactory());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean installCertificateonNativeKeyChain(Activity activity, int onActivityStatusID) {
        final String serviceName = KeyTalkCommunicationManager.getServiceName().trim();
        if(serviceName == null || serviceName.isEmpty()) {
            return false;
        } else {
            byte[] p12 = new RCCDFileUtil().getPFXFile(activity, serviceName+".pfx");
            if(p12 != null)  {
                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_PKCS12, p12);
                activity.startActivityForResult(intent, onActivityStatusID);
                return true;
            } else {
                return false;
            }
        }
    }

    public static String[] getServerMessage() {
        return selectedRCCDFileCertificateResponseData.getServerMsg();
    }

    public static String getServiceName() {
        return selectedRCCDFileCertificateResponseData.getServiceName().trim();
    }

    public static String getUrl() {
        return selectedRCCDFileCertificateResponseData.getUrl();
    }

    public static void setPasswordForNativeKeyStore(final String password) {
        KeyTalkSettings.setUserDefinedKeyStorePassword(password);
    }


    */
}
