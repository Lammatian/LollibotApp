package com.alliedtech.lollibotapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {

    private static final String TAG = "BLUETOOTH SERVICE";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothAdapter btAdapter;
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesMacs;
    private ArrayList<Integer> deviceSignalStrength;
    private Context context;
    private final LocalBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        scan();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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
                // Signal Strength measured in dBm (the higher the better)
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
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

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    //region Connection thread
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private byte[] btBuff;

        public ConnectedThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(
                        UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            btAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        public void write(byte[] buffer) {
            try {
                mmSocket.getOutputStream().write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(AbstractActivity.MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e("PrinterService", "Exception during write", e);
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
    //endregion


    class LocalBinder extends Binder {
        BluetoothService getServerInstance() {
            return BluetoothService.this;
        }
    }
}