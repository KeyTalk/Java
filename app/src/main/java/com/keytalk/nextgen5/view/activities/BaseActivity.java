/*
 * Class  :  BaseActivity
 * Description :
 *
 * Created By Jobin Mathew on 2019
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.keytalk.nextgen5.view.util.LocaleHelper;

public abstract  class BaseActivity extends AppCompatActivity {

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
