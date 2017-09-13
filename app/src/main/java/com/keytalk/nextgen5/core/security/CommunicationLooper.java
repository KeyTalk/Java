package com.keytalk.nextgen5.core.security;

import android.os.Handler;
import android.os.Looper;

import com.keytalk.nextgen5.core.CommunicationCommand;

/*
 * Class  :  CommunicationLooper
 * Description : Base thread class for handle all the request
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class CommunicationLooper extends Thread {

    private Handler handler;
    private int totalCompleted;
    private int totalQueued;
    private boolean shouldStoppImmediately = false;
    private boolean running = false;

    protected boolean isRunning() {
        return running;
    }

    public CommunicationLooper() {
        super();
    }

    protected void enqueCommandAtFrontOfQueue(final BaseCommand<?, ?> command) {
        handler.postAtFrontOfQueue(new CommunicationExecutor(command));
        totalQueued++;
    }

    protected void enqueCommandWithDelay(final BaseCommand<?, ?> command,
                                         long delayInMillis) {
        handler.postDelayed(new CommunicationExecutor(command), delayInMillis);
        totalQueued++;
    }

    protected void enqueueCommand(final BaseCommand<?, ?> command) {
        handler.post(new CommunicationExecutor(command));
        totalQueued++;
    }

    protected synchronized int getTotalCompleted() {
        return totalCompleted;
    }

    protected synchronized int getTotalQueued() {
        return totalQueued;
    }

    protected void cancelPendingOperations() {
        shouldStoppImmediately = true;
    }

    protected void resumeOperations() {
        shouldStoppImmediately = false;
    }

    // This method is allowed to be called from any thread
    public synchronized void requestStop() {
        handler.post(new Runnable() {
            public void run() {
                // This is guaranteed to run on the DownloadThread
                // so we can use myLooper() to get its looper
                Looper.myLooper().quit();
                running = false;
            }
        });
    }

    @Override
    public void run() {
        super.run();
        running = true;
        try {
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    class CommunicationExecutor implements Runnable {

        private CommunicationCommand mCommandToBeExecuted = null;

        protected CommunicationExecutor(CommunicationCommand command) {
            mCommandToBeExecuted = command;
        }

        public void run() {
            try {
                if (!shouldStoppImmediately) {
                    mCommandToBeExecuted.execute();
                }
            } finally {
                synchronized (CommunicationLooper.this) {
                    totalCompleted++;
                }
            }
        }
    }
}
