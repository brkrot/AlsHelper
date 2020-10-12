package com.example.alshelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.alshelper.bluetoothUtils.PairedDevicesList;
import com.example.alshelper.bluetoothUtils.Terminal;
import com.example.alshelper.sensors.SensorMenu;

public class MainActivity extends AppCompatActivity {


    ImageView bt;
    Button connectBT;
    Button disconnect;
    Button sensors;
    Button lamp;

    public void connectBT(){
        Intent intent = new Intent(this, PairedDevicesList.class);
        startActivity(intent);
    }

    private void diconnectBT(){
        Log.i("DISCCONCT","bla");
        AppBase.INSTANCE.bluetoothConnector.disconnectBT();
        bt.animate().alpha(0.1f).setDuration(500);
        connectBT.setEnabled(true);
        disconnect.setEnabled(false);
        lamp.setEnabled(false);
        sensors.setEnabled(false);
    }

    public void goToLampControl(View v){
        if(AppBase.INSTANCE.isBluetoothConnected){
            Intent intent = new Intent(this, LampControl.class);
            startActivity(intent);
        }
    }

    public void goToSensorMenu(View v){
        if(AppBase.INSTANCE.isBluetoothConnected){
            Intent intent = new Intent(this, SensorMenu.class);
            startActivity(intent);
        }
    }

    public void terminal(View v){
        if(AppBase.INSTANCE.isBluetoothConnected){
            Intent intent = new Intent(this, Terminal.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = (ImageView)findViewById(R.id.bluetoothImageView);
        bt.setAlpha(0.1f);
        connectBT = findViewById(R.id.connectButton);
        disconnect = findViewById(R.id.disconnectButton);
        lamp = findViewById(R.id.lampButton);
        sensors = findViewById(R.id.sensorsButton);

        disconnect.setEnabled(false);
        lamp.setEnabled(false);
        sensors.setEnabled(false);


        findViewById(R.id.bluetoothImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppBase.INSTANCE.isBluetoothConnected){
                    connectBT();
                }else{
                    diconnectBT();
                }
            }
        });

        findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diconnectBT();
            }
        });

        findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBT();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppBase.INSTANCE.bluetoothConnector!=null) {
            bt.animate().alpha(1f).setDuration(2000);
            disconnect.setEnabled(true);
            lamp.setEnabled(true);
            sensors.setEnabled(true);
            connectBT.setEnabled(false);
        }
    }
}
