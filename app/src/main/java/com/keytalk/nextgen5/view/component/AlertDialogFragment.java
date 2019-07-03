/*
 * Class  :  AlertDialogFragment
 * Description :
 *
 * Created By Jobin Mathew on 2018
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/*
 * Class  :  AlertDialogFragment
 * Description : Alert Dialog activity for showing customized alert messages
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class AlertDialogFragment extends DialogFragment {

    public interface AlertDialogCallBack {
        void doPositiveButtonClick();
        void doNegativeButtonClick();
    }

    public AlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static AlertDialogFragment newInstance(final String title, final String message,
                                                  final String positiveButton, final String negativeButton) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positiveButton", positiveButton);
        args.putString("negativeButton", negativeButton);
        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String message = getArguments().getString("message");
        final String positiveButton = getArguments().getString("positiveButton");
        final String negativeButton = getArguments().getString("negativeButton");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(positiveButton,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((AlertDialogCallBack)getActivity()).doPositiveButtonClick();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        if(negativeButton != null) {
            alertDialogBuilder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((AlertDialogCallBack)getActivity()).doNegativeButtonClick();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

            });
        }
        AlertDialog activityAlertDialog = alertDialogBuilder.create();
        activityAlertDialog.setCanceledOnTouchOutside(false);
        return activityAlertDialog;
    }
}
