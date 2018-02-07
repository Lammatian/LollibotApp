package com.alliedtech.lollibotapp;

//Basically has methods to handle the bluetooth, and return all devices etc, seperate because easier to test I think

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Set;

class BluetoothScanner {
    private BluetoothAdapter btAdapter;
    private boolean blueToothEnabled; //Boolean to check if bluetooth is enabled or not
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesMacs;
    private ArrayList<Integer> deviceSignalStrength;
    private boolean scanning;
    private Context context;

    BluetoothScanner(Context context) {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        blueToothEnabled = !(btAdapter == null || !(btAdapter.isEnabled()));
        devicesNames = new ArrayList<>();
        devicesMacs = new ArrayList<>();
        deviceSignalStrength = new ArrayList<>();

        scan();

    }

    //Method to know if bluetooth is available
    boolean isAvailable() {
        return blueToothEnabled;
    }

    //Method to return all devices scannable, whether bonded or not (Adapater has string with name and MAC address)
    ArrayList<String> getDevicesNames() {
        return devicesNames;
    }
    ArrayList<String> getDevicesMacs() {
        return devicesMacs;
    }
    ArrayList<Integer> getDevicesSignals() {
        return deviceSignalStrength;
    }

    //This is the receiver that basically returns all devices whether bonded or not
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE); //Signal Strength measured in dBm
                //(More positive RSSI means more strength)
                if (!devicesMacs.contains(device.getAddress()) && (device.getName() != null)) {
                    devicesNames.add(device.getName());
                    devicesMacs.add(device.getAddress());
                    deviceSignalStrength.add(rssi);
                    DevicesListActivity.deviceListAdapter.notifyDataSetChanged();
                }
            }

        }

    };

    //Gets all bluetooth devices, and the blReceiver adds all the devices to the arraylist
    private void scan() {
        Set<BluetoothDevice> btArrayList = btAdapter.getBondedDevices();

        for (BluetoothDevice device : btArrayList) {
            devicesNames.add(device.getName());
            devicesMacs.add(device.getAddress());
            deviceSignalStrength.add(100);
        }
       btAdapter.startDiscovery();
       context.registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));


    }



}
