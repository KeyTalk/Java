package com.keytalk.nextgen5.core.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.keytalk.nextgen5.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.security.KeyChainException;
import android.view.View;
import android.webkit.ClientCertRequestHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * Class  :  KMWebViewClient
 * Description : An subclass of webview which will inject passed certificate during request to load the page
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KMWebViewClient extends WebViewClient {
    private final PrivateKey mPrivateKey;
    private final X509Certificate[] mCertChain;
    private ProgressBar mProgressBar = null;
    private TextView mTextView = null;
    private Context mContext = null;


    protected KMWebViewClient(Context context, PrivateKey key, X509Certificate[] chain) {
        mPrivateKey = key;
        mCertChain = chain;
        mContext = context;
    }

    protected KMWebViewClient(Context context, ProgressBar progressBar,PrivateKey key, X509Certificate[] chain) {
        mPrivateKey = key;
        mCertChain = chain;
        mProgressBar = progressBar;
        mContext = context;
    }

    protected KMWebViewClient(TextView textView,PrivateKey key, X509Certificate[] chain) {
        mPrivateKey = key;
        mCertChain = chain;
        mTextView = textView;
    }

    protected KMWebViewClient(ProgressBar progressBar,TextView textView, PrivateKey key, X509Certificate[] chain) {
        mPrivateKey = key;
        mCertChain = chain;
        mProgressBar = progressBar;
        mTextView = textView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                   SslError error) {
        // we ignore popups and proceed
        //handler.proceed();
        super.onReceivedSslError(view, handler, error);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setMessage("This certificate may access your data and do you want to continue?");
	    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            handler.proceed();
	        }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            handler.cancel();
	        }
	    });
	    final AlertDialog dialog = builder.create();
	    dialog.show();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    /**
     * Necessary to send our client certificate via WebView for Api 15.
     * This can still be used for Api 10-13, since the corresponding
     * event is never fired.
     * @param view
     * @param handler
     * @param host_and_port
     * @throws KeyChainException
     * @throws InterruptedException
     */
    public void onReceivedClientCertRequest(WebView view,
                                            ClientCertRequestHandler handler, String host_and_port) throws
            KeyChainException, InterruptedException{
        handler.proceed(mPrivateKey, mCertChain);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);
        if(mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
        if(mTextView != null)
            mTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        if(mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
        if(mTextView != null)
            mTextView.setVisibility(View.GONE);
    }

}
