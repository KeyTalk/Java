package android.webkit;

import android.net.http.SslError;

/*
 * Class  :  WebViewClientClassicExt
 * Description : Adds WebViewClassic specific extension methods to the WebViewClient callback class.
 * These are not part of the public WebView API, so the class is hidden.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */


public class WebViewClientClassicExt extends WebViewClient {

    /**
     * Notify the host application that an SSL error occurred while loading a
     * resource, but the WebView chose to proceed anyway based on a
     * decision retained from a previous response to onReceivedSslError().
     */
    public void onProceededAfterSslError(WebView view, SslError error) {
    }

    /**
     * Notify the host application to handle a SSL client certificate
     * request (display the request to the user and ask whether to
     * proceed with a client certificate or not). The host application
     * has to call either handler.cancel() or handler.proceed() as the
     * connection is suspended and waiting for the response. The
     * default behavior is to cancel, returning no client certificate.
     *
     * @param view The WebView that is initiating the callback.
     * @param handler A ClientCertRequestHandler object that will
     *            handle the user's response.
     * @param host_and_port The host and port of the requesting server.
     */
  /*  public void onReceivedClientCertRequest(WebView view,
            ClientCertRequestHandler handler, String host_and_port) {
        //handler.cancel();
    }*/
}