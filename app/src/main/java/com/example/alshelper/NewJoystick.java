package com.example.alshelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

public class NewJoystick extends AppCompatActivity {

    BroadcastReceiver receiver = null;
    TextView locationTextView;

    private int[] pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_joystick);

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

        locationTextView = findViewById(R.id.locationTextView);
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S1");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });

    }

    /**
     * drawOnUi:
     * The function Draw the point image on the axis by the arduino joystick info
     * The Info is being sent every (!)READING_DATA_GAP in BlueToothAdapter ( now equals  to 300) ms
     * @param data is a string with the data from the Arduino
     */
    private void drawOnUi(String data){

        //Log.i("tag",String.valueOf(pos[0])+"\n"+String.valueOf(pos[1]));
        parseData(data);
        locationTextView.setText("X="+String.valueOf(pos[0])+"\n"+"Y="+String.valueOf(pos[1]));
    }

    /**
     *function get the data and parse it to integers array
     * @param data
     */
    private void parseData(String data) {
        String[] textStr = data.split("#");
        pos = new int[textStr.length];
        for (int i = 0; i < textStr.length-1; i++) {
            pos[i] = Integer.parseInt(textStr[i]);
        }
    }

}
