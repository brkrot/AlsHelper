package com.example.alshelper.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


public class JoystickSensor extends AppCompatActivity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;

 /*##################
# STATIC VARIABLES #
####################*/

    //# CONSTANT VARIABLES - some of them will update one time in onClick(startBottom) function #

    static final int JOYSTICK_RANGE = 1000;//this range is the highest value that we got from joystick via bluetooth(vertical/horizonal)

    static final int JOYSTICK_NORMALIZE_FACTOR = 36;//we want to normalize the joystick by (screen_width/36),
    // if we want the dot to move with smaller steps, we need to increase this value.

    static final double CHANGE_LEVEL_FACTOR = 1.5;

    static final int MAX_DELTA_SEC_BETWEEN_TWO_CLICKS = 2;

    static float AXIS_CENTER_X = 0;

    static float AXIS_CENTER_Y = 0;

    static int DOT_WIDTH = 30;//this is a default value, we will change it when click start bottom /change level / restart

    static int SCREEN_WIDTH = 720;//this is a default value we will change it when click start bottom /change level / restart

    static int JOYSTICK_NORMALIZE_FACTOR_PER_SCREEN_SIZE = 20;//this is a default value we will change it when click start bottom /change level / restart

    //# GLOBAL VARIABLES that will change more than 1 time #
    static double targetFactor = 1;

    static  int[] DataCollector = new int[]{1,2,3,4,5};

    static int currentLevel = 2;

    static String hitTarget = "target";

    static long tFirst = System.currentTimeMillis();

    static long tSecond = System.currentTimeMillis();

    static  boolean startIndication = false;

    static  boolean twoCloseClicks = false;

    static  boolean joystickRested = false;

    DiagnosisDataCollector diagnosticData = new DiagnosisDataCollector();


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
        if(startIndication) {
            ImageView target1 = (ImageView) findViewById(R.id.target1);
            ImageView target2 = (ImageView) findViewById(R.id.target2);
            ImageView target3 = (ImageView) findViewById(R.id.target3);
            if (view.getId() == R.id.levelDown & currentLevel > 1) {
                targetFactor = target1.getWidth() * CHANGE_LEVEL_FACTOR;
                currentLevel--;
                target1.setVisibility(View.VISIBLE);
                target2.setVisibility(View.VISIBLE);
                target3.setVisibility(View.VISIBLE);
                changeImageSize(target1, target1.getWidth() * CHANGE_LEVEL_FACTOR);
                changeImageSize(target2, target1.getWidth() * CHANGE_LEVEL_FACTOR);
                changeImageSize(target3, target1.getWidth() * CHANGE_LEVEL_FACTOR);
            } else if (view.getId() == R.id.levelUp & currentLevel < 4) {
                targetFactor = target1.getWidth() / CHANGE_LEVEL_FACTOR;
                currentLevel++;
                if (currentLevel == 4) {
                    target1.setVisibility(View.INVISIBLE);
                    target2.setVisibility(View.INVISIBLE);
                    target3.setVisibility(View.INVISIBLE);
                }
                changeImageSize(target1, target1.getWidth() / CHANGE_LEVEL_FACTOR);
                changeImageSize(target2, target1.getWidth() / CHANGE_LEVEL_FACTOR);
                changeImageSize(target3, target1.getWidth() / CHANGE_LEVEL_FACTOR);
            } else {
                Toast.makeText(getApplicationContext(), "You are already in the edge level", Toast.LENGTH_SHORT).show();
                return;
            }
            View restart = (View) findViewById(R.id.restatButton);
            restartSeek(restart);
            Log.i("change level", "change level");
        }
        else {
            Toast.makeText(getApplicationContext(),"Please press start before",Toast.LENGTH_SHORT).show();
        }
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
        twoCloseClicks = false;
        float xLocationOfDot;
        float yLocationOfDot;
        ImageView dot = (ImageView) findViewById(R.id.dot);
        float axisWidth = SCREEN_WIDTH;
        xLocationOfDot = dot.getX() + DOT_WIDTH/2;
        yLocationOfDot = dot.getY() + DOT_WIDTH/2;

        if ((xLocationOfDot + xProgress > 0 & xLocationOfDot + xProgress < axisWidth) & (yLocationOfDot + yProgress > 0 & yLocationOfDot + yProgress < axisWidth)){
            dot.animate().translationXBy(xProgress);
            dot.animate().translationYBy(yProgress);
            float dotX = dot.getX() + DOT_WIDTH/2;
            float dotY = dot.getY() + DOT_WIDTH/2;
            Log.i("the dot placed on" , " x: " + dotX + " y:" + dotY);
            if (currentLevel == 4){
                tSecond = System.currentTimeMillis();
                long tDelta = tSecond - tFirst;
                double elapsedSeconds = tDelta / 1000.0;
                Log.i("time stamp" , " first: " + tFirst + " second:" + tSecond + " delta:" + elapsedSeconds);

                if (elapsedSeconds<MAX_DELTA_SEC_BETWEEN_TWO_CLICKS && joystickRested){
                    twoCloseClicks = true;
                }
                tFirst = System.currentTimeMillis();
                joystickRested = false;
            }
            ImageView target1 = (ImageView) findViewById(R.id.target1);
            ImageView target2 = (ImageView) findViewById(R.id.target2);
            ImageView target3 = (ImageView) findViewById(R.id.target3);
            if(hitting_the_target(dotX, dotY, target1) || hitting_the_target(dotX, dotY, target2) || hitting_the_target(dotX, dotY, target3) || twoCloseClicks){
                if (twoCloseClicks){
                    DataCollector[4] = 1;
                }
                else {
                    switch (hitTarget){
                        case "target1":
                            update_data(1);
                            break;
                        case "target2":
                            update_data(2);
                            break;
                        case "target3":
                            update_data(3);
                            break;
                    }
                }
                Toast.makeText(getApplicationContext(),"GOOOOD!",Toast.LENGTH_SHORT).show();
                final MediaPlayer win = MediaPlayer.create(this, R.raw.hit_the_target);
                win.start();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"You are in the edge",Toast.LENGTH_SHORT).show();
        }
    }

    private void update_data(int target_number) {
        switch (currentLevel){
            case 1:
                DataCollector[target_number] = 1;
                break;
            case 2:
                DataCollector[target_number] = 2;
                break;
            case 3:
                DataCollector[target_number] = 3;
                break;
            default:
                break;
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
            hitTarget = (String) target.getTag();
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
        if(startIndication) {
            AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
            ImageView target1 = (ImageView) findViewById(R.id.target1);
            ImageView target2 = (ImageView) findViewById(R.id.target2);
            ImageView target3 = (ImageView) findViewById(R.id.target3);
            ImageView dot = (ImageView) findViewById(R.id.dot);
            if (targetFactor == 1) {
                targetFactor = target2.getWidth();
            }
            double targetSize = (targetFactor) / 2;
            float floatTargetSize = (float) targetSize;
            dot.animate().x(AXIS_CENTER_X - DOT_WIDTH / 2).y(AXIS_CENTER_Y - DOT_WIDTH / 2);
            target1.animate().x((AXIS_CENTER_X / 3) - floatTargetSize).y((AXIS_CENTER_Y / 3) - floatTargetSize);
            target2.animate().x(AXIS_CENTER_X - floatTargetSize).y((AXIS_CENTER_Y / 3) - floatTargetSize);
            target3.animate().x((AXIS_CENTER_X / 3) - floatTargetSize).y(AXIS_CENTER_Y - floatTargetSize);

            targetFactor = 1;
        }
        else{
            Toast.makeText(getApplicationContext(),"Please press start before",Toast.LENGTH_SHORT).show();
        }
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
                ImageView dot = (ImageView) findViewById(R.id.dot);
                ImageView axis = (ImageView) findViewById(R.id.xyAxis);
                DOT_WIDTH = dot.getWidth();
                SCREEN_WIDTH = axis.getWidth();
                AXIS_CENTER_X = axis.getX() + SCREEN_WIDTH/2;
                AXIS_CENTER_Y = axis.getY() + SCREEN_WIDTH/2;
                JOYSTICK_NORMALIZE_FACTOR_PER_SCREEN_SIZE = SCREEN_WIDTH/JOYSTICK_NORMALIZE_FACTOR;
                startIndication = true;
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


    protected void onStop(Bundle savedInstanceState) {
        super.onStop();
        for (int i=1;i<5;i++){
            switch (i){
                case(1):
                    diagnosticData.dataMap.put("combine vertical and horizonal ability", DataCollector[i]);
                    break;
                case(2):
                    diagnosticData.dataMap.put("vertical ability", DataCollector[i]);
                    break;
                case(3):
                    diagnosticData.dataMap.put("horizonal ability", DataCollector[i]);
                    break;
                case(4):
                    diagnosticData.dataMap.put("Two clicks ability", DataCollector[i]);
                    break;
            }
        }
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
                else {
                    joystickRested = true;
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
            if (xProgress < 3 & xProgress > -1){
                xProgress = 0;
            }
            if (yProgress < 3 & yProgress > -1){
                yProgress = 0;
            }
            xProgress = xProgress/JOYSTICK_NORMALIZE_FACTOR_PER_SCREEN_SIZE;
            yProgress = yProgress/JOYSTICK_NORMALIZE_FACTOR_PER_SCREEN_SIZE;
            Log.i("the xy values is: ", String.valueOf(xProgress) + " " + String.valueOf(yProgress));
            return this;
        }
    }
}