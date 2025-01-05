package com.CJI.bt_ros_comm_test.ui.main;

import android.media.AudioManager;
import android.media.ToneGenerator;

import static java.lang.Thread.sleep;

import com.CJI.bt_ros_comm_test.ui.device.SharedViewModel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.CJI.bt_ros_comm_test.Constants;
import com.CJI.bt_ros_comm_test.databinding.FragmentMaincontrolBinding;
import com.CJI.bt_ros_comm_test.R;


public class MainControlFragment extends Fragment {

    private FragmentMaincontrolBinding binding;
    private  String strSpeedX = "0.0";
    private  String strSpeedZ = "0.0";
    private EditText editTextSpeedZ;
    private  EditText editTextSpeedX;
    private  String strCmdOld = "";
    private int MAX_DELAY = 500;
    private Long mLastClickTime = 0L;
    private  boolean bSendingData = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainControlViewModel mainControlViewModel =
                new ViewModelProvider(this).get(MainControlViewModel.class);

        binding = FragmentMaincontrolBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonMoveX.setEnabled(false);
        binding.buttonMoveZ.setEnabled(false);

        final TextView textView = binding.textHomeBtDevice;
        mainControlViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

////////////////////////////////////////////////////////
//KEY PAD MOVE BUTTON => STOP  button
////////////////////////////////////////////////////////
        final ImageButton sendS = binding.imageButtonStop;
        sendS.setOnClickListener(v -> {

            //연속 누름 방지
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();

                try {
                    sendCmd("movestop");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

////////////////////////////////////////////////////////
//KEY PAD MOVE BUTTON => FORWOARD  button
////////////////////////////////////////////////////////
        final ImageButton sendXAcc = binding.imageButtonXAcc;
        sendXAcc.setOnClickListener(v -> {

            //연속 누름 방지
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();
                try {
                    sendCmd("moveAcc");
                    sleep(700);
                    sendCmd("movestart");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

////////////////////////////////////////////////////////
//KEY PAD MOVE BUTTON => BACKWARD  button
////////////////////////////////////////////////////////
        final ImageButton sendXDec = binding.imageButtonXDec;
        sendXDec.setOnClickListener(v -> {

            doBeepSound();
            //연속 누름 방지
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {
                try {

                    sendCmd("moveDec");
                    sleep(700);
                    sendCmd("movestart");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

////////////////////////////////////////////////////////
//KEY PAD MOVE BUTTON => Z ++  button
////////////////////////////////////////////////////////
        final ImageButton turnAcc = binding.ImagebuttonAcc;
        turnAcc.setOnClickListener(v -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();
                try {
                    sendCmd("turnR");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });
////////////////////////////////////////////////////////
//KEY PAD MOVE BUTTON => Z --  button
////////////////////////////////////////////////////////
        final ImageButton turnDec = binding.ImagebuttonDec;
        turnDec.setOnClickListener(v -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();
                try {
                    sendCmd("turnL");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

////////////////////////////////////////////////////////
//CONTINUE MOVE BUTTON => SpeedX  MOV  button
////////////////////////////////////////////////////////
        final Button moveX = binding.buttonMoveX;
        moveX.setOnClickListener(v -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();

                try {
                    sendCmd("movestart");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });
////////////////////////////////////////////////////////
//CONTINUE MOVE BUTTON => SpeedZ  MOV  button
////////////////////////////////////////////////////////
        final Button moveZ = binding.buttonMoveZ;
        moveZ.setOnClickListener(v -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime > MAX_DELAY) {

                doBeepSound();
                try {
                    sendCmd("movestart");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        });

////////////////////////////////////////////////////////////////////////////////////
// SPEED value setting  ===> X  ( 전진. 후진)
///////////////////////////////////////////////////////////////////////////////////
        editTextSpeedX =binding.editTextTextSpeedX;

        final SeekBar speedX = binding.seekBarSpeedX;
        speedX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                double dSpeed = i * 0.01;
                strSpeedX = String.format("%.2f",dSpeed);
                editTextSpeedX.setText(strSpeedX);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
////////////////////////////////////////////////////////////////////////////////////
// SPEED value setting  ===> Z  ( ROTATE)
///////////////////////////////////////////////////////////////////////////////////
        editTextSpeedZ =binding.editTextTextSpeedZ;

        final SeekBar speedZ = binding.seekBarSpeedZ;
        speedZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                double dSpeed = i * 0.01; // 100 => 0.01
                strSpeedZ = String.format("%.2f",dSpeed);
                editTextSpeedZ.setText(strSpeedZ);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final  Button setSpeedX = binding.buttonSetspeedX;
        final  Button setSpeedZ = binding.buttonSetspeedZ;
        setSpeedX.setOnClickListener(v -> sendSpeedDataX());
        setSpeedZ.setOnClickListener(v -> sendSpeedDataZ());

        // ViewModel 공유
        Constants.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        TextView textViewBtDevice = binding.textHomeBtDevice.findViewById(R.id.text_home_bt_device);
        TextView textViewBtDeviceStatus = binding.textViewBTStatus.findViewById(R.id.textView_BT_Status);

        // 데이터 감시
        Constants.sharedViewModel.getDataBT().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewBtDevice.setText(s);
            }
        });

        // 데이터 감시
        Constants.sharedViewModel.getDataBTStatus().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("connect")==true) {
                    textViewBtDeviceStatus.setTextColor(Color.GREEN);
                }
                else {
                    textViewBtDeviceStatus.setTextColor(Color.GRAY);
                }
                //textViewBtDeviceStatus.setText(s);
            }
        });

        Button button_setSpeedX = binding.buttonSetspeedX;
        button_setSpeedX.setOnClickListener(v -> {
            binding.buttonMoveX.setEnabled(true);
        });

        Button button_setSpeedZ = binding.buttonSetspeedZ;
        button_setSpeedZ.setOnClickListener(v -> {
            binding.buttonMoveZ.setEnabled(true);
        });



        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }

    void doBeepSound()
    {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                tone.startTone(ToneGenerator.TONE_DTMF_S,50);
            }
        };

        runnable.run();

    }

    void putRunable(String cmdstr) throws InterruptedException {
        double dSpeed = 0.0;
        double dSpeedZ = 0.00;//

        String strSendData = "";
        String readSpeedZ = "";
        String strSendDataZ = "";

        String setSpeed  = "";
        String dispData = "";
        String dispDataZ = "";

        Constants.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        bSendingData =true;

        switch(cmdstr){

            case "movestart":
                // 직진 이동시 각속도 0.0 으로 강체 셋팅
                dSpeed = 0.00;//
                strSendData = String.format("speedz:%S:", dSpeed);
                dispData = String.format("%.2f", dSpeed);
                editTextSpeedZ.setText(dispData);
                // 데이터 설정
                Constants.sharedViewModel.setDataZ(strSendData);

                readSpeedZ = binding.editTextTextSpeedZ.getText().toString();
                dSpeedZ = Double.valueOf(readSpeedZ);

                if(dSpeedZ != 0.0) {
                    sendSpeedDataZ();
                    //sleep(100);
                }

                // 데이터 설정
                cmdstr = "movestart";
                Constants.sharedViewModel.setData("noType");
                sendStringCmd(cmdstr);

                break;

            case "turnR":
                binding.buttonMoveZ.setEnabled(false);
                setSpeed = binding.editTextTextSpeedZ.getText().toString();
                dSpeed = Double.valueOf(setSpeed);

                if(dSpeed > -2.0) {
                    dSpeed = dSpeed - 0.02;
                }
                strSendData = String.format("speedz:%S:", dSpeed);
                dispData = String.format("%.2f", dSpeed);
                editTextSpeedZ.setText(dispData);

                // 데이터 설정
                Constants.sharedViewModel.setDataZ(cmdstr);
                sendSpeedDataZ();

                strCmdOld = cmdstr;

                break;

            case "turnL":
                binding.buttonMoveZ.setEnabled(false);
                setSpeed = binding.editTextTextSpeedZ.getText().toString();
                dSpeed = Double.valueOf(setSpeed);

                if(dSpeed < 2.0) {
                    dSpeed = dSpeed + 0.02;
                }
                strSendData = String.format("speedz:%S:", dSpeed);
                dispData = String.format("%.2f", dSpeed);
                editTextSpeedZ.setText(dispData);

                // 데이터 설정
                Constants.sharedViewModel.setDataZ(cmdstr);
                sendSpeedDataZ();

                strCmdOld = cmdstr;

                break;

            case "moveAcc":
                binding.buttonMoveX.setEnabled(false);
                setSpeed = binding.editTextTextSpeedX.getText().toString();
                dSpeed = Double.valueOf(setSpeed);

                if (cmdstr.equals("moveAcc") == true && //NEW CMD 가 전진인데
                        (strCmdOld.equals("moveAcc") == true || strCmdOld.equals("moveDec") == true)) //방향전환  ) //방향전환
                {

                    if (dSpeed < 2.0) {
                        dSpeed = dSpeed + 0.02;
                    }
                    else {
                        bSendingData = false;
                        return;
                    }

                }
                else if(strCmdOld.equals("") == true)//정지후 처음 기동
                {
                    if (dSpeed < 2.0) {
                        dSpeed = dSpeed + 0.02;
                    }
                    else {
                        bSendingData = false;
                        return;
                    }
                }
                else {
                    if (cmdstr.equals("moveDec") == true  && dSpeed != 0.0)
                    {
                        dSpeed = dSpeed * (-1);
                    }
                }

                //턴 후 직진시  z 값 0.0 =>진직 설정
                if(strCmdOld.equals("turnL") || strCmdOld.equals("turnR"))
                {
                    // 정지시 z 속도 0.0 으로 강체 셋팅
                    dSpeedZ = 0.00;//
                    strSendDataZ = String.format("speedz:%S:", dSpeedZ);
                    dispDataZ = String.format("%.2f", dSpeedZ);
                    editTextSpeedZ.setText(dispDataZ);

                    // 데이터 설정
                    Constants.sharedViewModel.setDataZ(strSendDataZ);
                    sendSpeedDataZ();
                    //sleep(100);
                }

                // 직진 이동시  x ++
                strSendData = String.format("speedx:%S:", dSpeed);
                dispData = String.format("%.2f", dSpeed);
                editTextSpeedX.setText(dispData);

                cmdstr = "moveAcc";
                // 데이터 설정
                Constants.sharedViewModel.setDataX(strSendData);
                sendSpeedDataX();

                strCmdOld = cmdstr;

                break;

            case "moveDec":
                binding.buttonMoveX.setEnabled(false);
                setSpeed = binding.editTextTextSpeedX.getText().toString();
                dSpeed = Double.valueOf(setSpeed);

                if (cmdstr.equals("moveDec") == true && //NEW CMD 가 감속인데
                        (strCmdOld.equals("moveAcc") == true || strCmdOld.equals("moveDec") == true)) //방향전환
                {
                    if (dSpeed > -2.0) {
                        dSpeed = dSpeed - 0.02;
                    } else return;

                }
                else if(strCmdOld.equals("") == true)//정지후 처음 기동
                {
                    if (dSpeed > -2.0) {
                        dSpeed = dSpeed - 0.02;
                    }
                    else {
                        bSendingData = false;
                        return;
                    }

                }
                else {
                    if (cmdstr.equals("moveAcc") == true  && dSpeed != 0.0)
                    {
                        dSpeed = dSpeed * (-1);
                    }
                }

                //턴 후 직진시  z 값 0.0 =>진직 설정
                if(strCmdOld.equals("turnL") || strCmdOld.equals("turnR"))
                {
                    // 정지시 z 속도 0.0 으로 강체 셋팅
                    dSpeedZ = 0.00;//
                    strSendDataZ = String.format("speedz:%S:", dSpeedZ);
                    dispDataZ = String.format("%.2f", dSpeedZ);
                    editTextSpeedZ.setText(dispDataZ);

                    // 데이터 설정
                    Constants.sharedViewModel.setDataZ(strSendDataZ);
                    sendSpeedDataZ();
                    //sleep(100);
                }

                // 직진 이동시  x --
                strSendData = String.format("speedx:%S:", dSpeed);
                dispData = String.format("%.2f", dSpeed);
                editTextSpeedX.setText(dispData);

                cmdstr = "moveDec";
                // 데이터 설정
                Constants.sharedViewModel.setDataX(strSendData);
                sendSpeedDataX();

                strCmdOld = cmdstr;

                break;

            case "movestop":
                strCmdOld = ""; //이전 방향 초기화

                // 직진 이동시  x --
                strSendData = String.format("speedx:%S:", 0.0);
                dispData = String.format("%.2f", 0.0);
                editTextSpeedX.setText(dispData);
                // 데이터 설정
                //Constants.sharedViewModel.setDataX(strSendData);
                //sendSpeedDataX();

                //sleep(100);

                // 정지시 z 속도 0.0 으로 강체 셋팅
                dSpeed = 0.00;//
                strSendDataZ = String.format("speedz:%S:", dSpeed);
                dispDataZ = String.format("%.2f", dSpeed);
                editTextSpeedZ.setText(dispDataZ);
                //Constants.sharedViewModel.setDataZ(strSendDataZ);
                //sendSpeedDataZ();

                cmdstr = "movestop";
                //Constants.sharedViewModel.setData(cmdstr);
                sendStringCmd(cmdstr);

                break;

            default:
                // 데이터 설정
                //Constants.sharedViewModel.setData(cmdstr);
                //sendStringCmd(cmdstr);

        }

        bSendingData = false;

    }

    void sendCmd(String cmdstr) throws InterruptedException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    putRunable(cmdstr);
                    //waitting to sending done
                    while(true) {
                        if (bSendingData == false) {
                            break;
                        } else sleep(50);
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        runnable.run();

    }

    void sendSpeedDataX() {

        if(Constants.BTCommTestFragment == null) return;

        Constants.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String setSpeed = binding.editTextTextSpeedX.getText().toString();
        String strSendData = String.format("speedx:%S:", setSpeed);
        // 데이터 설정
        Constants.sharedViewModel.setData(strSendData);
        Constants.BTCommTestFragment.sendTypeCmd("speedX");

    }
    void sendSpeedDataZ() {

        if(Constants.BTCommTestFragment == null) return;

        Constants.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String setSpeed = binding.editTextTextSpeedZ.getText().toString();
        String strSendData = String.format("speedz:%S:", setSpeed);
        // 데이터 설정
        Constants.sharedViewModel.setDataZ(strSendData);
        Constants.BTCommTestFragment.sendStringCmd(strSendData);
        //Constants.test.sendTypeCmd("speedZ");

    }

    void sendStringCmd(String cmdStr)
    {
        if(Constants.BTCommTestFragment == null) return;

        Constants.BTCommTestFragment.sendStringCmd(cmdStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Constants.mainActivity.showFab();

        //tab change
        Constants.mainActivity.changeFagmentTest();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}