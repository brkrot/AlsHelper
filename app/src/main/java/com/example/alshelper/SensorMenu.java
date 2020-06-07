package com.example.alshelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
                intent = new Intent(this, sensor1Activity.class);
                startActivity(intent);

                break;
            case "4":
                intent = new Intent(this, NewJoystick.class);
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
