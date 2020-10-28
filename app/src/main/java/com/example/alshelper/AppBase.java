package com.example.alshelper;

import android.app.Application;

import com.example.alshelper.bluetoothUtils.BluetoothConnector;
import com.example.alshelper.processingData.DiagnosisDataCollector;

public class AppBase extends Application {

    public static AppBase INSTANCE = null;
    public boolean isBluetoothConnected = false;
    public BluetoothConnector bluetoothConnector = null;
    public DiagnosisDataCollector diagnosticData = new DiagnosisDataCollector();

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
