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

public class OnOffSensorActivity extends AppCompatActivity {
    BroadcastReceiver receiver = null;

    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_off);

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
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S2");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });
    }

    private void drawOnUi(String data){
        try{
            ImageView greenDot = (ImageView) findViewById(R.id.greendot);
            ImageView redDot = (ImageView) findViewById(R.id.redDot);
            String[] extractData = data.split("#");
            if ("1".equals(extractData[0]) || "1".equals(extractData[1])){
                Log.i("the botton values is: ", "botton need to be GREEN" + extractData[1]);
                greenDot.setVisibility(View.VISIBLE);
                redDot.setVisibility(View.INVISIBLE);
            }
            else{
                Log.i("the botton values is: ", "botton need to be RED" + extractData[1]);
                redDot.setVisibility(View.VISIBLE);
                greenDot.setVisibility(View.INVISIBLE);
            }
            outputTextView.setText(data);

        }
        catch (Exception e){

        }
    }
}
