package com.example.alshelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public void move(View v){
        Intent intent;
        int tag = Integer.parseInt(v.getTag().toString());
        if(tag==1){
            intent = new Intent (this.getBaseContext(), sensor1Activity.class);
        }else{
            intent = new Intent(this.getBaseContext(), sensor2Activity.class);
        }

        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
