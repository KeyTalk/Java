package com.keytalk.nextgen5.view.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  ChangeServerURLActivity
 * Description : Change server url activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ChangeServerURLActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText urlNameEditText = null;
    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;
    private boolean isShowingAlertDialog = false;
    private int currentAlertDialogID = -1;
    private AlertDialog activityAlertDialog;

    private String serverURL = null;
    private String rccdFilePath = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_server_url);
        urlNameEditText = (EditText) findViewById(R.id.urlscreen_edittext);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.IS_NEW_SERVER_URL_ADDED)) {
                if(intent.getBooleanExtra(AppConstants.IS_NEW_SERVER_URL_ADDED, false)) {
                    if(intent.hasExtra(AppConstants.SERVER_URL_FROM_RCCD)) {
                        serverURL = intent.getStringExtra(AppConstants.SERVER_URL_FROM_RCCD);
                        if(serverURL != null && !serverURL.isEmpty()) {
                            urlNameEditText.setText(serverURL);
                        }
                    }
                    if(intent.hasExtra(AppConstants.SERVER_URL_RCCD_FILE_NAME)) {
                        rccdFilePath = intent.getStringExtra(AppConstants.SERVER_URL_RCCD_FILE_NAME);
                    }

                }
            }
        }
        urlNameEditText.setOnEditorActionListener(editorActionListener);
        Button submitButton = (Button) findViewById(R.id.urlSubmitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activityAlertDialog != null && currentAlertDialogID != -1
                && isShowingAlertDialog) {
            dissmissAlert(activityAlertDialog, currentAlertDialogID);
        }
    }


    public void dissmissAlert(DialogInterface dialog, int id) {
        try {
            if (id != -1) {
                removeDialog(id);
            }
            if (dialog != null) {
                dialog.cancel();
            }
            if (activityAlertDialog != null) {
                activityAlertDialog.cancel();
                activityAlertDialog.dismiss();
            }
            currentAlertDialogID = -1;
            isShowingAlertDialog = false;
            activityAlertDialog = null;
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.urlSubmitButton:
                hideSoftInputFromWindow();
                String urlName = urlNameEditText.getText().toString();
                if (urlName == null
                        || urlName.length() <= 0
                        || urlName.equals(getString(R.string.urlscreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_PASSWORD);
                } else {
                    String temUrlName = (urlName.replace("http://", "")).replace("https://", "");
                    String temServerURL =  (serverURL.replace("http://", "")).replace("https://", "");
                    if(temUrlName.equals(temServerURL)) {
                        showDialog(AppConstants.DIALOG_SAME_URL);
                    } else {
                        startNextActivity(urlName.trim());
                    }
                }
                break;
            default:
                break;
        }

    }

    public void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(urlNameEditText.getWindowToken(), 0);
    }


    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
                hideSoftInputFromWindow();
                String urlName = urlNameEditText.getText().toString();
                if (urlName == null
                        || urlName.length() <= 0
                        || urlName.equals(getString(R.string.urlscreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_PASSWORD);
                } else {
                    String temUrlName = (urlName.replace("http://", "")).replace("https://", "");
                    String temServerURL =  (serverURL.replace("http://", "")).replace("https://", "");
                    if(temUrlName.equals(temServerURL)) {
                        showDialog(AppConstants.DIALOG_SAME_URL);
                    } else {
                        startNextActivity(urlName.trim());
                    }
                }
            }
            return false;
        }
    };

    private void startNextActivity(final String urlString) {
        boolean isUpdated = false;
        if(rccdFilePath != null && !rccdFilePath.isEmpty()) {
            isUpdated = KeyTalkCommunicationManager.updateServerURL(ChangeServerURLActivity.this,rccdFilePath, urlString);
        }
        KeyTalkCommunicationManager.addToLogFile("ChangeServerURLScreen","User added new server URL : "+urlString);
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_NEW_SERVER_URL_ADDED, isUpdated);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public Dialog onCreateDialog(final int id) {
        layoutInflater = LayoutInflater.from(this);
        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        dialogIcon = (ImageView) dialogView.findViewById(R.id.dialog_image);
        dialogTxtMessage = (TextView) dialogView.findViewById(R.id.dialog_text);
        if (isFinishing()) {
            return null;
        }
        AlertDialog alertDialog = null;
        currentAlertDialogID = id;
        isShowingAlertDialog = true;
        switch (id) {
            case AppConstants.DIALOG_INVALID_PASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage
                        .setText(getString(R.string.url_error_message));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(
                                R.string.passwordscreen_error_message_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_SAME_URL:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage
                        .setText(getString(R.string.same_url_message));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(
                                R.string.passwordscreen_error_message_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;

        }
        return super.onCreateDialog(id);
    }
}
