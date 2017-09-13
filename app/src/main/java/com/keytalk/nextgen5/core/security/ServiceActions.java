package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Actions;

/*
 * Class  :  ServiceActions
 * Description : Support Class
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public enum ServiceActions implements Actions {
    MLS_RCCD_FILE_IMPORT_FROM_SERVER,
    MLS_RCCD_FILE_IMPORT_FROM_MAIL,
    MLS_RCCD_CONTENT_AUTH_REQUEST_TO_SERVER,
    MLS_RCCD_CERT_FROM_SERVER,
    MLS_RESET_PASSWORD_TO_SERVER;
}
