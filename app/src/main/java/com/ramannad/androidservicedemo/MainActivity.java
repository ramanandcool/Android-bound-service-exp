package com.ramannad.androidservicedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ramannad.androidservicedemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MyService myService;
    private MainActivityViewModel viewModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        setContentView(binding.getRoot());

        viewModel.getBinder().observe(this,myBinder -> {
            if (myBinder != null){
                myService = myBinder.getService();
            }
            else {
                myService = null;
            }
        });
        viewModel.getIsProgressBarUpdating().observe(this,aBoolean -> {
            final Handler handler = new Handler(getMainLooper());
            final Runnable runnable =  new Runnable() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void run() {
                    if (viewModel.getIsProgressBarUpdating().getValue()){
                        if (viewModel.getBinder().getValue() != null){
                            if (myService.getProgress() == myService.getMaxValue()){
                                viewModel.setIsProgressBarUpdating(false);
                            }
                            binding.progressBar.setProgress(myService.getProgress());
                            binding.progressBar.setMax(myService.getMaxValue());
                            String progress = 100 * myService.getProgress() / myService.getMaxValue() + "%";
                            binding.textView.setText(progress);
                        }
                        handler.postDelayed(this,100);
                    }
                    else {
                        handler.removeCallbacks(this);
                    }
                }
            };

            if (aBoolean){
                binding.toggleUpdates.setText("Pause");
                handler.postDelayed(runnable,100);
            }
            else if (myService.getProgress() == myService.getMaxValue()){
                binding.toggleUpdates.setText("Restart");
            }
            else {
                binding.toggleUpdates.setText("Start");
            }
        });
        binding.toggleUpdates.setOnClickListener(v -> toggleUpdates());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel.getBinder() != null){
            unbindService(viewModel.getServiceConnection());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    @SuppressLint("SetTextI18n")
    private void toggleUpdates(){
        if(myService != null){
            if(myService.getProgress() == myService.getMaxValue()){
                myService.resetTask();
                binding.toggleUpdates.setText("Start");
            }
            else if(myService.getIsPaused()){
                    myService.unPausePretendLongRunningTask();
                    viewModel.setIsProgressBarUpdating(true);
            } else{
                    myService.pausePretendLongRunningTask();
                    viewModel.setIsProgressBarUpdating(false);
            }
        }
    }

    private void startService(){
        Intent serviceIntent = new Intent(this,MyService.class);
        startService(serviceIntent);
        bindService();
    }

    private void bindService(){
        Intent serviceIntent = new Intent(this,MyService.class);
        bindService(serviceIntent,viewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }
}
