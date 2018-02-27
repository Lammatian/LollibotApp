package com.alliedtech.lollibotapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
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
import java.util.UUID;

//TODO: Fix handler messages mess
//TODO: Fix mess in general
//TODO: Documentation
public class BluetoothService extends Service {

    private static final String TAG = "BLUETOOTH SERVICE";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> bondedDevices;
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesMacs;
    private ArrayList<Integer> deviceSignalStrength;
    private Context context;
    private final LocalBinder binder = new LocalBinder();

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device
    private int mState;

    //region Testing
    private String test = "test1";

    public String getTest() {
        return test;
    }

    public void change() {
        test = (test.equals("test1")) ? "test2" : "test1";
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("Ayyy", 0);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }
    //endregion

    //region Overridden methods
    @Override
    public void onCreate() {
        super.onCreate();

        devicesNames = new ArrayList<>();
        devicesMacs = new ArrayList<>();
        deviceSignalStrength = new ArrayList<>();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        context = getApplicationContext();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        scan();
        mHandler.obtainMessage(Constants.MESSAGE_SCANNED).sendToTarget();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
    //endregion

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    boolean isAvailable() {
        return !(btAdapter == null || !(btAdapter.isEnabled()));
    }

    ArrayList<String> getDevicesNames() {
        return devicesNames;
    }
    ArrayList<String> getDevicesMacs() {
        return devicesMacs;
    }
    ArrayList<Integer> getDevicesSignals() {
        return deviceSignalStrength;
    }

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

                    Message msg = mHandler.obtainMessage(Constants.MESSAGE_NEW_DEVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DEVICE_NAME, device.getName());
                    bundle.putString(Constants.DEVICE_MAC, device.getAddress());
                    bundle.putInt(Constants.DEVICE_SIGNAL, rssi);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    private void scan() {
        devicesNames = new ArrayList<>();
        devicesMacs = new ArrayList<>();
        deviceSignalStrength = new ArrayList<>();

        bondedDevices = new ArrayList<>(btAdapter.getBondedDevices());

        for (BluetoothDevice device : bondedDevices) {
            devicesNames.add(device.getName());
            devicesMacs.add(device.getAddress());
            deviceSignalStrength.add(100);
        }

        btAdapter.startDiscovery();
        context.registerReceiver(btReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void connect(int index) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(bondedDevices.get(index));
        mConnectThread.start();
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket, device);
        mConnectedThread.start();
    }

    //region Connection failure handlers
    public void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
    }

    public void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
    }
    //endregion

    //region Sending data
    public void write(String command) {
        ConnectedThread t;

        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            t = mConnectedThread;
        }

        t.write(("[" + command + "]").getBytes());
    }

    public void write(String command, String argument) {
        ConnectedThread t;

        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            t = mConnectedThread;
        }

        t.write(("[" + command + "*" + argument + "*]").getBytes());
    }
    //endregion

    //region Connect thread
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            //TODO: Pairing
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
            mState = STATE_CONNECTING;
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
                connectionFailed();
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            Log.d(TAG, "Connected to " + mmSocket.toString());
            connected(mmSocket, mmDevice);
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
    //endregion

    //region Connected thread
    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //TODO: Device may not be necessary but will be here for now
        private final BluetoothDevice mmDevice;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket, BluetoothDevice device) {
            mmDevice = device;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;

            // Inform the application about connection being established
            mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        // Call this method from the main activity to shut down the connection.
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
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