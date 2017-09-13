package com.keytalk.nextgen5.view.component;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keytalk.nextgen5.R;

/*
 * Class  :  MyCountDownTimer
 * Description : CountDownTimer activity for showing seconds of delay when server request the same
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class MyCountDownTimer extends CountDownTimer
{

    private long starttime;
    private boolean isrunning=false;
    private LinearLayout countDownWidgett=null;
    private TextView countDownText=null;
    private TimerCallBack timerCallBack;
    private long millisUntilFinished=0;

    public MyCountDownTimer(long startTime, long interval,LinearLayout countDownWidgett,TimerCallBack tryAgain)
    {
        super(startTime, interval);
        this.countDownWidgett=countDownWidgett;
        this.countDownText=(TextView)countDownWidgett.findViewById(R.id.countdown_text);
        this.starttime=startTime;
        this.timerCallBack = tryAgain;
        millisUntilFinished=startTime;
    }

    public void setTimerCallBack(TimerCallBack tryAgain)
    {
        this.timerCallBack = tryAgain;
    }

    OnTouchListener ontouchlistener=new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            return true;
        }
    };

    public void startCountDown()
    {
        isrunning=true;
        countDownWidgett.setVisibility(View.VISIBLE);
        countDownWidgett.setOnTouchListener(ontouchlistener);
        countDownText.setText("" + starttime/1000);
        Log.d("TAG"," starttime/1000:"+ starttime/1000);
        start();
    }

    @Override
    public void onFinish()
    {
        countDownWidgett.setOnTouchListener(null);
        this.countDownWidgett.setVisibility(View.INVISIBLE);
        isrunning=false;
        timerCallBack.timerCallBack();
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        this.millisUntilFinished=millisUntilFinished;
        this.countDownText.setText("" + this.millisUntilFinished/1000);
    }

    public boolean isRunning()
    {
        return isrunning;
    }

    public long getmillisUntilFinished()
    {
        return  millisUntilFinished;
    }
}
