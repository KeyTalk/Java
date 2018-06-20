package com.keytalk.nextgen5.view.util;

/*
 * Class  :  AppConstants
 * Description : UI constants
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public final class AppConstants {

    public static final int SPLASH_DELAY_FIRST_RUN = 2000;
    public static final String IMPORTED_RCCD_FILE_FROM_EMAIL="imported_rccd_file_from_email";
    public static final String IMPORTED_RCCD_FILE_FROM_MEMORY="imported_rccd_file_from_memory";
    public static final String INI_FILE_PROVIDER_TEXT="Providers";
    public static final String INI_FILE_PROVIDER_NAME_TEXT="Name";
    public static final String INI_FILE_PROVIDER_SERVICE_TEXT="Services";
    public static final String INI_FILE_SERVICE_NAME_TEXT="Name";
    public static final String LABEL_OK = "Ok";

    public static final String IS_AUTH_REQUIRED_USER_NAME= "IS_AUTH_REQUIRED_USER_NAME";
    public static final String IS_AUTH_REQUIRED_PASSWORD = "IS_AUTH_REQUIRED_PASSWORD";
    public static final String IS_AUTH_REQUIRED_PASSWORD_TEXT = "IS_AUTH_REQUIRED_PASSWORD_TEXT";
    public static final String IS_AUTH_REQUIRED_PIN = "IS_AUTH_REQUIRED_PIN";
    public static final String IS_AUTH_REQUIRED_RESPONSE = "IS_AUTH_REQUIRED_RESPONSE";
    public static final String AUTH_SERVICE_USERS = "AUTH_SERVICE_USERS";
    public static final String AUTH_SERVICE_PASSWORD = "AUTH_SERVICE_PASSWORD";
    public static final String AUTH_SERVICE_PIN = "AUTH_SERVICE_PIN";
    public static final String AUTH_SERVICE_CHALLENGE = "AUTH_SERVICE_CHALLENGE";

    public static final String IS_CERT_REQUEST_ERROR = "IS_CERT_REQUEST_ERROR";
    public static final String CERT_REQUEST_ERROR_MSG = "CERT_REQUEST_ERROR_MSG";
    public static final String IS_CERT_REQUEST_SUCESS = "IS_CERT_REQUEST_SUCESS";
    public static final String IS_CERT_REQUEST_DELAY = "IS_CERT_REQUEST_DELAY";

    public static final String IS_CERT_REQUEST_DELAY_CREDENTIALS = "IS_CERT_REQUEST_DELAY_CREDENTIALS";
    public static final String IS_CERT_FROM_SERVER = "IS_CERT_FROM_SERVER";

    public static final String IS_RESET_CREDENTIALS_REQUEST = "IS_RESET_CREDENTIALS_REQUEST";
    public static final String IS_RESET_REQUEST_FROM_SERVER_USER = "IS_RESET_REQUEST_FROM_SERVER_USER";
    public static final String IS_RESET_REQUEST_FROM_SERVER_PWD = "IS_RESET_REQUEST_FROM_SERVER_PWD";
    public static final String IS_RESET_REQUEST_ERROR = "IS_RESET_REQUEST_ERROR";

    public static final String IS_CHALLENGE_CREDENTIALS_REQUEST = "IS_CHALLENGE_CREDENTIALS_REQUEST";
    public static final String IS_CHALLENGE_CREDENTIALS_DATA = "IS_CHALLENGE_CREDENTIALS_DATA";

    public static final String IS_NEW_CHALLENGE_CREDENTIALS_REQUEST = "IS_NEW_CHALLENGE_CREDENTIALS_REQUEST";
    public static final String IS_NEW_CHALLENGE_CREDENTIALS_DATA = "IS_NEW_CHALLENGE_CREDENTIALS_DATA";
    public static final String IS_NEW_RESPONSE_CREDENTIALS_DATA = "IS_NEW_RESPONSE_CREDENTIALS_DATA";

    public static final String IS_NEW_SERVER_URL_ADDED = "IS_NEW_SERVER_URL_ADDED";
    public static final String NEW_SERVER_URL_ADDED_BY_USER = "NEW_SERVER_URL_ADDED_BY_USER";

    public static final String SERVER_URL_FROM_RCCD = "SERVER_URL_FROM_RCCD";
    public static final String SERVER_URL_RCCD_FILE_NAME = "SERVER_URL_RCCD_FILE_NAME";


    public static final int DIALOG_NETWORK_ERROR = 5000;
    public static final int DIALOG_NO_DATA_IN_SEARCH_BOX = 5001;
    public static final int DIALOG_NO_DATA_IN_RESPONSE = 5002;
    public static final int DIALOG_INVALID_DATA_IN_RCCD_RESPONSE = 5003;
    public static final int DIALOG_INVALID_DATA_IN_RCCD_EMAIL = 5004;
    public static final int DIALOG_SUCESS_IMPORT_RCCD_FILE = 5005;
    public static final int DIALOG_RESET_RCCD_CONFIRM_MSG = 5006;
    public static final int DIALOG_RCCD_AUTH_ERROR_MSG = 5007;
    public static final int DIALOG_INVALID_USERNAME = 5008;
    public static final int DIALOG_INVALID_PASSWORD = 5009;
    public static final int DIALOG_INVALID_PINNUMBER = 5010;
    public static final int DIALOG_INVALID_CHALLENGE = 5011;
    public static final int DIALOG_SHOWMULTIPLE_USERNAME = 5012;
    public static final int DIALOG_CERT_SUCESSFULLY_RECEIVED = 5014;
    public static final int DIALOG_CERT_INSTALLATION_FAILED = 5015;
    public static final int DIALOG_RESET_SESSION_CONFIRM_MSG = 5017;
    public static final int DIALOG_INVALID_OLDPASSWORD = 5018;
    public static final int DIALOG_INVALID_NEWPASSWORD = 5019;
    public static final int DIALOG_INVALID_RETYPEPASSWORD = 5020;
    public static final int DIALOG_SAME_OLDNEWPASSWORD = 5021;
    public static final int DIALOG_DIFFRENT_NEWREPASSWORD = 5022;
    public static final int DIALOG_VIEW_IDENTITY = 5023;
    public static final int DIALOG_CERT_SUCESSFULLY_RECEIVED_MSG = 5024;
    public static final int  DIALOG_RCCD_UPDATE_SERVER_URL = 5026;
    public static final int DIALOG_SAME_URL = 5027;
    public static final int DIALOG_CHANGE_BROWSER_CONFIRM_MSG = 5028;
    public static final int DIALOG_REINSTALL_CERTIFICATE = 5029;
    public static final int DIALOG_PERMISSION_DENIED_MSG = 5030;
    public static final int DIALOG_EMPTY_TARGET_URL = 5031;
    public static final int DIALOG_REPORT_TO_ADMIN = 5032;
    public static final int DIALOG_RCCD_INVALID_ZIP_FILE_RESPONSE = 5033;
    public static final int DIALOG_RCCD_INVALID_CA_FILE_RESPONSE = 5034;
    public static final int REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY = 10010;
    public static final int REQUEST_CODE_CERT_INSTALL_ACTIVITY = 10011;
    public static final int REQUEST_CODE_RESET_PWD_ACTIVITY = 10012;
    public static final int REQUEST_CODE_UPDATE_SERVER_URL_ACTIVITY = 10013;

    public static final int REQUEST_READ_PHONE_STATE = 10025;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_STATE = 10026;


    public static final String ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL = "ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL";
    public static final String ALERT_DIALOG_TYPE_RESET_RCCD = "ALERT_DIALOG_TYPE_RESET_RCCD";
    public static final String ALERT_DIALOG_TYPE_RESET_SESSION = "ALERT_DIALOG_TYPE_RESET_SESSION";
    public static final String ALERT_DIALOG_TYPE_CLIENT_ID = "ALERT_DIALOG_TYPE_CLIENT_ID";
    public static final String ALERT_DIALOG_TYPE_EMPTY_URL = "ALERT_DIALOG_TYPE_EMPTY_URL";
    public static final String ALERT_DIALOG_TYPE_UNKNOWN = "ALERT_DIALOG_TYPE_UNKNOWN";
    public static final String ALERT_DIALOG_TYPE_RCCD_FROM_URL_FAILURE = "ALERT_DIALOG_TYPE_RCCD_FROM_URL_FAILURE";
    public static final String ALERT_DIALOG_TYPE_RCCD_FROM_URL_SUCESS = "ALERT_DIALOG_TYPE_RCCD_FROM_URL_SUCESS";
    public static final String ALERT_DIALOG_TYPE_NO_DATA_IN_RESPONSE = "ALERT_DIALOG_TYPE_NO_DATA_IN_RESPONSE";
    public static final String ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_RESPONSE = "ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_RESPONSE";

    public static final String SUCESS = "SUCESS";
    public static final String FAILURE = "FAILURE";



}


