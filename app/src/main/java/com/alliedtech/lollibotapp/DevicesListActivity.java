package com.alliedtech.lollibotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Home Screen Activity, looks very basic atm but it

public class DevicesListActivity extends AppCompatActivity {

    ListView viewOfAllDevices;
    BluetoothScanner btScanner;
    ArrayAdapter<String> adapterOfDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        viewOfAllDevices = findViewById(R.id.devicesList);
        btScanner = new BluetoothScanner(this);

        if (btScanner.isAvailable()) {
            adapterOfDevices = btScanner.allDevices();
            viewOfAllDevices.setAdapter(adapterOfDevices);
        }
        else {
            Toast.makeText(getApplicationContext(),"Bluetooth not available",Toast.LENGTH_SHORT).show();
        }

    }
    public void reScan(View v) {
        if (btScanner.isAvailable()) {
            adapterOfDevices = btScanner.allDevices();
            viewOfAllDevices.setAdapter(adapterOfDevices);
        }
        else {
            Toast.makeText(getApplicationContext(),"Bluetooth not available",Toast.LENGTH_SHORT).show();
        }

    }
}
