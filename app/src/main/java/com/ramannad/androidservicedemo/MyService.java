package com.ramannad.androidservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private final IBinder mBinder = new MyBinder();
    private Handler mHandler;
    private int mProgress , mMaxValue;
    private Boolean mIsPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(getMainLooper());
        mProgress = 0 ;
        mMaxValue = 5000;
        mIsPaused = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    public Boolean getIsPaused(){
        return mIsPaused;
    }

    public int getProgress(){
        return mProgress;
    }

    public int getMaxValue(){
        return mMaxValue;
    }

    public void pausePretendLongRunningTask() {
        mIsPaused = true;
    }

    public void unPausePretendLongRunningTask(){
        mIsPaused = false;
        startPretendLongRunningTask();
    }

    public void startPretendLongRunningTask(){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mProgress >= mMaxValue || mIsPaused ){
                    mHandler.removeCallbacks(this);
                    pausePretendLongRunningTask();
//                    mHandler.postDelayed(this,100);
                }
                else {
                    mProgress += 100;
                    mHandler.postDelayed(this,100);
                }
            }
        };
        mHandler.postDelayed(runnable,100);
    }

    public void resetTask(){
        mProgress = 0 ;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
