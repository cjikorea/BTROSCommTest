package com.CJI.bt_ros_comm_test.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainControlViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mTextBT_Device;

    public MainControlViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Bluetooth ID");

        mTextBT_Device = new MutableLiveData<>();
        mTextBT_Device.setValue("This is home fragment");
    }

    public LiveData<String> getText() {return mText; }
    public LiveData<String> getTextBT_Device() {
        return mTextBT_Device;
    }
}