package com.alliedtech.lollibotapp;

public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_CONNECTED = 0;
    int MESSAGE_DISCONNECTED = -1;
    int MESSAGE_CONNECTION_FAILED = -2;
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_SCANNED = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_READ = 4;
    int MESSAGE_DEVICE_NAME = 5;
    int MESSAGE_TOAST = 6;
    int MESSAGE_NEW_DEVICE = 7;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String DEVICE_MAC = "device_mac";
    String DEVICE_SIGNAL = "device_signal";
    String TOAST = "toast";

    // Battery level
    int MAX_VOLTAGE = 8330000;
    int MIN_VOLTAGE = 6600000;
    int BATTERY_UPDATE_PERIOD = 30*1000;
    int BATTERY_UPDATE_DELAY = 5*1000;
    int BATTERY_BACKLOG = 20;
}
