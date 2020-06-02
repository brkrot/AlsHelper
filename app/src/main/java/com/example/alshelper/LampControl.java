package com.example.alshelper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LampControl extends AppCompatActivity {

    ImageView bulb;
    public void switchLamp(View v){
        AppBase.INSTANCE.bluetoothConnector.writeToArduino("L"+v.getTag().toString());
        if(v.getTag().toString().equals("1")){
            bulb.animate().alpha(1f).setDuration(500);
        }else{
            bulb.animate().alpha(0.1f).setDuration(500);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        bulb = findViewById(R.id.lampImageView);
    }

}
