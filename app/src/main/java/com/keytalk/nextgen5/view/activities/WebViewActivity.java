package com.keytalk.nextgen5.view.activities;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.WebSettings.PluginState;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;

/*
 * Class  :  WebViewActivity
 * Description : WebView Activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        // mWebView.getSettings().setPluginsEnabled(true);

        boolean isSucess = KeyTalkCommunicationManager.loadURL(mWebView, (ProgressBar)findViewById(R.id.progressBar), (TextView)findViewById(R.id.textView));
        if(!isSucess) {
            finish();
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        getBaseContext().deleteDatabase(getCacheDir()+"/databases/webview.db");
        getBaseContext().deleteDatabase(getCacheDir()+"/databases/webviewCache.db");
        mWebView.clearSslPreferences();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearMatches();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}