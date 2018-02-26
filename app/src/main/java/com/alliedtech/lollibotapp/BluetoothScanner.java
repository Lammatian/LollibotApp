package com.alliedtech.lollibotapp;

//Basically has methods to handle the bluetooth, and return all devices etc, seperate because easier to test I think

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothScanner {
    private BluetoothAdapter btAdapter;
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesMacs;
    private ArrayList<Integer> deviceSignalStrength;
    private Context context;

    BluetoothScanner(Context context) {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        scan();
    }

    //Method to know if bluetooth is available
    boolean isAvailable() {
        return !(btAdapter == null || !(btAdapter.isEnabled()));
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
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
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
                }
            }
        }
    };

    //Gets all bluetooth devices, and the btReceiver adds all the devices to the arraylist
    private void scan() {
        Set<BluetoothDevice> btArrayList = btAdapter.getBondedDevices();

        for (BluetoothDevice device : btArrayList) {
            devicesNames.add(device.getName());
            devicesMacs.add(device.getAddress());
            deviceSignalStrength.add(100);
        }

        btAdapter.startDiscovery();
        context.registerReceiver(btReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }
}