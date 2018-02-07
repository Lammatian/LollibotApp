package com.alliedtech.lollibotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//Home Screen Activity, looks very basic atm but it'll be better

public class DevicesListActivity extends AppCompatActivity {

   public static ListView viewOfAllDevices;
    BluetoothScanner btScanner;
    public static DeviceListAdapter deviceListAdapter;
    ArrayList<String> deviceNames;
    ArrayList<String> deviceMacs;
    ArrayList<Integer> deviceScans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        viewOfAllDevices = findViewById(R.id.devicesList);
        btScanner = new BluetoothScanner(this);
        deviceNames = new ArrayList<>();
        deviceMacs = new ArrayList<>();
        deviceScans = new ArrayList<>();

        deviceNames  = btScanner.getDevicesNames();
        deviceMacs = btScanner.getDevicesMacs();
        deviceScans = btScanner.getDevicesSignals();
        if (btScanner.isAvailable()) {
            deviceListAdapter = new DeviceListAdapter(deviceNames,deviceMacs,deviceScans,this);
            viewOfAllDevices.setAdapter(deviceListAdapter);
        }
        else {
            Toast.makeText(getApplicationContext(),"Bluetooth not available",Toast.LENGTH_SHORT).show();
        }

        viewOfAllDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Can pass MAC address as Intent, since thats mostly what we all need for the EV3, UUID's will be same for all EV3 (We'll set it up to be)
            }
        });

    }
    public void reScan(View v) {
        if (btScanner.isAvailable()) {
            deviceNames.clear();
            deviceMacs.clear();
            deviceScans.clear();
            deviceListAdapter.notifyDataSetChanged();
            btScanner = new BluetoothScanner(getApplicationContext());
            deviceNames = btScanner.getDevicesNames();
            deviceMacs = btScanner.getDevicesMacs();
            deviceScans = btScanner.getDevicesSignals();
            deviceListAdapter = new DeviceListAdapter(deviceNames,deviceMacs,deviceScans,this);
            viewOfAllDevices.setAdapter(deviceListAdapter);
        }
        else {
            Toast.makeText(getApplicationContext(),"Bluetooth not available",Toast.LENGTH_SHORT).show();
        }
    }
}
