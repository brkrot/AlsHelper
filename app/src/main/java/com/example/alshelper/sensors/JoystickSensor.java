package com.example.alshelper.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alshelper.AppBase;
import com.example.alshelper.R;
import com.example.alshelper.processingData.DiagnosisDataCollector;
import com.example.alshelper.sensors.DynamicSquareLayout;


public class JoystickSensor extends AppCompatActivity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;

    /*##################
# STATIC VARIABLES #
####################*/
    static final int JOYSTICK_RANGE = 1000;

    static final int JOYSTICK_NORMALIZE_FACTOR = 10;

    static final double CHANGE_LEVEL_FACTOR = 1.5;

    static double targetFactor = 1;

    static int currentLevel = 2;

    DiagnosisDataCollector diagnosticData = new DiagnosisDataCollector();

    ImageView ivImage ;

    Bitmap yourBitmap ;

/*############
#  METHODS   #
##############*/

    /**
     * this function called when UPPER/LOWER LEVEL button clicked
     * ++/-- the level and then restart
     * in case of edge level return TOAST
     * @param view
     */
    public void changeLevel(View view)
    {
        ImageView target = (ImageView)findViewById(R.id.target);
        ImageView target2 = (ImageView)findViewById(R.id.target2);
        ImageView target3 = (ImageView)findViewById(R.id.target3);
        if (view.getId() == R.id.levelUp & currentLevel < 3){
            targetFactor = target.getWidth() * CHANGE_LEVEL_FACTOR;
            currentLevel++;
            Log.i("params", "changeLevel: " + target.getLayoutParams());
            changeImageSize(target, target.getWidth() * CHANGE_LEVEL_FACTOR);
            changeImageSize(target2, target.getWidth() * CHANGE_LEVEL_FACTOR);
            changeImageSize(target3, target.getWidth() * CHANGE_LEVEL_FACTOR);
            Log.i("params", "factor: " + currentLevel);
        }
        else if  (view.getId() == R.id.levelDown & currentLevel > 1){
            targetFactor = target.getWidth() / CHANGE_LEVEL_FACTOR;
            currentLevel--;
            changeImageSize(target, target.getWidth()/CHANGE_LEVEL_FACTOR);
            changeImageSize(target2, target.getWidth()/CHANGE_LEVEL_FACTOR);
            changeImageSize(target3, target.getWidth()/CHANGE_LEVEL_FACTOR);
        }
        else {
            Toast.makeText(getApplicationContext(),"You are already in the edge level",Toast.LENGTH_SHORT).show();
            return;
        }
        View restart = (View) findViewById(R.id.restatButton);
        restartSeek(restart);
        Log.i("change level","change level");
    }

    private void changeImageSize(ImageView target, double factor) {
        DynamicSquareLayout.LayoutParams layoutParams = new DynamicSquareLayout.LayoutParams(((Double) (factor)).intValue(), ((Double) (factor)).intValue());
        target.setLayoutParams(layoutParams);
    }

    /**
     * this function move the dot by animate function +
     * this function check if the current dot place is valid/"win" place
     * @param xProgress
     * @param yProgress
     */
    public void moveTheDot(int xProgress, int yProgress)
    {
        float xLocationOfDot;
        float yLocationOfDot;
        ImageView dot = (ImageView) findViewById(R.id.dot);
        ImageView axis = (ImageView) findViewById(R.id.xyAxis);
        float axisWidth = axis.getWidth();
        xLocationOfDot = dot.getX() + dot.getWidth()/2;
        yLocationOfDot = dot.getY() + dot.getHeight()/2;

        if ((xLocationOfDot + xProgress > 0 & xLocationOfDot + xProgress < axisWidth) & (yLocationOfDot + yProgress > 0 & yLocationOfDot + yProgress < axisWidth)){
            dot.animate().translationXBy(xProgress);
            dot.animate().translationYBy(yProgress);
            float dotX = dot.getX() + dot.getWidth()/2;
            float dotY = dot.getY() + dot.getHeight()/2;
            Log.i("the dot placed on" , " x: " + dotX + " y:" + dotY);
            ImageView target = (ImageView) findViewById(R.id.target);
            ImageView target2 = (ImageView) findViewById(R.id.target2);
            ImageView target3 = (ImageView) findViewById(R.id.target3);
            if(hitting_the_target(dotX, dotY, target) || hitting_the_target(dotX, dotY, target2) || hitting_the_target(dotX, dotY, target3)){
                int left = diagnosticData.dataMap.get("Left ability");
                diagnosticData.dataMap.remove("Left ability");
                diagnosticData.dataMap.put("Left ability", left+1);
                Log.i("the dot placed on" , "good!! LEFT ABILITY = " + diagnosticData.dataMap.get("Left ability"));
                final MediaPlayer win = MediaPlayer.create(this, R.raw.hit_the_target);
                win.start();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"You are in the edge",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this function check if the current dot place is "win" place
     * @param dotX
     * @param dotY
     * @param target
     * @return
     */
    private boolean hitting_the_target(float dotX, float dotY, ImageView target) {
        float targetWidth = target.getWidth();
        float targetHeight = target.getHeight();
        float endOfTargetX = target.getX() + targetWidth;
        float endOfTargetY = target.getY() + targetHeight;
        //this condition check if the dot is on the target
        if ((dotX > target.getX() & dotX < endOfTargetX) & (dotY > target.getY() & dotY < endOfTargetY)){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * called by click on restart button or called by
     *changeLevel function, this function reset all
     *parameters of this Activity(except of level)
     * @param view
     */
    public void restartSeek(View view)
    {
        AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
        ImageView axis = (ImageView) findViewById(R.id.xyAxis);
        ImageView dot = (ImageView) findViewById(R.id.dot);
        ImageView target = (ImageView) findViewById(R.id.target);
        ImageView target2 = (ImageView) findViewById(R.id.target2);
        ImageView target3 = (ImageView) findViewById(R.id.target3);
        float axisCenterX = axis.getX() + axis.getWidth()/2;
        float axisCenterY = axis.getY() + axis.getHeight()/2;
        float dotWidth = dot.getWidth()/2;
        float dotHeight = dot.getHeight()/2;
        if (targetFactor ==1){
            targetFactor = target2.getWidth();
        }
        double targetSize = (targetFactor)/2;
        float floatTargetSize = (float)targetSize;
        dot.animate().x(axisCenterX-dotWidth).y(axisCenterY-dotHeight);
        target.animate().x((axisCenterX/3)-floatTargetSize).y((axisCenterY/3)-floatTargetSize);
        target2.animate().x(axisCenterX-floatTargetSize).y((axisCenterY/3)-floatTargetSize);
        target3.animate().x((axisCenterX/3)-floatTargetSize).y(axisCenterY-floatTargetSize);
        float width = axis.getWidth();
        float height = axis.getHeight();

        Log.i("the dot placed on" , floatTargetSize + " height " + height + " center " + axisCenterX + "  " + axisCenterY);
        targetFactor = 1;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_sensor);

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
                AppBase.INSTANCE.bluetoothConnector.writeToArduino("S1");
                AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputTextView.setText("input stopted");
                AppBase.INSTANCE.bluetoothConnector.stopReadingData();
            }
        });
    }

    private void drawOnUi(String data){
        try{
            String[] xyProgress = data.split("#");
            if (xyProgress.length == 2){
                NormalizeJoystickValues normalizeJoystickValues = new NormalizeJoystickValues(xyProgress).invoke();
                int xProgress = normalizeJoystickValues.getxProgress();
                int yProgress = normalizeJoystickValues.getyProgress();
                //to prevent unnecessary function calls
                if(xProgress != 0 || yProgress !=0){
                    moveTheDot(xProgress, -yProgress);
                    outputTextView.setText(data);
                }
            }
        }
        catch (Exception e){

        }
    }

    private class NormalizeJoystickValues {
        private String[] xyProgress;
        private int xProgress;
        private int yProgress;

        public NormalizeJoystickValues(String... xyProgress) {
            this.xyProgress = xyProgress;
        }

        public int getxProgress() {
            return xProgress;
        }

        public int getyProgress() {
            return yProgress;
        }

        public NormalizeJoystickValues invoke() {
            xProgress = Integer.parseInt(xyProgress[0]) - JOYSTICK_RANGE / 2;
            yProgress = Integer.parseInt(xyProgress[1]) - JOYSTICK_RANGE / 2;
            if (xProgress < 4 & xProgress > -1){
                xProgress = 0;
            }
            if (yProgress < 4 & yProgress > -1){
                yProgress = 0;
            }
            xProgress = xProgress/JOYSTICK_NORMALIZE_FACTOR;
            yProgress = yProgress/JOYSTICK_NORMALIZE_FACTOR;
            Log.i("the xy values is: ", String.valueOf(xProgress) + " " + String.valueOf(yProgress));
            return this;
        }
    }
}