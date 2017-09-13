package android.webkit;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/*
 * Class  :  ClientCertRequestHandler
 * Description : Adds Client Certificate specific handler for support certificate in to webview.
 * These are not part of the public WebView API, so the class is hidden.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public final class ClientCertRequestHandler {

    ClientCertRequestHandler() {}

    public void proceed(PrivateKey privateKey, X509Certificate[] chain) {}

    public void ignore() {}

    public void cancel() {}
}