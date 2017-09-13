package com.keytalk.nextgen5.core.security;

import android.os.AsyncTask;

/*
 * Class  :  ErrorHandlingAsyncTask
 * Description : An AsyncTask subclass that has exception support
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

abstract public class ErrorHandlingAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Throwable error = null;

    /**
     * Called when the operation succeeds
     */
    protected abstract void onSuccess(Result result) throws Exception;

    /**
     * Called when an error occurs
     */
    protected abstract void onError(Throwable t);

    /**
     * Called in the background
     */
    protected abstract Result realDoInBackground(Params... params) throws Exception;

    protected Result doInBackground(Params... params) {
        try {
            return realDoInBackground(params);
        } catch(Exception e) {
            error = e;
        }
        return null;
    }

    @Override
    final protected void onPostExecute(Result result) {
        if (error != null)
            onError(error);
        else try {
            onSuccess(result);
        } catch (Throwable t) {
            onError(t);
        }
    }
}
