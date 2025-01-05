package com.CJI.bt_ros_comm_test.ui.device;

import com.CJI.bt_ros_comm_test.Constants;
import com.CJI.bt_ros_comm_test.R;
import com.CJI.bt_ros_comm_test.databinding.FragmentBtcommTestBinding;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayDeque;
import android.content.ServiceConnection;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unchecked")
public class BTCommTestFragment extends Fragment implements ServiceConnection, SerialListener {

    private FragmentBtcommTestBinding binding;
    private enum Connected {False, Pending, True}
    private BTCommTestFragment btCommTestFragment;

    private TextView receiveText;
    private TextView sendText;
    private TextUtil.HexWatcher hexWatcher;

    private static BTCommTestFragment.Connected connected = BTCommTestFragment.Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    private boolean isShow = false;
    private View sendBtn;
    private static SerialSocket socket;

    BTCommTestViewModel BTCommTestViewModel;;

    TimerTask timerTask;
    Timer timer = new Timer();
    private int CONNECT_RETRY = 5;
    private int connect_count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Constants.BTCommTestFragment = this;
    }

    /* UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btcomm_test, container, false);

        btCommTestFragment = this;

        BTCommTestViewModel =
                new ViewModelProvider(this).get(BTCommTestViewModel.class);


        binding = FragmentBtcommTestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        receiveText = binding.receiveText.findViewById(R.id.receive_text);

        final TextView textView = binding.sendText;//.send_text;
        BTCommTestViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final ImageButton sendBtn = binding.sendBtn2;
        sendBtn.setOnClickListener(v -> sendStringCmd(textView.getText().toString()));
        //receiveText = binding.receiveText.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        //receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        //receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        // ViewModel 공유
        Constants.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        TextView textView3 = binding.receiveText.findViewById(R.id.textView);




       // 데이터 감시
       Constants.sharedViewModel.getData().observe(getViewLifecycleOwner(), new Observer<String>() {
           @Override
           public void onChanged(String s) {
               textView.setText(s);
           }
       });


       return root;

   }


   @Override
   public void onDestroyView() {

      super.onDestroyView();
   }

    @Override
    public void onResume() {
        super.onResume();
        isShow = true;

        if(Constants.bluetooth_on == false)
        {
            status( "BT device is OFF");
            return;
        } else {

            if (initialStart) status("BT initialize...");
            if (initialStart && Constants.service != null) {

                if (Constants.deiveAddressString != null && connected == BTCommTestFragment.Connected.False)
                    getActivity().runOnUiThread(this::connect);
            } else {
                if (initialStart) {
                    if (Constants.deiveAddressString == null) {
                        status("BT device address : " + "null");
                    } else {
                        status("BT device name : " + Constants.deiveAddressName);
                    }

                    if (Constants.deiveAddressString == null) {
                        status("Select BT device that paired");
                    } else {
                        status("BT device address : " + Constants.deiveAddressString);
                    }
                }
            }
        }

        initialStart = false;
        Constants.mainActivity.showFab();

        if (Constants.deiveAddressString != null && Constants.deiveAddressString != null){
            //timer start
           if( connected == BTCommTestFragment.Connected.False) startTimerTask();
        }

    }


    private void startTimerTask()
    {
        stopTimerTask();

        timerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                //connect retry
                if(Constants.service != null) {
                    status( "BT Connect...");
                    if(Constants.deiveAddressString!= null && connected == BTCommTestFragment.Connected.False)
                        getActivity().runOnUiThread(this::connect);

                    connect_count ++;

                    if(connect_count > CONNECT_RETRY){
                        stopTimerTask();
                    }
                }

            }

            private void connect() {
                btCommTestFragment.connect();
            }
        };
        timer.schedule(timerTask,0 ,1000);
    }

    private void stopTimerTask()
    {
        if(timerTask != null)
        {
            connect_count=0;
            timerTask.cancel();
            timerTask = null;
            //status("BT connecting stop");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(Constants.bluetooth_on == false)
        {
            status( "BT device is OFF");
            return;
        } else {
            if (Constants.service != null)
                Constants.service.attach(this);
            else
                requireActivity().startService(new Intent(getContext(), SerialService.class));
            //getActivity().startService(mActivity, SerialService.class); // prevents service destroy on unbind from recreated activity caused by orientation change
        }
    }

    @Override
    public void onStop() {
        if(Constants.service != null && !getActivity().isChangingConfigurations())
        {
            //Constants.service.detach();
            //service = null;
        }
        isShow = false;
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(Constants.service == null)
            requireActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        //getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);

    }

/*
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if(Constants.service == null)
            requireActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        //getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }
*/
    @Override
    public void onDetach() {
        //try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();

    }


    public void connect() {
      try {

          if(Constants.bluetooth_on == false)
          {
              status( "BT device is OFF");
              return;
          }
         else if(Constants.deiveAddressString== null)
         {
             status( "BT device address : " + "null");
             return;
         }

         BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(Constants.deiveAddressString);
          if(isShow)
              status("BT server: connecting...");
         connected = BTCommTestFragment.Connected.Pending;
         if(socket == null) {
             socket = new SerialSocket(getActivity().getApplicationContext(), device);
             Constants.serialsocket = socket;
         }
          Constants.service.connect(socket);
      } catch (Exception e) {
         onSerialConnectError(e);
      }
   }

    public void disconnect() {
      connected = BTCommTestFragment.Connected.False;
       Constants.service.disconnect();
        if(isShow)
            status("BT server: disconnected ...");

        // 데이터 설정
        if(Constants.sharedViewModel != null) Constants.sharedViewModel.setDataBTStatus("disconnect");
   }
   private void status(String str) {
        if(isShow) {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
        }
   }
   public void sendStringCmd(String str) {

       if(Constants.BTCommTestFragment == null) return;

      if(connected != BTCommTestFragment.Connected.True) {
        // Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
        if(isShow)
            status("BT server: not connected" );
         return;
      }

       status("send data: " + str);
       //final TextView textView2;
       //TESTYViewModel.getTex2().observe(getViewLifecycleOwner(), textView2::setText);
       //textView2 = TESTYViewModel.getTex2();
      try {
         String msg;
         byte[] data;
         if(hexEnabled) {
            StringBuilder sb = new StringBuilder();
            TextUtil.toHexString(sb, TextUtil.fromHexString(str));
            TextUtil.toHexString(sb, newline.getBytes());
            msg = sb.toString();
            data = TextUtil.fromHexString(msg);
         } else {
            msg = str;
            data = (str + newline).getBytes();
         }
         //SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
         //spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
         //receiveText.append(spn);
          Constants.service.write(data);
      } catch (Exception e) {
         onSerialIoError(e);
      }
   }
    public void sendTypeCmd(String strType) {

        if(Constants.BTCommTestFragment == null) return;

        String str = "";

        if(connected != BTCommTestFragment.Connected.True) {
            // Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            if(isShow)
                status("BT server:not connected" );
            return;
        }
        if(strType.equals("speedX"))
        {
            str = Constants.sharedViewModel.getDataX().getValue();
        }
        else if(strType.equals("speedZ")) {
            str = Constants.sharedViewModel.getDataZ().getValue();
        } else return;

        //final TextView textView2;
        //TESTYViewModel.getTex2().observe(getViewLifecycleOwner(), textView2::setText);
        //textView2 = TESTYViewModel.getTex2();
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            //SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            //spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //receiveText.append(spn);
            Constants.service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

   private void receive(ArrayDeque<byte[]> datas) {
      SpannableStringBuilder spn = new SpannableStringBuilder();
      for (byte[] data : datas) {
         if (hexEnabled) {
            spn.append(TextUtil.toHexString(data)).append('\n');
         } else {
            String msg = new String(data);
            if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
               // don't show CR as ^M if directly before LF
               msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
               // special handling if CR and LF come in separate fragments
               if (pendingNewline && msg.charAt(0) == '\n') {
                  if(spn.length() >= 2) {
                     spn.delete(spn.length() - 2, spn.length());
                  } else {
                     Editable edt = receiveText.getEditableText();
                     if (edt != null && edt.length() >= 2)
                        edt.delete(edt.length() - 2, edt.length());
                  }
               }
               pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
         }
      }
      receiveText.append(spn);
   }

   private void showNotificationSettings() {
      Intent intent = new Intent();
      intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
      intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
      startActivity(intent);
   }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
        Constants.service = ((SerialService.SerialBinder) binder).getService();
        Constants.service.attach(this);
       if(initialStart && isResumed()) {
          initialStart = false;
          if(Constants.deiveAddressString!= null)
             getActivity().runOnUiThread(this::connect);
       }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Constants.service = null;
        //timer start
        startTimerTask();
    }

    @Override
    public void onSerialConnect() {
        if(isShow)
            status("BT server: connected");
       connected = BTCommTestFragment.Connected.True;
       //timer stop
        stopTimerTask();

        // 데이터 설정
        if(Constants.sharedViewModel != null) Constants.sharedViewModel.setDataBTStatus("connect");
    }

    @Override
    public void onSerialConnectError(Exception e) {
        if(isShow)
            status("BT server: connection failed: " + e.getMessage());
       disconnect();

        // 데이터 설정
        if(Constants.sharedViewModel != null) Constants.sharedViewModel.setDataBTStatus("disconnect");
    }

    @Override
    public void onSerialRead(byte[] data) {
       ArrayDeque<byte[]> datas = new ArrayDeque<>();
       datas.add(data);
       receive(datas);
    }

    @Override
    public void onSerialRead(ArrayDeque<byte[]> datas) {
       receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        if(isShow)
            status("BT server: connection lost: " + e.getMessage());
       disconnect();

        // 데이터 설정
        if(Constants.sharedViewModel != null) Constants.sharedViewModel.setDataBTStatus("disconnect");
    }
}