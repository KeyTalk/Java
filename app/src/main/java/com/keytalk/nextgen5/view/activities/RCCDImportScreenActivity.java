package com.keytalk.nextgen5.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.application.KeyTalkApplication;
import com.keytalk.nextgen5.core.RCCDDownloadCallBack;
import com.keytalk.nextgen5.core.security.KeyTalkCommunicationManager;
import com.keytalk.nextgen5.core.security.Response;
import com.keytalk.nextgen5.util.Keys;
import com.keytalk.nextgen5.util.NetworkUtil;
import com.keytalk.nextgen5.util.PreferenceManager;
import com.keytalk.nextgen5.view.component.AlertDialogFragment;
import com.keytalk.nextgen5.view.component.ErrorDialog;
import com.keytalk.nextgen5.view.component.SegmentedControlBar;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.io.InputStream;

import static android.R.attr.dialogIcon;
import static com.keytalk.nextgen5.view.util.AppConstants.ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
import static com.keytalk.nextgen5.view.util.AppConstants.ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_RESPONSE;
import static com.keytalk.nextgen5.view.util.AppConstants.REQUEST_READ_PHONE_STATE;

/*
 * Class  :  RCCDImportScreenActivity
 * Description : Import RCCD file from server activity
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDImportScreenActivity extends AppCompatActivity implements
        View.OnClickListener,RCCDDownloadCallBack,SegmentedControlBar.SegmentedButtonListener, AlertDialogFragment.AlertDialogCallBack {
    private EditText rccdInputEditText = null;
    private ProgressBar mProgressBar = null;
    boolean isNative = false;
    private SegmentedControlBar mSegementedControlBar;
    private static String providerName = null;
    private static int serviceCount = 0;
    private String alertType = AppConstants.ALERT_DIALOG_TYPE_UNKNOWN;
    private final String TAG = "RCCDImportScreenActivity";

    public void setData(Response<?> response) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rccdimport_screen);
        mSegementedControlBar = (SegmentedControlBar) findViewById(R.id.browser_type_group);
        if(android.os.Build.VERSION.SDK_INT < 14) {
            mSegementedControlBar.setVisibility(View.GONE);
            findViewById(R.id.browser_type_group_text).setVisibility(View.GONE);
            PreferenceManager.put(RCCDImportScreenActivity.this, Keys.PreferenceKeys.DEVICE_TYPE,false);
        } else if(android.os.Build.VERSION.SDK_INT >= 14 && android.os.Build.VERSION.SDK_INT <= 18) {
            mSegementedControlBar.setVisibilityStatus(View.VISIBLE, View.VISIBLE);
            mSegementedControlBar.setTextStatus(getString(R.string.embedded_browser),getString(R.string.native_browser));
            isNative = PreferenceManager.getBoolean(this, Keys.PreferenceKeys.DEVICE_TYPE);
            if(isNative) {
                mSegementedControlBar.setCheckedStatus(R.id.radio_btn_3);
            } else {
                mSegementedControlBar.setCheckedStatus(R.id.radio_btn_1);
            }
            mSegementedControlBar.setSegmentBtnListener(this);
        } else {
            mSegementedControlBar.setVisibility(View.GONE);
            findViewById(R.id.browser_type_group_text).setVisibility(View.GONE);
            PreferenceManager.put(RCCDImportScreenActivity.this, Keys.PreferenceKeys.DEVICE_TYPE,true);
        }

        rccdInputEditText = (EditText) findViewById(R.id.rccd_input_edittext);
        //rccdInputEditText.setText("https://downloads.keytalk.com/corp.rccd");
        //rccdInputEditText.setText("https://rccd.keytalk.com/v2/demopage.rccd");

        rccdInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()  {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    softInputDoneClick();
                }
                return false;
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.spinner_progressbar);
        Button importButton = (Button) findViewById(R.id.rccd_import_ok_button);
        importButton.setOnClickListener(this);
        Button resetButton = (Button) findViewById(R.id.rccd_import_reset_button);
        resetButton.setOnClickListener(this);
        Button resetSessionButton = (Button) findViewById(R.id.rccd_import_reset_session_button);
        resetSessionButton.setOnClickListener(this);
        Button viewClientIdentifierButton = (Button) findViewById(R.id.rccd_import_client_identifier_button);
        viewClientIdentifierButton.setOnClickListener(this);
        Intent emailRCCDIntent = getIntent();
        if (emailRCCDIntent.hasExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_EMAIL)) {
            Uri emailFileURI = Uri.parse(emailRCCDIntent.getStringExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_EMAIL));
            if (emailFileURI != null) {
                String emailFileName = getContentName(getContentResolver(),emailFileURI);
                if(TextUtils.isEmpty(emailFileName)) {
                    emailFileName = emailFileURI.getLastPathSegment();
                }
                if (emailFileName != null && !emailFileName.equals("") && !emailFileName.isEmpty()) {
                    try {
                        InputStream emailRCCDFileInputStream = getContentResolver().openInputStream(emailFileURI);
                        KeyTalkCommunicationManager keyTalkCommunicationManager = new KeyTalkCommunicationManager(this);
                        keyTalkCommunicationManager.getRCCDFileFromEmail(emailRCCDFileInputStream,emailFileName);
                        KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from email is added to system : "+emailFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from email is failed due exception : "+e);
                        alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                        DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                                getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                        alertDialog.show(getSupportFragmentManager(), "dialog");
                    }
                } else {
                    // dislay error Dialog
                    KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from email is failed due to empty file name");
                    alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                    DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                            getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                }
            } else {
                // dislay error Dialog
                KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD downloading from email is failed due to empty URI");
                alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                        getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                alertDialog.show(getSupportFragmentManager(), "dialog");
            }
        } else if (emailRCCDIntent.hasExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_MEMORY)) { //File from local memory
            Uri emailFileURI = Uri.parse(emailRCCDIntent.getStringExtra(AppConstants.IMPORTED_RCCD_FILE_FROM_MEMORY));
            if (emailFileURI != null) {
                //String emailFileName = getContentName(getContentResolver(),emailFileURI);
                String emailFileName = emailFileURI.getLastPathSegment();
                if(TextUtils.isEmpty(emailFileName)) {
                    emailFileName = getContentName(getContentResolver(),emailFileURI);
                }
                if (emailFileName != null && !emailFileName.equals("") && !emailFileName.isEmpty()) {
                    try {
                        InputStream emailRCCDFileInputStream = getContentResolver().openInputStream(emailFileURI);
                        KeyTalkCommunicationManager keyTalkCommunicationManager = new KeyTalkCommunicationManager( this);
                        keyTalkCommunicationManager.getRCCDFileFromEmail(emailRCCDFileInputStream,emailFileName);
                        KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from memory is added to system : "+emailFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from memory is failed due exception : "+e);
                        alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                        DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                                getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                        alertDialog.show(getSupportFragmentManager(), "dialog");
                    }
                } else {
                    // dislay error Dialog
                    KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD from memory is failed due to empty file name");
                    alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                    DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                            getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                }
            } else {
                // dislay error Dialog
                KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD downloading from memory is failed due to empty URI");
                alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
                DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                        getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.cancel_text));
                alertDialog.show(getSupportFragmentManager(), "dialog");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onDestroy()  {
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            case R.id.rccd_import_ok_button:
                hideSoftInputFromWindow();
                validateRequestURL();
                break;
            case R.id.rccd_import_reset_button:
                alertType = AppConstants.ALERT_DIALOG_TYPE_RESET_RCCD;
                DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                        getString(R.string.rccdscreen_reset_message), getString(R.string.reset_text), getString(R.string.cancel_text));
                alertDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.rccd_import_reset_session_button:
                alertType = AppConstants.ALERT_DIALOG_TYPE_RESET_SESSION;
                alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                        getString(R.string.rccdscreen_reset_session_message), getString(R.string.reset_text), getString(R.string.cancel_text));
                alertDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.rccd_import_client_identifier_button:
                KeyTalkCommunicationManager.addToLogFile(TAG,"Tapped view client Identifier button");
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                    } else {
                        String deviceID = ((TelephonyManager) RCCDImportScreenActivity.this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),getString(R.string.view_client_identifier_message,deviceID), getString(R.string.OK_text), null);
                        alertDialog.show(getSupportFragmentManager(), "dialog");
                    }
                } else {
                    String deviceID = ((TelephonyManager) RCCDImportScreenActivity.this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),getString(R.string.view_client_identifier_message,deviceID), getString(R.string.OK_text), null);
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String deviceID = ((TelephonyManager) RCCDImportScreenActivity.this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),getString(R.string.view_client_identifier_message,deviceID), getString(R.string.OK_text), null);
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                } else {
                    DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),getString(R.string.phonestat_permission_alert_message), getString(R.string.OK_text), null);
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                }
            }
        }
    }



    public void softInputDoneClick() {
        hideSoftInputFromWindow();
        validateRequestURL();
    }

    public void hideSoftInputFromWindow()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rccdInputEditText.getWindowToken(), 0);
    }

    public void showSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(rccdInputEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void validateRequestURL() {
        if (TextUtils.isEmpty(rccdInputEditText.getText().toString()) || rccdInputEditText.getText().toString() == null) {
            alertType = AppConstants.ALERT_DIALOG_TYPE_EMPTY_URL;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title), getString(R.string.no_values_entered), getString(R.string.OK_text), null);
            alertDialog.show(getSupportFragmentManager(), "dialog");
        } else {
			/*Pattern pattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?(.rccd){1}");
			Matcher matcher = pattern.matcher(rccdInputEditText.getText().toString());
		    if(matcher.matches()) {*/
            if(NetworkUtil.getInstance(this).isNetworkAvailable(true)) {
                String providerURL = rccdInputEditText.getText().toString().trim();
                if(providerURL.length() < 3 /*|| !providerURL.substring(providerURL.length() - 5).equals(".rccd")*/) {
                    alertType = AppConstants.ALERT_DIALOG_TYPE_EMPTY_URL;
                    DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title), getString(R.string.no_values_entered), getString(R.string.OK_text), null);
                    alertDialog.show(getSupportFragmentManager(), "dialog");
                } else {
                    boolean isSecure = false;
                    if(providerURL.startsWith("https://")) {
                        isSecure = true;
                    }
                    providerURL = (providerURL.replace("http://", "")).replace("https://", "");
                    if(providerURL.split("/").length <= 1) {
                        alertType = AppConstants.ALERT_DIALOG_TYPE_EMPTY_URL;
                        DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title), getString(R.string.no_values_entered), getString(R.string.OK_text), null);
                        alertDialog.show(getSupportFragmentManager(), "dialog");
                    } else {
                        if(!providerURL.substring(providerURL.length() - 5).equals(".rccd")) {
                            if(providerURL.split("\\.").length > 2) {
                                if(providerURL.substring(providerURL.lastIndexOf(".") + 1) == null || providerURL.substring(providerURL.lastIndexOf(".") + 1).isEmpty()) {
                                    providerURL = providerURL + "rccd";
                                } else {
                                    providerURL = providerURL+ ".rccd"; ;
                                }
                            } else {
                                if(providerURL.substring(providerURL.lastIndexOf(".") + 1) == null || providerURL.substring(providerURL.lastIndexOf(".") + 1).isEmpty()) {
                                    providerURL = providerURL + "rccd";
                                } else {
                                    providerURL = providerURL + ".rccd";
                                }
                            }
                        }
                        if(isSecure) {
                            providerURL = "https://"+providerURL;
                        } else {
                            providerURL = "http://"+providerURL;
                        }
                        KeyTalkCommunicationManager.addToLogFile(TAG,"RCCD request initiated :"+providerURL);
                        mProgressBar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        KeyTalkCommunicationManager keyTalkCommunicationManager = new KeyTalkCommunicationManager(this);
                        keyTalkCommunicationManager.getRCCDFileFromURL(KeyTalkApplication.getCommunicationLooper(), providerURL);
                    }
                }
            } else {
                KeyTalkCommunicationManager.addToLogFile(TAG,"Tapped ok button without valid network connection");
                DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title), getString(R.string.no_network_message), getString(R.string.OK_text), null);
                alertDialog.show(getSupportFragmentManager(), "dialog");
            }
        }
    }



    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case AppConstants.DIALOG_SUCESS_IMPORT_RCCD_FILE:
                AlertDialog alertDialog = (AlertDialog)dialog;
                //alertDialog.setMessage(getString(R.string.sucess_rccd_download, providerName,serviceCount));
                alertDialog.setMessage("Provider "+providerName+" ("+serviceCount+" Services) was imported from the RCCD file");
                break;
            default:
                break;
        }
        super.onPrepareDialog(id, dialog);
    }


    public String getContentName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, new String[] { "_display_name" },
                null, null, null);
        if(cursor == null)
            return null;
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex("_display_name");
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
		/*
		 * Working in some phones
		 * Cursor cursor = resolver.query(uri, null,
		 * null, null, null); cursor.moveToFirst(); int nameIndex =
		 * cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME); if
		 * (nameIndex >= 0) { return cursor.getString(nameIndex); } else {
		 * return null;
		 *
		 * }
		 */
    }

    @Override
    public void rccdDownloadCallBack(String providerName, int serviceCount,
                                     int downloadStatus) {
        //dismissDialog();
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // TODO Auto-generated method stub
        RCCDImportScreenActivity.providerName = providerName;
        RCCDImportScreenActivity.serviceCount =serviceCount;
            //downloadStatus = DIALOG_INVALID_DATA_IN_RCCD_RESPONSE = 5003 --> Invalid data;
            //downloadStatus = DIALOG_SUCESS_IMPORT_RCCD_FILE = 5005; ---> Sucess
            //downloadStatus = DIALOG_NO_DATA_IN_RESPONSE = 5002;  --> Error, not data in rccd
            //downloadStatus = DIALOG_INVALID_DATA_IN_RCCD_EMAIL = 5004; --> Invalide rccd file in email;
        if(downloadStatus == AppConstants.DIALOG_NO_DATA_IN_RESPONSE) {
            alertType = AppConstants.ALERT_DIALOG_TYPE_NO_DATA_IN_RESPONSE;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                    getString(R.string.empty_request_response), getString(R.string.report_button), getString(R.string.OK_text));
            alertDialog.show(getSupportFragmentManager(), "dialog");
        } else if(downloadStatus == AppConstants.DIALOG_INVALID_DATA_IN_RCCD_RESPONSE) {
            alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_RESPONSE;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                    getString(R.string.invalid_rccd_download_response), getString(R.string.report_button), getString(R.string.OK_text));
            alertDialog.show(getSupportFragmentManager(), "dialog");
        } else if(downloadStatus == AppConstants.DIALOG_INVALID_DATA_IN_RCCD_EMAIL) {
            alertType = ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                    getString(R.string.invalid_rccd_download_email), getString(R.string.report_button), getString(R.string.OK_text));
            alertDialog.show(getSupportFragmentManager(), "dialog");
        } else if(downloadStatus == AppConstants.DIALOG_SUCESS_IMPORT_RCCD_FILE) {
            alertType = AppConstants.ALERT_DIALOG_TYPE_RCCD_FROM_URL_SUCESS;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                    "Provider "+providerName+" ("+serviceCount+" Services) was imported from the RCCD file", getString(R.string.OK_text), null);
            alertDialog.show(getSupportFragmentManager(), "dialog");
        } else {
            alertType = AppConstants.ALERT_DIALOG_TYPE_RCCD_FROM_URL_FAILURE;
            DialogFragment alertDialog = AlertDialogFragment.newInstance(getString(R.string.import_rccd_alert_title),
                    getString(R.string.invalid_rccd_download_response), getString(R.string.report_button), getString(R.string.cancel_text));
            alertDialog.show(getSupportFragmentManager(), "dialog");
        }
    }

    public void reportWithEmail() {
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
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public String onSegmentButtonClicked(View view, String buttonValue) {
        switch (view.getId()) {

            case R.id.radio_btn_1:
                isNative = false;
                //showDialog(AppConstants.DIALOG_CHANGE_BROWSER_CONFIRM_MSG);
                PreferenceManager.put(RCCDImportScreenActivity.this, Keys.PreferenceKeys.DEVICE_TYPE, isNative);
                KeyTalkCommunicationManager.addToLogFile(TAG, "Changing browser");
                KeyTalkCommunicationManager.removeAllCertificate(RCCDImportScreenActivity.this);
                return null;
            case R.id.radio_btn_3:
                isNative = true;
                //showDialog(AppConstants.DIALOG_CHANGE_BROWSER_CONFIRM_MSG);
                PreferenceManager.put(RCCDImportScreenActivity.this, Keys.PreferenceKeys.DEVICE_TYPE, isNative);
                KeyTalkCommunicationManager.addToLogFile(TAG, "Changing browser");
                KeyTalkCommunicationManager.removeAllCertificate(RCCDImportScreenActivity.this);
                return null;
            default:
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rccd_import_screen, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_announcement) {
            if(KeyTalkCommunicationManager.isRCCDFileExist(RCCDImportScreenActivity.this)) {
                Intent intent = new Intent(RCCDImportScreenActivity.this, ServiceListingActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RCCDImportScreenActivity.this, "Sorry, you don't have service to be show!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doPositiveButtonClick() {
        switch (alertType) {
            case ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_EMAIL:
                reportWithEmail();
                break;
            case AppConstants.ALERT_DIALOG_TYPE_RESET_RCCD:
                KeyTalkCommunicationManager.addToLogFile(TAG,"Resetting RCCD file and certificates");
                KeyTalkCommunicationManager.removeAllRCCDFiles(RCCDImportScreenActivity.this);
                break;
            case AppConstants.ALERT_DIALOG_TYPE_RESET_SESSION:
                KeyTalkCommunicationManager.addToLogFile(TAG,"Resetting certificates");
                KeyTalkCommunicationManager.removeAllCertificate(RCCDImportScreenActivity.this);
                break;
            case AppConstants.ALERT_DIALOG_TYPE_EMPTY_URL:
                rccdInputEditText.requestFocus();
                showSoftInputFromWindow();
                break;
            case AppConstants.ALERT_DIALOG_TYPE_RCCD_FROM_URL_FAILURE:
                reportWithEmail();
                break;
            case AppConstants.ALERT_DIALOG_TYPE_RCCD_FROM_URL_SUCESS:
                finish();
                startActivity(new Intent(RCCDImportScreenActivity.this, ServiceListingActivity.class));
                break;
            case AppConstants.ALERT_DIALOG_TYPE_NO_DATA_IN_RESPONSE:
                reportWithEmail();
                break;
            case AppConstants.ALERT_DIALOG_TYPE_INVALID_DATA_IN_RCCD_RESPONSE:
                reportWithEmail();
                break;


            default:
                break;
        }
        alertType = AppConstants.ALERT_DIALOG_TYPE_UNKNOWN;
    }

    @Override
    public void doNegativeButtonClick() {
        switch (alertType) {
            default:
                break;
        }
        alertType = AppConstants.ALERT_DIALOG_TYPE_UNKNOWN;
    }
}
