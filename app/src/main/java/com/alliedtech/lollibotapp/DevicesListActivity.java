package com.alliedtech.lollibotapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alliedtech.lollibotapp.adapters.DeviceListAdapter;

import java.util.ArrayList;

public class DevicesListActivity extends AppCompatActivity {

    boolean mBounded;
    BluetoothService mService;

    public ListView viewOfAllDevices;
    public DeviceListAdapter deviceListAdapter;
    ArrayList<String> deviceNames;
    ArrayList<String> deviceMacs;
    ArrayList<Integer> deviceScans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        viewOfAllDevices = findViewById(R.id.devicesList);
        deviceNames = new ArrayList<>();
        deviceMacs = new ArrayList<>();
        deviceScans = new ArrayList<>();

        viewOfAllDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mService.connect(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConnection);
    }

    private void onServiceConnected() {
        startService(new Intent(this, BluetoothService.class));
    }

    public void showBondedDevices() {
        deviceMacs = mService.getDevicesMacs();
        deviceNames = mService.getDevicesNames();
        deviceScans = mService.getDevicesSignals();

        deviceListAdapter = new DeviceListAdapter(deviceNames, deviceMacs, deviceScans, this);
        viewOfAllDevices.setAdapter(deviceListAdapter);
    }

    public void reScan(View v) { }

    public void addNewDevice(Bundle data) {
        String name = data.getString(Constants.DEVICE_NAME);
        String mac = data.getString(Constants.DEVICE_MAC);
        int signal = data.getInt(Constants.DEVICE_SIGNAL);

        deviceNames.add(name);
        deviceMacs.add(mac);
        deviceScans.add(signal);

        deviceListAdapter.notifyDataSetChanged();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_SCANNED:
                    showBondedDevices();
                    break;
                case Constants.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == 2)
                        startActivity(new Intent(getApplicationContext(), DeviceActivity.class));
                    break;
                case Constants.MESSAGE_NEW_DEVICE:
                    addNewDevice(msg.getData());
                    break;
            }
        }
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(DevicesListActivity.this,
                    "Service is disconnected",
                    Toast.LENGTH_SHORT).show();
            mBounded = false;
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(DevicesListActivity.this,
                    "Service is connected",
                    Toast.LENGTH_SHORT).show();
            mBounded = true;
            BluetoothService.LocalBinder mLocalBinder = (BluetoothService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();
            mService.setHandler(mHandler);
            DevicesListActivity.this.onServiceConnected();
        }
    };
}
