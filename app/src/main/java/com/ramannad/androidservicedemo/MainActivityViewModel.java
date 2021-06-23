package com.ramannad.androidservicedemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mIsProgressUpdating = new MutableLiveData<>();
    private final MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder)iBinder;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return mBinder;
    }

    public LiveData<Boolean> getIsProgressBarUpdating(){
        return mIsProgressUpdating;
    }


    public void setIsProgressBarUpdating(Boolean isUpdating){
        mIsProgressUpdating.postValue(isUpdating);
    }
}
