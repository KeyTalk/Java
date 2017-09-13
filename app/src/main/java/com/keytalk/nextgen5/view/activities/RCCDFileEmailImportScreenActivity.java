package com.keytalk.nextgen5.view.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  RCCDFileEmailImportScreenActivity
 * Description : This activity should display when user tapped on rccd in the mobile file system or email attachment
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDFileEmailImportScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rccdfile_email_import);
        Button cancelButton = (Button) findViewById(R.id.email_rccd_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                RCCDFileEmailImportScreenActivity.this.finish();
            }
        });

        Button openButton = (Button) findViewById(R.id.email_rccd_open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent inputIntent = getIntent();
                String intentAction = inputIntent.getAction();
                if (intentAction.compareTo(Intent.ACTION_VIEW) == 0) {
                    String intentScheme = inputIntent.getScheme();
                    if (intentScheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                        KeyTalkCommunicationManager.addToLogFile("RCCDFileEmailImportScreenActivity","RCCD from Email Attachment ");
                        Intent emailAttachmentIntent = new Intent(RCCDFileEmailImportScreenActivity.this,RCCDImportScreenActivity.class);
                        emailAttachmentIntent.putExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_EMAIL, inputIntent.getData().toString());
                        RCCDFileEmailImportScreenActivity.this.finish();
                        startActivity(emailAttachmentIntent);
                    } else if (intentScheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                        KeyTalkCommunicationManager.addToLogFile("RCCDFileEmailImportScreenActivity","RCCD from local memory");
                        Intent emailAttachmentIntent = new Intent(RCCDFileEmailImportScreenActivity.this,RCCDImportScreenActivity.class);
                        emailAttachmentIntent.putExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_MEMORY, inputIntent.getData().toString());
                        RCCDFileEmailImportScreenActivity.this.finish();
                        startActivity(emailAttachmentIntent);
                    }
                }
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}