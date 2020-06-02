package com.example.alshelper;

import android.app.Application;

// todo barak 4: you create a class that extends Application. you override onCreate() so you can have the application from anywhere:
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
