package com.alliedtech.lollibotapp;

//Basically has methods to handle the bluetooth, and return all devices etc, seperate because easier to test I think

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BluetoothScanner {
    private BluetoothAdapter btAdapter;
    private boolean blueToothEnabled; //Boolean to check if bluetooth is enabled or not
    private ArrayList<BluetoothDevice> scannedDevicesArraylist; //ArrayList to store all the devices
    private ArrayList<String> devicesNameAndMac; //To be changed when custom adapter made, for now just stores devices name and MAC
    private ArrayAdapter<String> scannedDevicesAdapter; //To be changed to a custom adapter for more customizability
    private Context context;

    BluetoothScanner(Context context) {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        blueToothEnabled = !(btAdapter == null || !(btAdapter.isEnabled()));
        scannedDevicesArraylist = new ArrayList<>();
        devicesNameAndMac = new ArrayList<>();
        scannedDevicesAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1, devicesNameAndMac);

    }

    //Method to know if bluetooth is available
    boolean isAvailable() {
        return blueToothEnabled;
    }

    //Method to return all devices scannable, whether bonded or not (Adapater has string with name and MAC address)
    ArrayAdapter<String> allDevices() {
        scan();
        scannedDevicesAdapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_list_item_1, devicesNameAndMac);
        return scannedDevicesAdapter;
    }

    //This is the receiver that basically returns all devices whether bonded or not
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE); //Signal Strength measured in dBm
            // add the name to the list
            scannedDevicesArraylist.add(device);
            //Terrible way to add it, but for now just doing this (More negative RSSI means more strength)
            devicesNameAndMac.add("Name: " + device.getName() + ", MAC address: " + device.getAddress() + ", Signal Strength : " + rssi);
        }

    };

    //Gets all bluetooth devices, and the blReceiver adds all the devices to the arraylist
    private void scan() {
       btAdapter.startDiscovery();
       context.registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

    }
    public ArrayList getDevices() {
        return this.scannedDevicesArraylist;
    }



}
