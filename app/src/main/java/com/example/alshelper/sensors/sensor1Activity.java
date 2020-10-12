package com.example.alshelper.sensors;

/*############
#  IMPORTS   #
##############*/
import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.alshelper.R;

public class sensor1Activity extends AppCompatActivity {
/*##################
# STATIC VARIABLES #
####################*/
    static final int SEEK_BAR_HIGH_RANGE = 100;

    static final int SEEK_BAR_MEDIUM_RANGE = 50;

    static final int SEEK_BAR_LOW_RANGE = 24;

    static int currentLevel = 2;

    static int currentRange = SEEK_BAR_MEDIUM_RANGE;

/*############
#  METHODS   #
##############*/

    /**
     *
     * @param level
     */
    public void identifyLevelRange(int level)
    /*
    this function get level number(1-3) and
    return the fit range for this level
    an error will return when the level is invalid
    */
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

    public void changeLevel(View view)
    /*
    this function called when UPPER/LOWER LEVEL button clicked
    ++/-- the level and then restart
    in case of edge level return TOAST
    */
    {
        if (view.getId() == R.id.levelUp && currentLevel < 3){
            currentLevel++;
            identifyLevelRange(currentLevel);
        }
        else if  (view.getId() == R.id.levelDown && currentLevel > 1){
            currentLevel--;
            identifyLevelRange(currentLevel);
        }
        else {
            Toast.makeText(getApplicationContext(),"You are already in the edge level",Toast.LENGTH_SHORT).show();
            return;
        }
        View restart = (View) findViewById(R.id.restatButton);
        restartSeek(restart);
        Log.i("change level","change level");
    }

    public void onStopTracking(SeekBar seekBar, boolean hDirect)
    /*
    this function move the dot by animate function + check if the current dot place is valid/"win" place
    */
    {
        int moveSteps = currentRange/2-seekBar.getProgress();
        float location;
        ImageView dot = (ImageView) findViewById(R.id.dot);
        ImageView axis = (ImageView) findViewById(R.id.xyAxis);
        ImageView target = (ImageView) findViewById(R.id.target);
        float targetWidth = target.getWidth();
        float targetHeight = target.getHeight();
        float endOfTargetX = target.getX() + targetWidth;
        float endOfTargetY = target.getY() + targetHeight;
        float axisWidth = axis.getWidth();
        if (hDirect){
            location = dot.getX() + dot.getWidth()/2;
        }
        else{
            location = dot.getY() + dot.getHeight()/2;
        }

        if (location + moveSteps > 0 && location + moveSteps < axisWidth){
            if(hDirect){
                dot.animate().translationXBy(moveSteps);
            }
            else {
                dot.animate().translationYBy(moveSteps);
            }
            float dotX = dot.getX() + dot.getWidth()/2;
            float dotY = dot.getY() + dot.getHeight()/2;
            Log.i("the dot placed on" , " x: " + dotX + " y:" + dotY);
            if ((dotX > target.getX() && dotX < endOfTargetX) && (dotY > target.getY() && dotY < endOfTargetY)){
                final MediaPlayer win = MediaPlayer.create(this, R.raw.hit_the_target);
                win.start();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"You are in the edge",Toast.LENGTH_SHORT).show();
        }
    }

    public void restartSeek(View view)
    /*
    called by click on restart button or called by
    changeLevel function, this function reset all
    parameters of this Activity(except of level)
    */
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

/*##############
#  ON-CREATE() #
################*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor1);

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
                onStopTracking( seekBar, true);
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
                onStopTracking( seekBar, false);
            }
        });
    }
}
