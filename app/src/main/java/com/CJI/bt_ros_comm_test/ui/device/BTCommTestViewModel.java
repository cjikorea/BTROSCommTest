package com.CJI.bt_ros_comm_test.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BTCommTestViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mText2;

    public BTCommTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("q");

        mText2 = new MutableLiveData<>();
        mText2.setValue("4321");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getTex2() {
        return mText2;
    }
    public LiveData<String> setText(String text) {
        mText2.setValue(text);

       return mText2;
    }

}