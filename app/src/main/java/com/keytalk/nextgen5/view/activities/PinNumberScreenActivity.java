package com.keytalk.nextgen5.view.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.AuthenticationCertificateCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.component.MyCountDownTimer;
import com.keytalk.nextgen5.view.component.TimerCallBack;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  PinNumberScreenActivity
 * Description : Pin number activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class PinNumberScreenActivity extends AppCompatActivity  implements OnClickListener ,AuthenticationCertificateCallBack,TimerCallBack {
    private EditText pinEditText;
    private String userName = "";
    private String passWord = "";

    private LinearLayout countDownWidget = null;
    private static MyCountDownTimer countdowntimer = null;

    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;
    private  Runnable tryAgain;

    private boolean isUserNameRequested;
    private boolean isPasswordRequested;
    private boolean isPinRequested;
    private boolean isResponseRequested;
    private String challenge;

    private String passwordTexts;


    private  boolean isShowingAlertDialog=false;
    private  int currentAlertDialogID=-1;
    private  AlertDialog activityAlertDialog;
    private  boolean isShowingDialog = false;
    private  ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_number_screen);
        pinEditText = (EditText) findViewById(R.id.authenticationpin_edittext);
        pinEditText.setOnEditorActionListener(editorActionListener);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(AppConstants.AUTH_SERVICE_USERS)) {
                String userNames = intent.getStringExtra(AppConstants.AUTH_SERVICE_USERS);
                if(userNames != null && !userNames.isEmpty() && userNames.length() > 0 && !userNames.equals("")) {
                    userNames=userNames.replace("\"", "").replace("[", "").replace("]", "");
                    String[] userNamesArray=userNames.split(",");
                    if (userNamesArray != null) {
                        userName = userNamesArray[0];
                    }
                }

            }
            if(intent.hasExtra(AppConstants.AUTH_SERVICE_PASSWORD)) {
                passWord = intent.getStringExtra(AppConstants.AUTH_SERVICE_PASSWORD);
            }
            if(intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME)) {
                isUserNameRequested = intent.getBooleanExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME, true);
            }
            if(intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD)) {
                isPasswordRequested = intent.getBooleanExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD, false);
            }
            if(intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT)) {
                passwordTexts = intent.getStringExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT);
            }
            if(intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_PIN)) {
                isPinRequested = intent.getBooleanExtra(AppConstants.IS_AUTH_REQUIRED_PIN, false);
            }
            if(intent.hasExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE)) {
                isResponseRequested = intent.getBooleanExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE, false);
            }
            if(intent.hasExtra(AppConstants.AUTH_SERVICE_CHALLENGE)) {
                challenge = intent.getStringExtra(AppConstants.AUTH_SERVICE_CHALLENGE);
            }
        }

        TextView pinCustomText = (TextView) findViewById(R.id.pinCustomText);
        if(userName != null && !userName.isEmpty()) {
            pinCustomText.setText(String.format(getString(R.string.authenticationpin_text), userName));
        }
        countDownWidget = (LinearLayout) findViewById(R.id.countdowun_background);
        Button submitButton = (Button) findViewById(R.id.pinOKButton);
        submitButton.setOnClickListener(this);
    }



    OnEditorActionListener editorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            hideSoftInputFromWindow();
            String pin = pinEditText.getText().toString();
            if (pin == null || pin.length() <= 0 	|| pin.equals(getString(R.string.passwordscreen_default_text))) {
                showDialog(AppConstants.DIALOG_INVALID_PINNUMBER);
            } else {
                startNextActivity(pin);
            }
            return false;
        }
    };

    public void hideSoftInputFromWindow()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pinEditText.getWindowToken(), 0);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onDestroy()
    {
        if(activityAlertDialog!=null && currentAlertDialogID!=-1 && isShowingAlertDialog)
        {
            dissmissAlert(activityAlertDialog, currentAlertDialogID);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }



    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.pinOKButton:
                hideSoftInputFromWindow();
                String pin = pinEditText.getText().toString();
                if (pin == null || pin.length() <= 0 	|| pin.equals(getString(R.string.passwordscreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_PINNUMBER);
                } else {
                    startNextActivity(pin);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void reloadPage() {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","reloadPage started");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_SUCESS, true);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void displayError(String errorMessage) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","displayError started :"+errorMessage);
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_ERROR, true);
        doneIntent.putExtra(AppConstants.CERT_REQUEST_ERROR_MSG, errorMessage);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","invalidCredentialsDelay started :");
        this.tryAgain = tryAgain;
        countdowntimer = new MyCountDownTimer(seconds * 1000, 1000, countDownWidget,this);
        countdowntimer.startCountDown();
    }

    public void startNextActivity(final String pinNumber) {
        Intent intent = null;
        if(isResponseRequested) {
            //Go to responseRequested
            intent = new Intent(this, ChallengeResponseScreenActivity.class);

        }
        if(intent != null) {
            intent.putExtra(AppConstants.AUTH_SERVICE_USERS, userName);
            intent.putExtra(AppConstants.AUTH_SERVICE_PASSWORD, passWord);
            intent.putExtra(AppConstants.AUTH_SERVICE_PIN, pinNumber);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME, isUserNameRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD, isPasswordRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT,passwordTexts);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PIN, isPinRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE, isResponseRequested);
            intent.putExtra(AppConstants.AUTH_SERVICE_CHALLENGE, challenge);
            KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","moving to next credential activity");
            startActivityForResult(intent,AppConstants.REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY);
        } else {
            //Only UserName password , pinNumber Required
            showDialog("Validating...");
            KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","sending credentials to server");
            boolean isSucess = KeyTalkCommunicationManager.sendUserCredentialsForCertificate(userName,passWord, pinNumber, this);
            if(!isSucess) {
                displayError("We are not able to process your request. Please try again.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();

        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        countdowntimer=null;
        tryAgain=null;
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
        currentAlertDialogID=id;
        isShowingAlertDialog=true;
        switch (id) {
            case AppConstants.DIALOG_INVALID_PINNUMBER:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.authenticationpin_error_message));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.passwordscreen_error_message_button,
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


    public final void dismissDialog()
    {
        try
        {
            if (!isFinishing() && isShowingDialog)
            {
                if(dialog!=null)
                {
                    dialog.cancel();
                    dialog.dismiss();
                    dialog=null;
                    isShowingDialog = false;
                }
            }
        }catch(Exception e)
        {
            isShowingDialog = false;
        }
    }

    public void onDetachedFromWindow()
    {
        try
        {
            if (dialog != null && isShowingDialog)
            {
                dialog.cancel();
                dialog.dismiss();
                dialog=null;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }


    @Override
    public void credentialRequest(String serviceUsers,
                                  boolean isUserNameRequested, boolean isPasswordRequested, String passwordText,
                                  boolean isPinRequested, boolean isResponseRequested, String challenge) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","credentialRequest started");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_DELAY_CREDENTIALS, true);
        doneIntent.putExtra(AppConstants.AUTH_SERVICE_USERS, serviceUsers);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME, isUserNameRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD, isPasswordRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT,passwordText);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_PIN, isPinRequested);
        doneIntent.putExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE, isResponseRequested);
        doneIntent.putExtra(AppConstants.AUTH_SERVICE_CHALLENGE, challenge);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void timerCallBack() {
        // TODO Auto-generated method stub
        if(tryAgain!=null)
        {
            dismissDialog();
            isShowingDialog=false;
            dialog=null;
            //showDialog("Validating...");
            boolean isSucess = KeyTalkCommunicationManager.restartAfterDelay(tryAgain);
            if(!isSucess) {
                displayError("We are not able to process your request. Please try again.");
            }
        }
    }


    @Override
    public void resetCredentials(String userName, String expiredPassword) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","resetCredentials started");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_RESET_CREDENTIALS_REQUEST, true);
        doneIntent.putExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_USER, userName);
        doneIntent.putExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_PWD, expiredPassword);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void resetCredentialsOption(int days) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","resetCredentialsOption started");
        layoutInflater = LayoutInflater.from(this);
        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        dialogIcon = (ImageView) dialogView.findViewById(R.id.dialog_image);
        dialogTxtMessage = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogIcon.setImageResource(R.drawable.icon_info_transparent);
        if(days == 0)
            dialogTxtMessage.setText(getString(R.string.password_expire_option));
        else {
            //dialogTxtMessage.setText(getString(R.string.password_expire_option_more,days));
            String msg = "Your password will expire within "+days+" days. Do you want to reset the password";
            dialogTxtMessage.setText(msg);
        }
        dialogTxtMessage.setTextSize(18);
        activityAlertDialog = new AlertDialog.Builder(this) .setView(dialogView)
                .setIcon(0)
                .setCancelable(false)
                .setPositiveButton(R.string.reset_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int whichButton) {
                                dialog.cancel();
                                showDialog("Resetting...");
                                boolean isSucess = KeyTalkCommunicationManager.resetPasswordNow();
                                if(!isSucess) {
                                    displayError("We are not able to process your request. Please try again.");
                                }
                            }
                        }).setNegativeButton(R.string.cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int whichButton) {
                                dialog.cancel();
                                showDialog("Validating...");
                                boolean isSucess = KeyTalkCommunicationManager.resetPasswordLater();
                                if(!isSucess) {
                                    displayError("We are not able to process your request. Please try again.");
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
        KeyTalkCommunicationManager.addToLogFile("PinNumberScreenActivity","requestChallange started");
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

