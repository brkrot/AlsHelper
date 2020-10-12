package com.example.alshelper.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alshelper.AppBase;
import com.example.alshelper.R;

public class AnalogSensorActivity extends AppCompatActivity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analog_sensor);

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
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S3");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });
    }

    private void drawOnUi(String data){
        try{
            Log.i("the resistance values is: ", data);
            outputTextView.setText(data);

        }
        catch (Exception e){

        }
    }
}
