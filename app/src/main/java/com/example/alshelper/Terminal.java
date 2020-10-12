package com.example.alshelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.alshelper.DiagnosisDataCollector;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


public class Terminal extends AppCompatActivity {
    BroadcastReceiver receiver = null;
    TextView outputTextView;
    EditText sendText;
String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        input="";

        sendText = (EditText)findViewById(R.id.sendText);
        outputTextView = (TextView)findViewById(R.id.textView1);
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

    }




    private void drawOnUi(String data){
        try{
            if(!data.equals("")){
                input+=data+"\n";
            }

            outputTextView.setText(input);
            } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void send(View v){
        AppBase.INSTANCE.bluetoothConnector.writeToArduino(sendText.getText().toString());
        if(AppBase.INSTANCE.bluetoothConnector.isReadindData()!=true) {
           AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
//            input = AppBase.INSTANCE.bluetoothConnector.readOnce();
//            outputTextView.setText(input);
        }
    }

    public void stop(View v) {
        // call the superclass method first
        super.onStop();
        AppBase.INSTANCE.bluetoothConnector.stopReadingData();
        //unregisterReceiver(receiver);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

}


