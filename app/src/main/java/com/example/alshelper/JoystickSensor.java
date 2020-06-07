package com.example.alshelper;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class JoystickSensor extends Activity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_sensor);
        outputTextView = (TextView)findViewById(R.id.outputTextView);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                if (!intent.getAction().equals("broadcast_data_from_bluetooth")) return;
                String data = intent.getStringExtra("incoming");
                if (data == null) return;

                drawOnUi(data);
            }
        };

        // here in onCreate()
        registerReceiver(receiver, new IntentFilter("broadcast_data_from_bluetooth"));

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S1");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputTextView.setText("");
                AppBase.INSTANCE.bluetoothConnector.stopReadingData();
            }
        });
    }

    private void drawOnUi(String data){
        outputTextView.setText(data);
    }
}