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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class JoystickSensor extends AppCompatActivity {

    BroadcastReceiver receiver = null;

    private TextView outputTextView;

    /*##################
# STATIC VARIABLES #
####################*/
    static final int JOYSTICK_RANGE = 1000;

    static final int JOYSTICK_NORMALIZE_FACTOR = 10;

    static final int SEEK_BAR_HIGH_RANGE = 100;

    static final int SEEK_BAR_MEDIUM_RANGE = 50;

    static final int SEEK_BAR_LOW_RANGE = 24;

    static int currentLevel = 2;

    static int currentRange = SEEK_BAR_MEDIUM_RANGE;

    ImageView ivImage ;

    Bitmap yourBitmap ;

/*############
#  METHODS   #
##############*/

    /**
     *  this function get level number(1-3) and
     *return the fit range for this level
     *an error will return when the level is invalid
     * @param level
     */
    public void identifyLevelRange(int level)
    {
        switch (level){
            case 1:
                currentRange = SEEK_BAR_LOW_RANGE;
                break;
            case 2:
                currentRange = SEEK_BAR_MEDIUM_RANGE;
                break;
            case 3:
                currentRange = SEEK_BAR_HIGH_RANGE;
                break;
            default:
                Log.e("invalidInput", "invalid level input to identifyLevelRange function");
        }
    }

    /**
     * this function called when UPPER/LOWER LEVEL button clicked
     * ++/-- the level and then restart
     * in case of edge level return TOAST
     * @param view
     */
    public void changeLevel(View view)
    {
        ImageView target = (ImageView)findViewById(R.id.target);
        if (view.getId() == R.id.levelUp & currentLevel < 3){
            currentLevel++;
            identifyLevelRange(currentLevel);
            Log.i("params", "changeLevel: " + target.getLayoutParams());
            DynamicSquareLayout.LayoutParams layoutParams = new DynamicSquareLayout.LayoutParams(((Double)(target.getWidth()*1.5)).intValue(), ((Double)(target.getWidth()*1.5)).intValue());
            target.setLayoutParams(layoutParams);
        }
        else if  (view.getId() == R.id.levelDown & currentLevel > 1){
            currentLevel--;
            identifyLevelRange(currentLevel);
            DynamicSquareLayout.LayoutParams layoutParams = new DynamicSquareLayout.LayoutParams(((Double)(target.getWidth()/1.5)).intValue(), ((Double)(target.getWidth()/1.5)).intValue());
            target.setLayoutParams(layoutParams);
        }
        else {
            Toast.makeText(getApplicationContext(),"You are already in the edge level",Toast.LENGTH_SHORT).show();
            return;
        }
        View restart = (View) findViewById(R.id.restatButton);
        restartSeek(restart);
        Log.i("change level","change level");
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
        ImageView axis = (ImageView) findViewById(R.id.xyAxis);
        ImageView dot = (ImageView) findViewById(R.id.dot);
        float axisCenterX = axis.getX() + axis.getWidth()/2;
        float axisCenterY = axis.getY() + axis.getHeight()/2;
        float dotWidth = dot.getWidth()/2;
        float dotHeight = dot.getHeight()/2;
        dot.animate().x(axisCenterX-dotWidth).y(axisCenterY-dotHeight);
        SeekBar horizenBar = (SeekBar) findViewById(R.id.horizenSeekBar);
        SeekBar verticalBar = (SeekBar) findViewById(R.id.verticalSeekBar);
        horizenBar.setMax(currentRange);
        verticalBar.setMax(currentRange);
        horizenBar.setProgress(currentRange/2);
        verticalBar.setProgress(currentRange/2);

        float width = axis.getWidth();
        float height = axis.getHeight();

        Log.i("the dot placed on" , width + " height " + height + " center " + axisCenterX + "  " + axisCenterY);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_sensor);



        //initialize the seekbars + level
        SeekBar horizenBar = (SeekBar) findViewById(R.id.horizenSeekBar);
        SeekBar verticalBar = (SeekBar) findViewById(R.id.verticalSeekBar);

        currentRange = SEEK_BAR_MEDIUM_RANGE;
        horizenBar.setMax(currentRange);
        verticalBar.setMax(currentRange);
        horizenBar.setProgress(currentRange/2);
        verticalBar.setProgress(currentRange/2);

        horizenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("horizen seekBar Change" , Integer.toString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int xProgress = currentRange/2-seekBar.getProgress();
                int yProgress = 0;
                moveTheDot(xProgress, yProgress);
            }
        });

        verticalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("vertical seekBar Change" , Integer.toString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int xProgress = 0;
                int yProgress = currentRange/2-seekBar.getProgress();
                moveTheDot(xProgress, yProgress);
            }
        });




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
                outputTextView.setText("");
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
                    moveTheDot(xProgress, yProgress);
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
            if (xProgress < 6 & xProgress > -1){
                xProgress = 0;
            }
            if (yProgress < 6 & yProgress > -1){
                yProgress = 0;
            }
            xProgress = xProgress/JOYSTICK_NORMALIZE_FACTOR;
            yProgress = yProgress/JOYSTICK_NORMALIZE_FACTOR;
            Log.i("the xy values is: ", String.valueOf(xProgress) + " " + String.valueOf(yProgress));
            return this;
        }
    }
}