/*
 * Class  :  PasswordScreenActivity
 * Description :
 *
 * Created By Jobin Mathew on 2018
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.AuthenticationCertificateCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.component.MyCountDownTimer;
import com.keytalk.nextgen5.view.component.TimerCallBack;
import com.keytalk.nextgen5.view.util.AppConstants;


import java.util.ArrayList;

/*
 * Class  :  PasswordScreenActivity
 * Description : Password Activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class PasswordScreenActivity extends AppCompatActivity implements
        OnClickListener, AuthenticationCertificateCallBack, TimerCallBack {
    private EditText pwdEditText = null;
    private String userName = "";
    private LinearLayout countDownWidget = null;
    private MyCountDownTimer countdowntimer = null;

    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;
    private Runnable tryAgain;

    private boolean isUserNameRequested;
    private boolean isPasswordRequested;
    private boolean isPinRequested;
    private boolean isResponseRequested;
    private String challenge;
    private String passwordTexts;

    private boolean isShowingAlertDialog = false;
    private int currentAlertDialogID = -1;
    private AlertDialog activityAlertDialog;
    private boolean isShowingDialog = false;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.passwordscreen_header);
        TextView header = (TextView)findViewById(R.id.header_string);
        header.setText(R.string.passwordscreen_header);
        pwdEditText = (EditText) findViewById(R.id.passwordscreen_edittext);
        pwdEditText.setOnEditorActionListener(editorActionListener);

        TextView pwdScreenText = (TextView) findViewById(R.id.passwordscreen_text);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.AUTH_SERVICE_USERS)) {
                String userNames = intent
                        .getStringExtra(AppConstants.AUTH_SERVICE_USERS);
                if (userNames != null && !userNames.isEmpty()
                        && userNames.length() > 0 && !userNames.equals("")) {
                    userNames = userNames.replace("\"", "").replace("[", "")
                            .replace("]", "");
                    String[] userNamesArray = userNames.split(",");
                    if (userNamesArray != null) {
                        userName = userNamesArray[0];
                    }
                }
            }

            if (intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT)) {
                passwordTexts = intent.getStringExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT);
                if (passwordTexts != null && !passwordTexts.isEmpty()
                        && passwordTexts.length() > 0 && !passwordTexts.equals("")) {
                    pwdScreenText.setText(passwordTexts);
                } else {
                    if (userName != null && !userName.isEmpty()) {
                        pwdScreenText.setText(String.format(
                                getString(R.string.passwordscreen_text), userName));
                    }
                }
            } else {
                if (userName != null && !userName.isEmpty()) {
                    pwdScreenText.setText(String.format(
                            getString(R.string.passwordscreen_text), userName));
                }
            }
            //IS_AUTH_REQUIRED_PASSWORD_TEXT

            if (intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME)) {
                isUserNameRequested = intent.getBooleanExtra(
                        AppConstants.IS_AUTH_REQUIRED_USER_NAME, true);
            }
            if (intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD)) {
                isPasswordRequested = intent.getBooleanExtra(
                        AppConstants.IS_AUTH_REQUIRED_PASSWORD, false);
            }
            if (intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PIN)) {
                isPinRequested = intent.getBooleanExtra(
                        AppConstants.IS_AUTH_REQUIRED_PIN, false);
            }
            if (intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE)) {
                isResponseRequested = intent.getBooleanExtra(
                        AppConstants.IS_AUTH_REQUIRED_RESPONSE, false);
            }
            if (intent.hasExtra(AppConstants.AUTH_SERVICE_CHALLENGE)) {
                challenge = intent
                        .getStringExtra(AppConstants.AUTH_SERVICE_CHALLENGE);
            }
        }
        countDownWidget = (LinearLayout) findViewById(R.id.countdowun_background);
        Button submitButton = (Button) findViewById(R.id.passwordSubmitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (activityAlertDialog != null && currentAlertDialogID != -1
                && isShowingAlertDialog) {
            dissmissAlert(activityAlertDialog, currentAlertDialogID);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.passwordSubmitButton:
                hideSoftInputFromWindow();
                String password = pwdEditText.getText().toString();
                if (password == null
                        || password.length() <= 0
                        || password
                        .equals(getString(R.string.passwordscreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_PASSWORD);
                } else {
                    startNextActivity(password);
                }
                break;
            default:
                break;
        }

    }

    public void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pwdEditText.getWindowToken(), 0);
    }

    @Override
    public void reloadPage() {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","Loading browser inititate here");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_SUCESS, true);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void displayError(String errorMessage) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","error message "+errorMessage);
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_ERROR, true);
        doneIntent.putExtra(AppConstants.CERT_REQUEST_ERROR_MSG, errorMessage);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","invalidCredentialsDelay started");
        this.tryAgain = tryAgain;
        countdowntimer = new MyCountDownTimer(seconds * 1000, 1000,
                countDownWidget, this);
        countdowntimer.startCountDown();
    }

    OnEditorActionListener editorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
                hideSoftInputFromWindow();
                String password = pwdEditText.getText().toString();
                if (password == null
                        || password.length() <= 0
                        || password
                        .equals(getString(R.string.passwordscreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_PASSWORD);
                } else {
                    startNextActivity(password);
                }
            }
            return false;
        }
    };

    public void startNextActivity(final String passwordSelected) {
        Intent intent = null;
        if (isPinRequested) {
            // Go to pin number screen
            intent = new Intent(this, PinNumberScreenActivity.class);
        } else if (isResponseRequested) {
            // Go to responseRequested
            intent = new Intent(this, ChallengeResponseScreenActivity.class);

        }
        if (intent != null) {
            intent.putExtra(AppConstants.AUTH_SERVICE_USERS, userName);
            intent.putExtra(AppConstants.AUTH_SERVICE_PASSWORD,
                    passwordSelected);

            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME,
                    isUserNameRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD,
                    isPasswordRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT,passwordTexts);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PIN, isPinRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE,
                    isResponseRequested);
            intent.putExtra(AppConstants.AUTH_SERVICE_CHALLENGE, challenge);
            KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","Loading next user credential activity");
            startActivityForResult(intent,
                    AppConstants.REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY);
        } else {
            // Only UserName and password Required
            showDialog(getString(R.string.validating));
            KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","sending credentials to server");
            boolean isSucess = KeyTalkCommunicationManager.sendUserCredentialsForCertificate(userName,passwordSelected, this);
            if (!isSucess) {
                String request_try_again = getString(R.string.request_try_again);
                displayError(request_try_again);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY
                && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countdowntimer = null;
        tryAgain = null;
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
                        .setText(getString(R.string.password_error_message));
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

    public void showDialog(String message) {
        if (!isFinishing() && isShowingDialog) {
            dismissDialog();
        }
        isShowingDialog = true;
        dialog = ProgressDialog.show(this, "", message, true, false);
    }

    public final void dismissDialog() {
        try {
            if (!isFinishing() && isShowingDialog) {
                if (dialog != null) {
                    dialog.cancel();
                    dialog.dismiss();
                    dialog = null;
                    isShowingDialog = false;
                }
            }
        } catch (Exception e) {
            isShowingDialog = false;
        }
    }

    public void onDetachedFromWindow() {
        try {
            if (dialog != null && isShowingDialog) {
                dialog.cancel();
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void credentialRequest(String serviceUsers,
                                  boolean isUserNameRequested, boolean isPasswordRequested,String passwordText,
                                  boolean isPinRequested, boolean isResponseRequested,
                                  String challenge) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","credentialRequest started");

        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_DELAY_CREDENTIALS,
                true);
       // doneIntent.putExtra(AppConstants.AUTH_SERVICE_USERS, serviceUsers);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME,
                isUserNameRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD,
                isPasswordRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT,passwordText);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PIN, isPinRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE,
                isResponseRequested);
        doneIntent.putExtra(AppConstants.AUTH_SERVICE_CHALLENGE, challenge);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void timerCallBack() {
        // TODO Auto-generated method stub

        if (tryAgain != null) {
            dismissDialog();
            showDialog(getString(R.string.validating));
            boolean isSucess = KeyTalkCommunicationManager
                    .restartAfterDelay(tryAgain);
            if (!isSucess) {
                String request_try_again = getString(R.string.request_try_again);
                displayError(request_try_again);
            }
        }

    }

    @Override
    public void resetCredentials(String userName, String expiredPassword) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","resetCredentials started");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_RESET_CREDENTIALS_REQUEST, true);
        doneIntent.putExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_USER,
                userName);
        doneIntent.putExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_PWD,
                expiredPassword);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void resetCredentialsOption(int days) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","resetCredentialsOption started ");
        layoutInflater = LayoutInflater.from(this);
        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        dialogIcon = (ImageView) dialogView.findViewById(R.id.dialog_image);
        dialogTxtMessage = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogIcon.setImageResource(R.drawable.icon_info_transparent);
        if (days == 0)
            dialogTxtMessage
                    .setText(getString(R.string.password_expire_option));
        else {
            //dialogTxtMessage.setText(getString(R.string.password_expire_option_more,days));
            String msg = String.valueOf(R.string.password_expire_string + days + R.string.days_reset_password);
            dialogTxtMessage.setText(msg);
        }
        dialogTxtMessage.setTextSize(18);
        activityAlertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setIcon(0)
                .setCancelable(false)
                .setPositiveButton(R.string.reset_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                                showDialog(getString(R.string.resetting));
                                boolean isSucess = KeyTalkCommunicationManager
                                        .resetPasswordNow();
                                if (!isSucess) {
                                    String request_try_again = getString(R.string.request_try_again);
                                    displayError(request_try_again);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                                showDialog(getString(R.string.validating));
                                boolean isSucess = KeyTalkCommunicationManager
                                        .resetPasswordLater();
                                if (!isSucess) {
                                    String request_try_again = getString(R.string.request_try_again);
                                    displayError(request_try_again);
                                }
                            }
                        }).show();
        activityAlertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void requestChallange(boolean isTokenRequest,String[] challangeData,boolean isNewChallengeRequest, ArrayList<String[]> newChallengeData) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PasswordScreenActivity","requestChallange started>>>>>>>>>>>>>>> "+isTokenRequest+","+challangeData);
        Intent doneIntent = new Intent();
        if(isTokenRequest) {
            doneIntent.putExtra(AppConstants.IS_CHALLENGE_CREDENTIALS_REQUEST, true);
            doneIntent.putExtra(AppConstants.IS_CHALLENGE_CREDENTIALS_DATA,challangeData);
        } else if(isNewChallengeRequest) {
            doneIntent.putExtra(AppConstants.IS_NEW_CHALLENGE_CREDENTIALS_REQUEST, true);
            doneIntent.putExtra(AppConstants.IS_NEW_CHALLENGE_CREDENTIALS_DATA,newChallengeData.get(0));
            doneIntent.putExtra(AppConstants.IS_NEW_RESPONSE_CREDENTIALS_DATA,newChallengeData.get(1));
        }
        setResult(RESULT_OK, doneIntent);
        finish();
    }

}
