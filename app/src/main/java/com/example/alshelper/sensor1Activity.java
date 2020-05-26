package com.example.alshelper;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class sensor1Activity extends AppCompatActivity {

    static final int SEEK_BAR_HIGH_RANGE = 100;

    static final int SEEK_BAR_MEDIUM_RANGE = 50;

    static final int SEEK_BAR_LOW_RANGE = 24;

    static int currentLevel = 2;

    static int currentRange = SEEK_BAR_MEDIUM_RANGE;

    public void identifyLevelRange(int level){
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

    public void changeLevel(View view){
        if (view.getId() == R.id.levelUp & currentLevel < 3){
            currentLevel++;
            identifyLevelRange(currentLevel);
        }
        else if  (view.getId() == R.id.levelDown & currentLevel > 1){
            currentLevel--;
            identifyLevelRange(currentLevel);
        }
        else {
            Toast.makeText(getApplicationContext(),"You are already in the edge level",Toast.LENGTH_SHORT).show();
        }
        SeekBar horizenBar = (SeekBar) findViewById(R.id.horizenSeekBar);
        SeekBar verticalBar = (SeekBar) findViewById(R.id.verticalSeekBar);
        Log.i("change level","change level");
        horizenBar.setMax(currentRange);
        verticalBar.setMax(currentRange);
        horizenBar.setProgress(currentRange/2);
        verticalBar.setProgress(currentRange/2);
    }

    public void onStopTracking(SeekBar seekBar, boolean hDirect){
        int moveSteps = currentRange/2-seekBar.getProgress();
        int location;
        ImageView dot = (ImageView) findViewById(R.id.dot);

        Rect myViewRect = new Rect();
        dot.getGlobalVisibleRect(myViewRect);
        if (hDirect){
            location = myViewRect.left;;
        }
        else{
            location = myViewRect.top;
        }
        Log.i("the dot placed on" , Integer.toString(location));

        if (location + moveSteps > 0){
            if(hDirect){
                dot.animate().translationXBy(moveSteps);
            }
            else {
                dot.animate().translationYBy(moveSteps);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"You are in the edge",Toast.LENGTH_SHORT).show();
        }
    }

    public void restartSeek(View view){
        SeekBar horizenBar = (SeekBar) findViewById(R.id.horizenSeekBar);
        SeekBar verticalBar = (SeekBar) findViewById(R.id.verticalSeekBar);
        horizenBar.setProgress(currentRange/2);
        verticalBar.setProgress(currentRange/2);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor1);

        ImageView axis = (ImageView) findViewById(R.id.xyAxis);
        Rect myViewRect = new Rect();
        axis.getGlobalVisibleRect(myViewRect);
        int x = myViewRect.left;
        int y = myViewRect.top;

        int width = axis.getDrawable().getIntrinsicWidth();
        int height = axis.getDrawable().getIntrinsicHeight();

        Log.i("the dot placed on" , Integer.toString(width) + " height " + Integer.toString(height) + " center " + Integer.toString(x) + "  " + Integer.toString(y));

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
