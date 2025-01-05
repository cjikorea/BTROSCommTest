package com.CJI.bt_ros_comm_test.ui.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.CJI.bt_ros_comm_test.Constants;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

public class SerialSocket implements Runnable {

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BroadcastReceiver disconnectBroadcastReceiver;

    private final Context context;
    private SerialListener listener;
    private final BluetoothDevice device;
    private BluetoothSocket socket;
    private boolean connected;

    SerialSocket(Context context, BluetoothDevice device) {
        if (context instanceof Activity)
            throw new InvalidParameterException("expected non UI context");
        this.context = context;
        this.device = device;
        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (listener != null)
                    listener.onSerialIoError(new IOException("background disconnect"));
                disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }

    String getName() {

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String strName = device.getName();
        String strAddress = device.getAddress();

        return strName != null ? strName : strAddress;
    }

    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */
    void connect(SerialListener listener) throws IOException {
        this.listener = listener;
        ContextCompat.registerReceiver(context, disconnectBroadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DISCONNECT), ContextCompat.RECEIVER_NOT_EXPORTED);
        Executors.newSingleThreadExecutor().submit(this);
    }

    void disconnect() {
        listener = null; // ignore remaining data and errors
        // connected = false; // run loop will reset connected
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
        try {
            context.unregisterReceiver(disconnectBroadcastReceiver);
        } catch (Exception ignored) {
        }
    }

    void write(byte[] data) throws IOException {
        if (!connected)
            throw new IOException("not connected");
        socket.getOutputStream().write(data);
    }

    @Override
    public void run() { // connect & read
        try {
            if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
            if(listener != null)
                listener.onSerialConnect();
        } catch (Exception e) {
            if(listener != null)
                listener.onSerialConnectError(e);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
            return;
        }
        connected = true;
        try {
            byte[] buffer = new byte[1024];
            int len;
            //noinspection InfiniteLoopStatement
            while (true) {
                len = socket.getInputStream().read(buffer);
                byte[] data = Arrays.copyOf(buffer, len);
                if(listener != null)
                    listener.onSerialRead(data);
            }
        } catch (Exception e) {
            connected = false;
            if (listener != null)
                listener.onSerialIoError(e);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

}
