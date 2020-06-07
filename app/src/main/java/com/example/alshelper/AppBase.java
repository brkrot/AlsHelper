package com.example.alshelper;

import android.app.Application;

public class AppBase extends Application {

    public static AppBase INSTANCE = null;
    public boolean isBluetoothConnected = false;
    public BluetoothConnector bluetoothConnector = null;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
