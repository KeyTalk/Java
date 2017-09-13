package com.keytalk.nextgen5.view.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.keytalk.nextgen5.view.util.AppConstants;

/*
 * Class  :  ErrorDialog
 * Description : Alert Dialog activity for showing customized alert messages
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ErrorDialog extends AlertDialog {

    private int icon;
    private String title;
    private String message;
    private Context context;
    private boolean isFinished;

    public ErrorDialog(Context context) {
        this(context, "", "", true);
    }

    public ErrorDialog(Context context, String title, String message) {
        this(context, title, message, true);
    }

    public ErrorDialog(Context context, String title, String message,
                       boolean isFinished) {
        this(context, android.R.drawable.ic_dialog_alert, title, message,
                isFinished);
    }

    public ErrorDialog(Context context, int icon, String title, String message,
                       boolean isFinished) {
        super(context);
        init(context, icon, title, message, isFinished);
        setData();
    }

    private void init(Context context, int icon, String title, String message,
                      boolean isFinished) {
        this.context = context;
        this.icon = icon;
        this.title = title;
        this.message = message;
        this.isFinished = isFinished;
    }

    @SuppressWarnings("deprecation")
    private void setData() {
        setTitle(title);
        setMessage(message);
        setIcon(icon);
        setButton(AppConstants.LABEL_OK, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFinished) {
                    ((Activity) context).finish();
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isFinished) {
                    ((Activity) context).finish();
                }
            }
        });
    }
}
