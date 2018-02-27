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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public AppCompatActivity thisActivity = this;
    boolean mBounded;
    BluetoothService mService;

    public void change(View view) {
        mService.change();
    }

    public void show(View view) {
        Toast.makeText(getApplicationContext(), mService.getTest(), Toast.LENGTH_SHORT).show();
    }

    public void move(View view) {
        mService.write(RobotCommand.COMMAND_MOVE_LINES);
    }

    public void changeActivity() {
        Intent intent = new Intent(MainActivity.this, DevicesListActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = thisActivity;
            switch (msg.what) {
                case 0:
                    changeActivity();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "Heyyy", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            BluetoothService.LocalBinder mLocalBinder = (BluetoothService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();
            mService.setHandler(mHandler);
        }
    };
}
