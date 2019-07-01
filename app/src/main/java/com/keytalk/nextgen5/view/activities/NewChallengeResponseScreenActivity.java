/*
 * Class  :  NewChallengeResponseScreenActivity
 * Description :
 *
 * Created By Jobin Mathew on 2018
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.AuthenticationCertificateCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.component.MyCountDownTimer;
import com.keytalk.nextgen5.view.component.TimerCallBack;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  NewChallengeResponseScreenActivity
 * Description : Challenge Response Activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class NewChallengeResponseScreenActivity extends AppCompatActivity implements OnClickListener ,AuthenticationCertificateCallBack,TimerCallBack {
    private LinearLayout countDownWidget = null;
    private MyCountDownTimer countdowntimer = null;
    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;
    private Runnable tryAgain;
    private  boolean isShowingAlertDialog=false;
    private  int currentAlertDialogID=-1;
    private  AlertDialog activityAlertDialog;
    private  boolean isShowingDialog = false;
    private  ProgressDialog dialog;

    private EditText umtsAutnEditText=null;
    private TextView umtsAutnText = null;
    private EditText umtsRandomEditText=null;
    private TextView umtsRandomText = null;
    private EditText resEditText=null;
    private TextView resText = null;
    private EditText ikEditText=null;
    private TextView ikText = null;
    private EditText ckEditText=null;
    private TextView ckText = null;

    private String[] challengeArray = null;
    private String[] responseArray = null;



    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_challenge_response_screen);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.responsescreen_header);
        TextView header = (TextView)findViewById(R.id.header_string);
        header.setText(R.string.responsescreen_header);
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(AppConstants.IS_NEW_CHALLENGE_CREDENTIALS_DATA) &&
                    intent.getStringArrayExtra(AppConstants.IS_NEW_CHALLENGE_CREDENTIALS_DATA) != null) {
                challengeArray = intent.getStringArrayExtra(AppConstants.IS_NEW_CHALLENGE_CREDENTIALS_DATA);
            }
            if(intent.hasExtra(AppConstants.IS_NEW_RESPONSE_CREDENTIALS_DATA) &&
                    intent.getStringArrayExtra(AppConstants.IS_NEW_RESPONSE_CREDENTIALS_DATA) != null) {
                responseArray = intent.getStringArrayExtra(AppConstants.IS_NEW_RESPONSE_CREDENTIALS_DATA);
            }
        }

        umtsAutnEditText = (EditText) findViewById(R.id.umtsAutn_edittext);
        umtsRandomEditText = (EditText) findViewById(R.id.umtsRandom_edittext);
        resEditText = (EditText) findViewById(R.id.res_edittext);
        ikEditText = (EditText) findViewById(R.id.ik_edittext);
        ckEditText = (EditText) findViewById(R.id.ck_edittext);
        umtsAutnText = (TextView) findViewById(R.id.umtsAutnText);
        umtsRandomText = (TextView) findViewById(R.id.umtsRandomText);
        resText = (TextView) findViewById(R.id.resText);
        ikText = (TextView) findViewById(R.id.ikText);
        ckText = (TextView) findViewById(R.id.ckText);


        if((challengeArray == null || challengeArray.length <= 0) && (responseArray == null || responseArray.length <= 0) ) {
            String request_try_again = getString(R.string.request_try_again);
            displayError(request_try_again);
        } else {
            if (challengeArray == null || challengeArray.length <= 0) {
                umtsAutnEditText.setVisibility(View.GONE);
                umtsRandomEditText.setVisibility(View.GONE);
                umtsAutnText.setVisibility(View.GONE);
                umtsRandomText.setVisibility(View.GONE);
            } else {
                String[] tempData = challengeArray[0].split("#");
                umtsAutnText.setText(tempData[0]);
                if (tempData.length == 2) {
                    umtsAutnEditText.setText(tempData[1]);
                } else {
                    umtsAutnEditText.setHint(tempData[0]);
                }
                if (challengeArray.length > 1) {
                    tempData = challengeArray[1].split("#");
                    umtsRandomText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        umtsRandomEditText.setText(tempData[1]);
                    } else {
                        umtsRandomEditText.setHint(tempData[0]);
                    }
                } else {
                    umtsRandomText.setVisibility(View.GONE);
                    umtsRandomEditText.setVisibility(View.GONE);
                }
            }
            if (responseArray == null || responseArray.length <= 0) {
                resEditText.setVisibility(View.GONE);
                ikEditText.setVisibility(View.GONE);
                ckEditText.setVisibility(View.GONE);
                resText.setVisibility(View.GONE);
                ikText.setVisibility(View.GONE);
                ckText.setVisibility(View.GONE);
            } else {
                resEditText.setVisibility(View.GONE);
                ikEditText.setVisibility(View.GONE);
                ckEditText.setVisibility(View.GONE);
                resText.setVisibility(View.GONE);
                ikText.setVisibility(View.GONE);
                ckText.setVisibility(View.GONE);
                if (responseArray.length == 1) {
                    resEditText.setVisibility(View.VISIBLE);
                    resText.setVisibility(View.VISIBLE);
                    String[] tempData = responseArray[0].split("#");
                    resText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        resEditText.setText(tempData[1]);
                    } else {
                        resEditText.setHint(tempData[0]);
                    }
                } else if (responseArray.length == 2) {
                    resEditText.setVisibility(View.VISIBLE);
                    resText.setVisibility(View.VISIBLE);
                    ikEditText.setVisibility(View.VISIBLE);
                    ikText.setVisibility(View.VISIBLE);
                    String[] tempData = responseArray[0].split("#");
                    resText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        resEditText.setText(tempData[1]);
                    } else {
                        resEditText.setHint(tempData[0]);
                    }

                    tempData = responseArray[1].split("#");
                    ikText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        ikEditText.setText(tempData[1]);
                    } else {
                        ikEditText.setHint(tempData[0]);
                    }

                } else if (responseArray.length <= 3) {
                    resEditText.setVisibility(View.VISIBLE);
                    resText.setVisibility(View.VISIBLE);
                    ikEditText.setVisibility(View.VISIBLE);
                    ikText.setVisibility(View.VISIBLE);
                    ckEditText.setVisibility(View.VISIBLE);
                    ckText.setVisibility(View.VISIBLE);
                    String[] tempData = responseArray[0].split("#");
                    resText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        resEditText.setText(tempData[1]);
                    } else {
                        resEditText.setHint(tempData[0]);
                    }
                    tempData = responseArray[1].split("#");
                    ikText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        ikEditText.setText(tempData[1]);
                    } else {
                        ikEditText.setHint(tempData[0]);
                    }
                    tempData = responseArray[2].split("#");
                    ckText.setText(tempData[0]);
                    if (tempData.length == 2) {
                        ckEditText.setText(tempData[1]);
                    } else {
                        ckEditText.setHint(tempData[0]);
                    }
                }
            }
        }

        countDownWidget = (LinearLayout) findViewById(R.id.countdowun_background);
        Button submitButton = (Button) findViewById(R.id.challengeOKButton);
        submitButton.setOnClickListener(this);
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
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.challengeOKButton:
                if(responseArray.length <= 0) {
                    String request_try_again = getString(R.string.request_try_again);
                    displayError(request_try_again);
                } else {
                    String res=null, ik = null, ck = null;
                    if(responseArray.length == 1 ) {
                        res = resEditText.getText().toString();
                        if (res == null || res.length() <= 0 	|| res.equals(getString(R.string.responsescreen_default_text))) {
                            showDialog(AppConstants.DIALOG_INVALID_CHALLENGE);
                        } else {

                            startNextActivity(responseArray[0].trim()+"#"+res.trim());
                        }
                    } else if(responseArray.length == 2 ) {
                        res = resEditText.getText().toString();
                        ik = ikEditText.getText().toString();
                        if (res == null || res.length() <= 0 	|| res.equals(getString(R.string.responsescreen_default_text))
                                || ik == null || ik.length() <= 0 	|| ik.equals(getString(R.string.responsescreen_default_text))) {
                            showDialog(AppConstants.DIALOG_INVALID_CHALLENGE);
                        } else {
                            //res,ik
                            startNextActivity(responseArray[0].trim()+"#"+res.trim()+","+responseArray[1].trim()+"#"+ik.trim());
                        }
                    } else if(responseArray.length == 3 ) {
                        res = resEditText.getText().toString();
                        ik = ikEditText.getText().toString();
                        ck = ckEditText.getText().toString();
                        if (res == null || res.length() <= 0 	|| res.equals(getString(R.string.responsescreen_default_text))
                                || ik == null || ik.length() <= 0 	|| ik.equals(getString(R.string.responsescreen_default_text))
                                || ck == null || ck.length() <= 0 	|| ck.equals(getString(R.string.responsescreen_default_text))) {
                            showDialog(AppConstants.DIALOG_INVALID_CHALLENGE);
                        } else {
                            //res,ik,ck
                            startNextActivity(responseArray[0].trim()+"#"+res.trim()+","+responseArray[1].trim()+"#"+ik.trim()+","+responseArray[2].trim()+"#"+ck.trim());
                        }
                    }
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
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","reloadPage started ");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_SUCESS, true);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void displayError(String errorMessage) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","displayError started ");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_ERROR, true);
        doneIntent.putExtra(AppConstants.CERT_REQUEST_ERROR_MSG, errorMessage);
        setResult(RESULT_OK, doneIntent);
        finish();
    }


    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","invalidCredentialsDelay started ");
        this.tryAgain = tryAgain;
        countdowntimer = new MyCountDownTimer(seconds * 1000, 1000, countDownWidget,this);
        countdowntimer.startCountDown();
    }

    public void startNextActivity(final String response) {
        showDialog(getString(R.string.validating));
        boolean isSucess = KeyTalkCommunicationManager.getCertificateWithNewChallange(response.trim(),this);
        if(!isSucess) {
            String request_try_again = getString(R.string.request_try_again);
            displayError(request_try_again);
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
            case AppConstants.DIALOG_INVALID_CHALLENGE:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.passwordscreen_error_message));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.passwordscreen_error_message_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog,id);
                                    }
                                })
                        .setNegativeButton(R.string.report_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        reportWithEmail();
                                    }
                                })
                        .create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;

        }
        return super.onCreateDialog(id);
    }

    public void reportWithEmail()
    {
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","reportWithEmail started ");
        boolean isSucess = KeyTalkCommunicationManager.isLogFileAvailable(this);
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.emailscreen_email_address)});
        email.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.emailscreen_subject));
        email.putExtra(Intent.EXTRA_TEXT,getString(R.string.emailscreen_message));
        email.setType("message/rfc822");
        if(isSucess) {
            Uri uri = KeyTalkCommunicationManager.getLogDetailsAsUri(this);
            email.putExtra(Intent.EXTRA_STREAM,uri);
        }
        startActivity(Intent.createChooser(email, "Choose an Email client :"));	}

    public void dissmissAlert(DialogInterface dialog,int id)
    {
        try
        {
            if(id!=-1)
            {
                removeDialog(id);
            }
            if(dialog!=null)
            {
                dialog.cancel();
            }
            if(activityAlertDialog!=null)
            {
                activityAlertDialog.cancel();
                activityAlertDialog.dismiss();
            }
            currentAlertDialogID=-1;
            isShowingAlertDialog=false;
            activityAlertDialog=null;
            dialog=null;
        }catch(Exception e)
        {
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
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","credentialRequest started ");
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
            showDialog(getString(R.string.validating));
            boolean isSucess = KeyTalkCommunicationManager.restartAfterDelay(tryAgain);
            if(!isSucess) {
                String request_try_again = getString(R.string.request_try_again);
                displayError(request_try_again);
            }
        }
    }




    @Override
    public void resetCredentials(String userName, String expiredPassword) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","resetCredentials started ");
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
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","resetCredentialsOption started ");
        layoutInflater = LayoutInflater.from(this);
        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        dialogIcon = (ImageView) dialogView.findViewById(R.id.dialog_image);
        dialogTxtMessage = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogIcon.setImageResource(R.drawable.icon_info_transparent);
        if(days == 0)
            dialogTxtMessage.setText(getString(R.string.password_expire_option));
        else {
            //dialogTxtMessage.setText(getString(R.string.password_expire_option_more,days));
            String msg = String.valueOf(R.string.password_expire_string + days + R.string.days_reset_password);
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
                                showDialog(getString(R.string.resetting));
                                boolean isSucess = KeyTalkCommunicationManager.resetPasswordNow();
                                if(!isSucess) {
                                    String request_try_again = getString(R.string.request_try_again);
                                    displayError(request_try_again);
                                }
                            }
                        }).setNegativeButton(R.string.cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int whichButton) {
                                dialog.cancel();
                                showDialog(getString(R.string.validating));
                                boolean isSucess = KeyTalkCommunicationManager.resetPasswordLater();
                                if(!isSucess) {
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
        KeyTalkCommunicationManager.addToLogFile("NewChallengeResponseScreenActivity","requestChallange started ");
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
