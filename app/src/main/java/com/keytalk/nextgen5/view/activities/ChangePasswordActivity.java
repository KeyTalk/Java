package com.keytalk.nextgen5.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.ResetPasswordCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.view.component.MyCountDownTimer;
import com.keytalk.nextgen5.view.component.TimerCallBack;
import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  ChangePasswordActivity
 * Description : Change password activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ChangePasswordActivity extends AppCompatActivity implements OnClickListener,ResetPasswordCallBack,TimerCallBack {
    private EditText oldPwdEditText = null;
    private EditText newPwdEditText = null;
    private EditText retypePwdEditText = null;

    private LayoutInflater layoutInflater;
    private View dialogView;
    private ImageView dialogIcon;
    private TextView dialogTxtMessage;

    private String userName = null;

    private LinearLayout countDownWidget = null;
    private MyCountDownTimer countdowntimer = null;

    private boolean isShowingAlertDialog = false;
    private int currentAlertDialogID = -1;
    private AlertDialog activityAlertDialog;
    private boolean isShowingDialog = false;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPwdEditText = (EditText) findViewById(R.id.oldpassword_edittext);
        newPwdEditText = (EditText) findViewById(R.id.newpassword_edittext);
        retypePwdEditText = (EditText) findViewById(R.id.retypepassword_edittext);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_USER)) {
                String userNames = intent
                        .getStringExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_USER);
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
            if (intent.hasExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_PWD)) {
                String password = intent
                        .getStringExtra(AppConstants.IS_RESET_REQUEST_FROM_SERVER_PWD);
                if (password != null && !password.isEmpty()
                        && password.length() > 0 && !password.equals("")) {
                    password = password.replace("\"", "").replace("[", "")
                            .replace("]", "");
                    String[] passwordArray = password.split(",");
                    if (passwordArray != null) {
                        password = passwordArray[0];
                        oldPwdEditText.setText(password);
                    }
                }
            }

        }

        TextView changePasswordText = (TextView) findViewById(R.id.changepasswordscreen_text);
        if (userName != null && !userName.isEmpty()) {
            changePasswordText.setText(String.format(
                    getString(R.string.change_password_message), userName));
        }
        countDownWidget = (LinearLayout) findViewById(R.id.countdowun_background);
        Button changeButton = (Button) findViewById(R.id.passwordChangeButton);
        changeButton.setOnClickListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        if (activityAlertDialog != null && currentAlertDialogID != -1
                && isShowingAlertDialog) {
            dissmissAlert(activityAlertDialog, currentAlertDialogID);
        }
        super.onDestroy();
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passwordChangeButton:
                hideSoftInputFromWindow();

                String oldPassword = oldPwdEditText.getText().toString();
                String newPassword = newPwdEditText.getText().toString();
                String retypePassword = retypePwdEditText.getText().toString();
                if (oldPassword == null || oldPassword.trim().length() <= 0) {
                    // valid old password.
                    showDialog(AppConstants.DIALOG_INVALID_OLDPASSWORD);

                } else if (newPassword == null || newPassword.trim().length() <= 0) {
                    // valid new password.
                    showDialog(AppConstants.DIALOG_INVALID_NEWPASSWORD);

                } else if (retypePassword == null
                        || retypePassword.trim().length() <= 0) {
                    // valid retype password.
                    showDialog(AppConstants.DIALOG_INVALID_RETYPEPASSWORD);

                } else if (oldPassword.trim().equals(newPassword.trim())) {
                    // must not be same.
                    showDialog(AppConstants.DIALOG_SAME_OLDNEWPASSWORD);

                } else if (!newPassword.trim().equals(retypePassword.trim())) {
                    // must be same.
                    showDialog(AppConstants.DIALOG_DIFFRENT_NEWREPASSWORD);

                } else {
                    // send request
                    showDialog("Resetting...");
                    boolean isSucess = KeyTalkCommunicationManager.resetPassword(oldPassword,newPassword, this);
                    if(!isSucess) {
                        passwordResetError("We are not able to process your request. Please try again.");
                    }
                }
                break;
        }

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
        currentAlertDialogID = id;
        isShowingAlertDialog = true;
        switch (id) {
            case AppConstants.DIALOG_INVALID_OLDPASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.invalid_oldpassword));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.OK_text,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_INVALID_NEWPASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.invalid_newpassword));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.OK_text,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_INVALID_RETYPEPASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage
                        .setText(getString(R.string.invalid_retypepassword));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.OK_text,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_SAME_OLDNEWPASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage.setText(getString(R.string.same_old_newpassword));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.OK_text,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dissmissAlert(dialog, id);
                                    }
                                }).create();
                activityAlertDialog.setCanceledOnTouchOutside(false);
                return activityAlertDialog;
            case AppConstants.DIALOG_DIFFRENT_NEWREPASSWORD:
                dialogIcon.setImageResource(R.drawable.icon_info_transparent);
                dialogTxtMessage
                        .setText(getString(R.string.different_new_retypepassword));
                dialogTxtMessage.setTextSize(18);
                activityAlertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setIcon(0)
                        .setPositiveButton(R.string.OK_text,
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
    public void passwordResetError(String errorMessage) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("ChangePasswordActivity","passwordResetError :"+errorMessage);
        Intent doneIntent = new Intent();
        doneIntent.putExtra(AppConstants.IS_RESET_REQUEST_ERROR, true);
        doneIntent.putExtra(AppConstants.CERT_REQUEST_ERROR_MSG, errorMessage);
        setResult(RESULT_OK, doneIntent);
        finish();
    }

    @Override
    public void passwordResetDelay(int seconds) {
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("ChangePasswordActivity","passwordResetDelay started ");
        oldPwdEditText.setText(null);
        newPwdEditText.setText(null);
        retypePwdEditText.setText(null);
        countdowntimer = new MyCountDownTimer(seconds * 1000, 1000, countDownWidget,this);
        countdowntimer.startCountDown();
    }


    @Override
    public void timerCallBack() {
        // TODO Auto-generated method stub
        oldPwdEditText.requestFocus();
        KeyTalkCommunicationManager.addToLogFile("ChangePasswordActivity","timerCallBack started ");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(oldPwdEditText, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void credentialRequest(final String serviceUsers,
                                  final boolean isUserNameRequested, final boolean isPasswordRequested, final String passwordText,
                                  final boolean isPinRequested, final boolean isResponseRequested,
                                  final String challenge) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        dismissDialog();
        KeyTalkCommunicationManager.addToLogFile("ChangePasswordActivity","credentialRequest started ");
        layoutInflater = LayoutInflater.from(this);
        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        dialogIcon = (ImageView) dialogView.findViewById(R.id.dialog_image);
        dialogTxtMessage = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogIcon.setImageResource(R.drawable.icon_info_transparent);
        dialogTxtMessage.setText(getString(R.string.password_updated));
        dialogTxtMessage.setTextSize(18);
        activityAlertDialog = new AlertDialog.Builder(this) .setView(dialogView)
                .setIcon(0)
                .setCancelable(false)
                .setPositiveButton(R.string.OK_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int whichButton) {
                                dialog.cancel();
                                Intent doneIntent = new Intent();
                                doneIntent.putExtra(AppConstants.IS_CERT_REQUEST_DELAY_CREDENTIALS,
                                        true);
                                doneIntent.putExtra(AppConstants.AUTH_SERVICE_USERS, serviceUsers);
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
                        }).show();
        activityAlertDialog.setCanceledOnTouchOutside(false);
    }
}
