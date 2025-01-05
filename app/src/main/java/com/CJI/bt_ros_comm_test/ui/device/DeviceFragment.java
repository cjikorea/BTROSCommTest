package com.CJI.bt_ros_comm_test.ui.device;

import com.CJI.bt_ros_comm_test.Constants;
import com.CJI.bt_ros_comm_test.R;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.CJI.bt_ros_comm_test.databinding.FragmentDeviceBinding;
import androidx.fragment.app.ListFragment;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unchecked")
public class DeviceFragment extends ListFragment {//

    private FragmentDeviceBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    ActivityResultLauncher<String> requestBluetoothPermissionLauncherForRefresh;
    private Menu menu;
    private boolean permissionMissing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, listItems) {
            @NonNull
            @Override
            public View getView(int position, View view, @NonNull ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                text1.setText(deviceName);
                text2.setText(device.getAddress());
                return view;
            }
        };

        requestBluetoothPermissionLauncherForRefresh = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> BluetoothUtil.onPermissionsResult(this, granted, this::refresh));


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        View header = getActivity().getLayoutInflater().inflate(R.layout.device_list_header, null, false);
        getListView().addHeaderView(header, null, false);
        setEmptyText("initializing...");
        ((TextView) getListView().getEmptyView()).setTextSize(18);
        setListAdapter(listAdapter);

        if (BluetoothUtil.hasPermissions(this, requestBluetoothPermissionLauncherForRefresh))
            refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

        Constants.mainActivity.hideFab();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*
        if (id == R.id.btn_settings) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
            return true;
        } else */
        if (id == R.id.action_settings) {
            if (BluetoothUtil.hasPermissions(this, requestBluetoothPermissionLauncherForRefresh))
                refresh();
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }


    @SuppressLint("MissingPermission")
    void refresh() {
        listItems.clear();
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionMissing = getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED;
                //if(menu != null && menu.findItem(R.id.bt_refresh) != null)
                //    menu.findItem(R.id.bt_refresh).setVisible(permissionMissing);
            }
            if (!permissionMissing) {
                for (BluetoothDevice device : bluetoothAdapter.getBondedDevices())
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE)
                        listItems.add(device);
                Collections.sort(listItems, BluetoothUtil::compareTo);
            }
        }

        if (bluetoothAdapter == null)
            setEmptyText("<bluetooth not supported>");
        else if (!bluetoothAdapter.isEnabled())
            setEmptyText("<bluetooth is disabled>");
        else if (permissionMissing)
            setEmptyText("<permission missing, use REFRESH>");
        else
            setEmptyText("<no bluetooth devices found>");

        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        BluetoothDevice device = listItems.get(position - 1);
        Bundle args = new Bundle();
        args.putString("device", device.getAddress());

        Constants.deiveAddressString = device.getAddress();
        if (getActivity().checkSelfPermission( Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String deviceName = device.getName();
        Constants.sharedViewModel.setDataBT(deviceName);
        //getParentFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit();


        SharedPreferences.Editor editor =  Constants.sharedPreferences.edit();//Constants.mainActivity.getPreferences(Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.bluetooth_device_name), device.getName());
        editor.putString(getString(R.string.bluetooth_device_address), device.getAddress());

        editor.apply();


    }


}