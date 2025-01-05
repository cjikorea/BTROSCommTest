package com.CJI.bt_ros_comm_test;


import android.content.SharedPreferences;

import com.CJI.bt_ros_comm_test.ui.device.SerialService;
import com.CJI.bt_ros_comm_test.ui.device.SerialSocket;
import com.CJI.bt_ros_comm_test.ui.device.SharedViewModel;
import com.CJI.bt_ros_comm_test.ui.device.BTCommTestFragment;

public class Constants {

    // values have to be globally unique
    public static final String INTENT_ACTION_DISCONNECT =  BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    // values have to be unique within each app
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;
    //my user data
    public static String deiveAddressString = null;
    public static String deiveAddressName = null;
    public static  SerialService service = null;
    public static SerialSocket serialsocket = null;
    public static BTCommTestFragment BTCommTestFragment;
    public static MainActivity mainActivity;
    public static SharedViewModel sharedViewModel;
    public static boolean bluetooth_on = false;

    public static SharedPreferences sharedPreferences;


    private Constants() {}
}
