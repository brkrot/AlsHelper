package com.example.alshelper.processingData;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import com.example.alshelper.AppBase;
import com.example.alshelper.R;

public class TestResults extends AppCompatActivity {

    private Integer joystickSumOfActions = 0;
    private Integer analogSumOfActions = 0;
    private Integer onOffSumOfActions = 0;
    private TextView JoystickTestVal;
    private TextView onOffTestVal;
    private TextView morsButtonTestVal;
    private TextView analogTestVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);
        Log.i("TestResults", "onCreate");
        JoystickTestVal = (TextView) findViewById(R.id.JoystickTestVal);
        onOffTestVal = (TextView) findViewById(R.id.OnOffTestVal);
        morsButtonTestVal = (TextView) findViewById(R.id.morsButtonTestVal);
        analogTestVal = (TextView) findViewById(R.id.AnalogTestVal);
        for (Map.Entry<String, Integer> set : AppBase.INSTANCE.diagnosticData.dataMap.entrySet()) {
            System.out.println(set.getKey() + " = " + set.getValue());
            String sensor_and_attribute_key[] = set.getKey().split(" ", 2);
            switch (sensor_and_attribute_key[0]){
                case("JOYSTICK"):
                    present_joystick_ability(sensor_and_attribute_key[1], set.getValue());
                    break;
                case("ONOFF"):
                    present_on_off_ability(sensor_and_attribute_key[1], set.getValue());
                    break;
                case("ANALOG"):
                    present_analog_ability(sensor_and_attribute_key[1], set.getValue());
                    break;
                default:
                    break;
        }
    }


}


    public void createHelpingSystem(View v){
        Intent intent = new Intent(this, Form.class);
        startActivity(intent);
    }
    private void present_joystick_ability(String ability, Integer value) {
        if (value != 0){
            joystickSumOfActions += 1;
            JoystickTestVal.setText((joystickSumOfActions).toString());
        }
    }
    private void present_analog_ability(String ability, Integer value) {
        if (value != 0) {
            analogSumOfActions += 1;
            analogTestVal.setText(analogSumOfActions.toString());
        }
    }
    private void present_on_off_ability(String ability, Integer value) {
        if (value != 0) {
            onOffSumOfActions += 1;
            onOffTestVal.setText(onOffSumOfActions.toString());
        }
    }

    }