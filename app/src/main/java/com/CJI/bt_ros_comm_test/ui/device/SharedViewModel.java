package com.CJI.bt_ros_comm_test.ui.device;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel  extends ViewModel {

    private MutableLiveData<String> data  = new MutableLiveData<>();
    private MutableLiveData<String> dataX  = new MutableLiveData<>();
    private MutableLiveData<String> dataZ  = new MutableLiveData<>();
    private MutableLiveData<String> dataBT  = new MutableLiveData<>();
    private MutableLiveData<String> dataBTStatus  = new MutableLiveData<>();
    private MutableLiveData<String> data_BT_Deviceid  = new MutableLiveData<>();

    //Cmd data
    public void setData(String input){
        data.setValue(input);
    }
    public LiveData<String> getData(){
        return data;
    }

    //Cmd data
    public void setDataX(String input){
        dataX.setValue(input);
    }
    public LiveData<String> getDataX(){
        return dataX;
    }

    //Cmd data
    public void setDataZ(String input){
        dataX.setValue(input);
    }
    public LiveData<String> getDataZ(){
        return dataZ;
    }

    //Bluetooth device
    public void setDataBT(String input){
        dataBT.setValue(input);
    }
    public LiveData<String> getDataBT(){
        return dataBT;
    }

    //Bluetooth status
    public void setDataBTStatus(String input){
        dataBTStatus.setValue(input);
    }
    public LiveData<String> getDataBTStatus(){
        return dataBTStatus;
    }

    //Cmd data
    public void setBTdeviceid(String input){
        data_BT_Deviceid.setValue(input);
    }
    public LiveData<String> getBTdeviceid(){
        return data_BT_Deviceid;
    }

}
