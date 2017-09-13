package com.keytalk.nextgen5.view.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.AuthenticationCertificateCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.component.MyCountDownTimer;
import com.keytalk.nextgen5.view.component.TimerCallBack;
import com.keytalk.nextgen5.view.component.UserNameListAdaptor;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/*
 * Class  :  UserNameInputScreen
 * Description : User Name Activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class UserNameInputScreen extends AppCompatActivity implements View.OnClickListener,AuthenticationCertificateCallBack,TimerCallBack {

    private EditText userNameEditText = null;
    //private CustomButton userCustomButton;
    private ListView userNameList;
    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;
    //	private CharSequence userNamesArray[]=null;
    private String userNamesArray[]=null;

    private MyCountDownTimer countdowntimer = null;
    private LinearLayout countDownWidget = null;

    private boolean isUserNameRequested;
    private boolean isPasswordRequested;
    private boolean isPinRequested;
    private boolean isResponseRequested;
    private String challenge;
    private String passwordTexts;
    private Runnable tryAgain;

    private  boolean isShowingAlertDialog=false;
    private  int currentAlertDialogID=-1;
    private AlertDialog activityAlertDialog;
    private  boolean isShowingDialog = false;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_input_screen);
        userNameEditText = (EditText) findViewById(R.id.usernamescreen_edittext);
        userNameEditText.setOnEditorActionListener(editorActionListener);
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(AppConstants.AUTH_SERVICE_USERS)) {
                String userNames = intent.getStringExtra(AppConstants.AUTH_SERVICE_USERS);
                if(userNames != null && !userNames.isEmpty() && userNames.length() > 0 && !userNames.equals("")) {
                    userNames=userNames.replace("\"", "").replace("[", "").replace("]", "");
                    userNamesArray=userNames.split(",");
                    if (userNamesArray != null && userNamesArray.length > 0 && !userNamesArray[0].toString().trim().isEmpty()) {
                        userNameList = (ListView) findViewById(R.id.userNameList);
                        UserNameListAdaptor usernameAdaptor=new UserNameListAdaptor(this,android.R.layout.simple_list_item_1,userNamesArray);
                        userNameList.setAdapter(usernameAdaptor);
                        userNameList.setOnItemClickListener(onItemClickListner);
                    }else {
                        userNameList = (ListView) findViewById(R.id.userNameList);

                        userNameList.setVisibility(View.GONE);
                        TextView userNameScreenText=(TextView)findViewById(R.id.usernamescreen_text);
                        userNameScreenText.setVisibility(View.GONE);
                    }
                } else {
                    userNameList = (ListView) findViewById(R.id.userNameList);
                    userNameList.setVisibility(View.GONE);
                    TextView userNameScreenText=(TextView)findViewById(R.id.usernamescreen_text);
                    userNameScreenText.setVisibility(View.GONE);
                }

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

        } else {
            userNameList = (ListView) findViewById(R.id.userNameList);
            userNameList.setVisibility(View.GONE);
            TextView userNameScreenText=(TextView)findViewById(R.id.usernamescreen_text);
            userNameScreenText.setVisibility(View.GONE);
        }

        countDownWidget = (LinearLayout) findViewById(R.id.countdowun_background);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    AdapterView.OnItemClickListener onItemClickListner=new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
        {
            String username =((UserNameListAdaptor)userNameList.getAdapter()).getItem(position);
            startNextActivity(username);
        }
    };

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
            case R.id.username_listitem:
                hideSoftInputFromWindow();
                if (userNamesArray != null) {
                    if (userNamesArray.length > 1) {
                        showDialog(AppConstants.DIALOG_SHOWMULTIPLE_USERNAME);
                    } else {
                        startNextActivity(userNamesArray[0].toString().trim());
                    }
                }
                break;
            default:
                break;
        }
    }

    public void hideSoftInputFromWindow()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userNameEditText.getWindowToken(), 0);
    }



    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                String userName = userNameEditText.getText().toString();
                hideSoftInputFromWindow();
                if (userName == null || userName.length() <= 0 	|| userName.equals(getString(R.string.usernamescreen_default_text))) {
                    showDialog(AppConstants.DIALOG_INVALID_USERNAME);
                } else {
                    if (userNamesArray != null && userNamesArray.length > 0 ) {
                        boolean isExist = false;
                        for(int i = 0; i<userNamesArray.length; i++) {
                            if(userName.trim().equals(userNamesArray[i].toString().trim())) {
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist) {
                            KeyTalkCommunicationManager.addUserNameTemp(userName);
                        }
                    }
                    startNextActivity(userName);
                }
            }
            return false;
        }
    };

    public void startNextActivity(final String usernameSelected) {
        Intent intent = null;
        if(isPasswordRequested) {
            //Go to password screen
            intent = new Intent(this, PasswordScreenActivity.class);
        } else if(isPinRequested) {
            //Go to pin number screen
            intent = new Intent(this, PinNumberScreenActivity.class);
        } else if(isResponseRequested) {
            //Go to responseRequested
            intent = new Intent(this, ChallengeResponseScreenActivity.class);

        }
        if(intent != null) {
            intent.putExtra(AppConstants.AUTH_SERVICE_USERS, usernameSelected.trim());
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_USER_NAME, isUserNameRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD, isPasswordRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PASSWORD_TEXT, passwordTexts);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_PIN, isPinRequested);
            intent.putExtra(AppConstants.IS_AUTH_REQUIRED_RESPONSE, isResponseRequested);
            intent.putExtra(AppConstants.AUTH_SERVICE_CHALLENGE, challenge);
            KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","Loading next user credential activity");
            startActivityForResult(intent,AppConstants.REQUEST_CODE_CERT_REQUEST_CREDENTIAL_ACTIVITY);
        } else {
            KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","sending user name to server");
            //Only UserName Required
            showDialog("Validating...");
            //boolean isSucess = KeyTalkCommunicationManager.getCertificateWithUserName(usernameSelected.trim(), this);
            boolean isSucess = KeyTalkCommunicationManager.sendUserCredentialsForCertificate(usernameSelected.trim(), this);
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

    @Override
    protected void onPause() {
        super.onPause();
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
            case AppConstants.DIALOG_INVALID_USERNAME:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.usernamescreen_error_message));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.usernamescreen_error_message_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog,id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_SHOWMULTIPLE_USERNAME:
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setIcon(0)
                        .setItems(userNamesArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                startNextActivity(userNamesArray[which].toString().trim());
                                dissmissAlert(dialog,id);
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
    public void reloadPage() {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","Loading browser inititate here");
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_SUCESS, true);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void displayError(String errorMessage) {
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","error message "+errorMessage);
        // TODO Auto-generated method stub
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_ERROR, true);
        doneIntent.putExtra(AppConstants.CERT_REQUEST_ERROR_MSG, errorMessage);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void invalidCredentialsDelay(int seconds, Runnable tryAgain) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","invalidCredentialsDelay started");
        this.tryAgain = tryAgain;
        countdowntimer = new MyCountDownTimer(seconds * 1000, 1000, countDownWidget,this);
        countdowntimer.startCountDown();
    }

    @Override
    public void credentialRequest(String serviceUsers,
                                  boolean isUserNameRequested, boolean isPasswordRequested, String passwordText,
                                  boolean isPinRequested, boolean isResponseRequested, String challenge) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","credentialRequest started");
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
        showDialog("Validating...");
        boolean isSucess = KeyTalkCommunicationManager.restartAfterDelay(tryAgain);
        if(!isSucess) {
            displayError("We are not able to process your request. Please try again.");
        }

    }

    @Override
    public void resetCredentials(String userName, String expiredPassword) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","resetCredentials started");
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
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","resetCredentialsOption started ");
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
        KeyTalkCommunicationManager.addToLogFile("UserNameInputScreen","requestChallange started ");
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


