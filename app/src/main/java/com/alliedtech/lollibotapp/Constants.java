package com.alliedtech.lollibotapp;

//TODO: Clean up message constants
public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_CONNECTED = 0;
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_SCANNED = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int MESSAGE_NEW_DEVICE = 6;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String DEVICE_MAC = "device_mac";
    String DEVICE_SIGNAL = "device_signal";
    String TOAST = "toast";

    // Battery level
    // TODO: Change to proper battery level
    double MAX_VOLTAGE = 8.3;
}
