package com.example.alshelper.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.AndroidResources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alshelper.AppBase;
import com.example.alshelper.R;

import static java.lang.Integer.parseInt;

public class AnalogSensorActivity extends AppCompatActivity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;
    private static ProgressBar vertical_progressbar;
    private static TextView vertical_status;
    private static Drawable draw;//*3
    private static Drawable draw2;//*5
    private static Drawable draw3;//*7
    private static Drawable draw4;//*11
    private static Drawable current_draw;

    static final int MIN_DELTA_SEC_ON_ONE_AREA = 2;

    static long tFirst = System.currentTimeMillis();

    static long tSecond = System.currentTimeMillis();

    static int currentLevel = 2;

    static  boolean startIndication = false;

    static  boolean colorChanged = false;

    static  int[] DataCollector = new int[]{1,1,1} ;

    public void changeLevel(View view)
    {
        if(startIndication) {
            if (view.getId() == R.id.levelDown & currentLevel > 1) {
                currentLevel--;
            } else if (view.getId() == R.id.levelUp & currentLevel < 3) {
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
                vertical_progressbar = (ProgressBar) findViewById(R.id.vertical_progressbar);
                vertical_status = (TextView) findViewById(R.id.verticalprogress_percentage);
                draw = getApplicationContext().getResources().getDrawable(R.drawable.vertical_progressbar);
                draw2 = getApplicationContext().getResources().getDrawable(R.drawable.vertical_progressbar2);
                draw3 = getApplicationContext().getResources().getDrawable(R.drawable.vertical_progressbar3);
                draw4 = getApplicationContext().getResources().getDrawable(R.drawable.vertical_progressbar4);
                startIndication = true;
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S3");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });
    }

    private void drawOnUi(String data){
        try{
            String[] xyProgress = data.split("#");
            int extractData = parseInt(xyProgress[1]);
            Log.i("the resistance values is: ", String.valueOf(extractData));
            // Get the Drawable custom_progressbar
            // set the drawable as progress drawable
            if (extractData >= 0) {
                int normalizData = normalize_data(extractData);
                view_and_check(normalizData);
            }
            outputTextView.setText(data);

        }
        catch (Exception e){

        }
    }

    private void view_and_check(int normalizData) {
        current_draw = vertical_progressbar.getProgressDrawable();
        change_progress_color(normalizData);
        if (current_draw!=vertical_progressbar.getProgressDrawable()){
            tFirst = System.currentTimeMillis();
            colorChanged = true;
        }
        else if(colorChanged){
            tSecond = System.currentTimeMillis();
            long tDelta = tSecond - tFirst;
            double elapsedSeconds = tDelta / 1000.0;
            if (elapsedSeconds>MIN_DELTA_SEC_ON_ONE_AREA){
                Toast.makeText(getApplicationContext(),"GOOOOD!",Toast.LENGTH_SHORT).show();
                colorChanged = false;
                current_draw = vertical_progressbar.getProgressDrawable();
                Log.i("the currentdraw is: ", current_draw.toString());
                switch (current_draw.toString()){
                    case ("draw")://TODO extract the tag/string of each color
                        update_data_per_color(3);
                        break;
                    case ("draw2"):
                        update_data_per_color(5);
                        break;
                    case ("draw3"):
                        update_data_per_color(7);
                        break;
                    case ("draw4"):
                        update_data_per_color(11);
                        break;
                }
            }
        }
        vertical_progressbar.setProgress(normalizData);
        vertical_status.setText(normalizData + " %");
    }

    private void update_data_per_color(int colorNumber) {
        if(DataCollector[currentLevel] % colorNumber == 0){
            DataCollector[currentLevel] =  DataCollector[currentLevel]*colorNumber;
        }
    }

    private int normalize_data(int extractData) {
        int normalizData = extractData/10;
        if (normalizData > 100){
            normalizData = 100;
        }
        return normalizData;
    }

    private void change_progress_color(int normalizData) {
        vertical_progressbar.getProgressDrawable();
        switch (currentLevel){
            case 1:
                if(normalizData>50){
                    vertical_progressbar.setProgressDrawable(draw2);
                }
                else {
                    vertical_progressbar.setProgressDrawable(draw);
                }
                break;
            case 2:
                if(normalizData<33){
                    vertical_progressbar.setProgressDrawable(draw);
                }
                else if(normalizData<66){
                    vertical_progressbar.setProgressDrawable(draw2);
                }
                else{
                    vertical_progressbar.setProgressDrawable(draw3);
                }
                break;
            case 3:
                if(normalizData<25){
                    vertical_progressbar.setProgressDrawable(draw);
                }
                else if(normalizData<50){
                    vertical_progressbar.setProgressDrawable(draw2);
                }
                else if(normalizData<75){
                    vertical_progressbar.setProgressDrawable(draw3);
                }
                else{
                    vertical_progressbar.setProgressDrawable(draw4);
                }
                break;
            default:
                break;
        }
    }

}
