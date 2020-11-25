package com.example.alshelper.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alshelper.AppBase;
import com.example.alshelper.R;

public class OnOffSensorActivity extends AppCompatActivity {
    BroadcastReceiver receiver = null;

    private TextView outputTextView;
    private ImageView greenDot;
    private ImageView redDot;
    private ObjectAnimator dotAnim;


    static final int MAX_DELTA_SEC_BETWEEN_TWO_CLICKS = 2;

    static long tFirst = System.currentTimeMillis();

    static long tSecond = System.currentTimeMillis();

    static  boolean sensorRested = false;

    static int currentLevel = 1;

    static  boolean startIndication = false;

    static  boolean lastColor = false;

    static  int[] DataCollector = new int[]{0,0,0};

    public void changeLevel(View view)
    {
        if(startIndication) {
            if (view.getId() == R.id.levelDown & currentLevel > 1) {
                currentLevel--;
            } else if (view.getId() == R.id.levelUp & currentLevel < 2) {
                currentLevel++;

            } else {
                Toast.makeText(getApplicationContext(), "You are already in the edge level", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("change level", "change level");
        }
        else {
            Toast.makeText(getApplicationContext(),"Please press start before",Toast.LENGTH_SHORT).show();
        }
    }

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
                greenDot = (ImageView) findViewById(R.id.greendot);
                redDot = (ImageView) findViewById(R.id.redDot);
                dotAnim = ObjectAnimator.ofInt(greenDot, "backgroundColor", Color.TRANSPARENT, Color.GREEN,
                        Color.TRANSPARENT);
                startIndication = true;
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S2");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
                Log.i("the : ", "*****************" );
            }
        });
    }

    private void drawOnUi(String data){
        try{
            String[] extractData = data.split("#");
            change_bottom_color(extractData);
            outputTextView.setText(data);

        }
        catch (Exception e){

        }
    }

    @SuppressLint("WrongConstant")
    private void change_bottom_color(String[] extractData) {
        if ("1".equals(extractData[0]) || "1".equals(extractData[1])){
            Log.i("the botton values is: ", "botton need to be GREEN" + extractData[1]);
            lastColor = true;
            greenDot.setVisibility(View.VISIBLE);
            redDot.setVisibility(View.INVISIBLE);
            if (currentLevel == 2){
                tSecond = System.currentTimeMillis();
                long tDelta = tSecond - tFirst;
                double elapsedSeconds = tDelta / 1000.0;
                Log.i("time stamp" , " first: " + tFirst + " second:" + tSecond + " delta:" + elapsedSeconds);

                if (elapsedSeconds<MAX_DELTA_SEC_BETWEEN_TWO_CLICKS && sensorRested){
                    Toast.makeText(getApplicationContext(),"GOOOOD!",Toast.LENGTH_SHORT).show();
                    dotAnim.setDuration(1500);
                    dotAnim.setEvaluator(new ArgbEvaluator());
                    dotAnim.setRepeatMode(Animation.REVERSE);
                    dotAnim.start();
                    DataCollector[1] = 1;
                    Log.i("the botton values is: ", "GOOOOOOOOOOOOOOOOOOD");
                }
                tFirst = System.currentTimeMillis();
                sensorRested = false;
            }
            else {
                DataCollector[0] = 1;
            }
            sensorRested = false;
        }
        else{
            Log.i("the botton values is: ", "botton need to be RED" + extractData[1] + greenDot.getVisibility());
            redDot.setVisibility(View.VISIBLE);
            greenDot.setVisibility(View.INVISIBLE);
            if(lastColor) {
                sensorRested = true;
                Log.i("!!!!!!!!!!", "rested!!!!!!!!");
            }
            lastColor = false;
        }
    }
}

