package com.example.alshelper.sensors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alshelper.JoystickSensor;
import com.example.alshelper.R;
import com.example.alshelper.sensors.AnalogSensorActivity;
import com.example.alshelper.sensors.OnOffSensorActivity;

public class SensorMenu extends AppCompatActivity {

    public void chooseSensor(View v){
        Intent intent;
        //AppBase.INSTANCE.bluetoothConnector.writeToArduino("S"+v.getTag().toString());
        switch(v.getTag().toString()){
            case "1":
                intent = new Intent(this, JoystickSensor.class);
                startActivity(intent);
                break;
            case "2":
                /*intent = new Intent(this, KnockSensor.class);
                startActivity(intent);*/

                break;
            case "3":
                intent = new Intent(this, OnOffSensorActivity.class);
                startActivity(intent);
                break;
            case "4":
                intent = new Intent(this, AnalogSensorActivity.class);
                startActivity(intent);

                break;
            default:

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_menu);
    }
}
