/*
 * Class  :  Utilities
 * Description :
 *
 * Created By Jobin Mathew on 2019
 * All rights reserved @ keytalk.com
 */

package com.keytalk.nextgen5.view.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.keytalk.nextgen5.util.PreferenceManager;

import java.util.Locale;

/**
 * Created by SrashtiG on 3/11/2019.
 */

public class Utilities {

    public static void checkLanguage(Context context)
    {
        try {

            String langsaved = PreferenceManager.getString(context, "MYSTRLABEL");
            Locale locale;
            if(langsaved!=null){
                locale = new Locale(langsaved);

                Locale.setDefault(locale);
                Configuration config = context.getResources().getConfiguration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

            }}
        catch (Exception e){
            Log.e("language change","Error");
        }
    }
}
