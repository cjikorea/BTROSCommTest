package com.CJI.bt_ros_comm_test;

import android.Manifest;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.CJI.bt_ros_comm_test.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.sharedPreferences = getSharedPreferences("InfoDeviceAddress", MODE_PRIVATE);
        String readDeviceAddress = Constants.sharedPreferences.getString(getString(R.string.bluetooth_device_address), "");
        String readDeviceName = Constants.sharedPreferences.getString(getString(R.string.bluetooth_device_name), "");

        Constants.mainActivity = this;

        //bluetooth on check
        Constants.bluetooth_on = blueToothOffCheck();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_TEST, R.id.nav_home, R.id.nav_device, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //saved device address
        if(readDeviceAddress.equals("") != true)
            Constants.deiveAddressString = readDeviceAddress;

        //saved ddvice name
        if(readDeviceName.equals("") != true) {
            Constants.deiveAddressName = readDeviceName;
            Constants.sharedViewModel.setDataBT(readDeviceName);
        }

        binding.appBarMain.fabBtOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bluetooth connect...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab_bt_on).show();

                if(Constants.BTCommTestFragment != null)
                    Constants.BTCommTestFragment.connect();


            }
        });
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fabBtOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bluetooth Disconnect...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab_bt_off).show();

                if(Constants.BTCommTestFragment != null)
                    Constants.BTCommTestFragment.disconnect();
            }
        });

        hideFab();
    }

    private boolean blueToothOffCheck()
    {
        //Disable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled() == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }

            Toast.makeText(this, "BlueTooth is OFF Now", Toast.LENGTH_SHORT).show();
            //mBluetoothAdapter.disable();
            return false;

        }
        return  true;

    }
    public  void changeFagmentTest()
    {
        View view = navigationView.findViewById(R.id.nav_TEST);
       // View view = navigationView.getMenu().findItem(R.id.nav_TEST).getActionView();
        //navigationView.getMenu().findItem(R.id.nav_TEST).
        //((MainActivity)getActivity()).ChangeActionbar();
        //NavigationUI.findBottomSheetBehavior().onNavDestinationSelected()
       // navController.

       //AppCompatActivity activity = (AppCompatActivity) getActivity();
       // ActionBar actionBar = activity.getSupportActionBar();
       // mAppBarConfiguration..setTitle(R.string.my_fragment_title);
        //binding.navView.

        binding.appBarMain.toolbar.inflateMenu(R.menu.activity_main_drawer);
        //MenuItem saveItem = binding.appBarMain.toolbar.getMenu().findItem(R.id.nav_TEST);
        //saveItem.setVisible(true);
        //saveItem.expandActionView();
        //saveItem.setChecked(true);
        //saveItem.setActionView(view);

        ActionBar actionBar = getActionBar();
        //binding.appBarMain.toolbar.setTitle("com.CJI.bt_ros_comm_test");
        //actionBar.(R.id.nav_TEST);

    }

    public void showFab()
    {
        binding.appBarMain.fabBtOff.show();
        binding.appBarMain.fabBtOn.show();
    }

    public void hideFab()
    {
        binding.appBarMain.fabBtOff.hide();
        binding.appBarMain.fabBtOn.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}