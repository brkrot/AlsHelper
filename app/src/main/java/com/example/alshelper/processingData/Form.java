package com.example.alshelper.processingData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.alshelper.R;
import java.util.ArrayList;


public class Form extends AppCompatActivity {


    Spinner spinner_sensors,s1,s2,s3,s4;
    TextView sensorText,t1,t2,t3,t4;
    Button send;
    ImageView imageView_chosenSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        send = (Button) findViewById(R.id.button_send);
        imageView_chosenSensor = (ImageView)findViewById(R.id.imageView_chosenSensor);
        spinner_sensors = (Spinner) findViewById(R.id.spinner_choose_sensor);
        s1 = (Spinner) findViewById(R.id.spinner_choose_action1);
        s2 = (Spinner) findViewById(R.id.spinner_choose_action2);
        s3 = (Spinner) findViewById(R.id.spinner_choose_action3);
        s4 = (Spinner) findViewById(R.id.spinner_choose_action4);

        sensorText = (TextView) findViewById(R.id.textView_choose_sensor);
        t1 = (TextView)findViewById(R.id.textView_choose_action1);
        t2 = (TextView)findViewById(R.id.textView_choose_action2);
        t3 = (TextView)findViewById(R.id.textView_choose_action3);
        t4 = (TextView)findViewById(R.id.textView_choose_action4);

        spinner_sensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosen_sensor = parent.getItemAtPosition(position).toString();
                if(chosen_sensor.equals(getString(R.string.sensor_joystick))){

                    imageView_chosenSensor.setImageResource(R.drawable.joystick);

                    t1.setText("Up");
                    t2.setText("Right");
                    t3.setText("Down");
                    t4.setText("Left");

                    t1.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    s2.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    s3.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.VISIBLE);
                    s4.setVisibility(View.VISIBLE);   //all visible

                } else if (chosen_sensor.equals(getString(R.string.sensor_potentiometer))){
                    imageView_chosenSensor.setImageResource(R.drawable.potentiometer);
                    t1.setText("Potentiometer range 1");
                    t2.setText("Potentiometer range 2");
                    t3.setText("Potentiometer range 3");


                    t1.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    s2.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    s3.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.INVISIBLE);
                    s4.setVisibility(View.INVISIBLE);
                }else if (chosen_sensor.equals(getString(R.string.sensor_button))){
                    imageView_chosenSensor.setImageResource(R.drawable.button);
                    t1.setText("ChOose your action");

                    t1.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    s2.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                    s3.setVisibility(View.INVISIBLE);
                    t4.setVisibility(View.INVISIBLE);
                    s4.setVisibility(View.INVISIBLE);
                }else if (chosen_sensor.equals(getString(R.string.sensor_magnet))){
                    imageView_chosenSensor.setImageResource(R.drawable.magnet);
                    t1.setText("pass threshold");

                    t1.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    s2.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                    s3.setVisibility(View.INVISIBLE);
                    t4.setVisibility(View.INVISIBLE);
                    s4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void send(View v){
        ArrayList ArduinoCodeData = new ArrayList<String>();
        String chose_sensor = spinner_sensors.getSelectedItem().toString();

        //We are passing all the four options, in the naxt activity we wil take only some of them
        ArduinoCodeData.add(chose_sensor);
        String option1  = s1.getSelectedItem().toString();
        ArduinoCodeData.add(option1);
        String option2  = s2.getSelectedItem().toString();
        ArduinoCodeData.add(option2);
        String option3  = s3.getSelectedItem().toString();
        ArduinoCodeData.add(option3);
        String option4  = s4.getSelectedItem().toString();
        ArduinoCodeData.add(option4);

        Intent next = new Intent(this,EditCode.class);
        next.putStringArrayListExtra(getString(R.string.chosen_action_for_arduino),ArduinoCodeData);
        startActivity(next);
    }
}
