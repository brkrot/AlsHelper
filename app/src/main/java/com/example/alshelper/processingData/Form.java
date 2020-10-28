package com.example.alshelper.processingData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alshelper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Form extends AppCompatActivity {

    private int numberOfActions = -1;
    private TextView whatSensorYouChose = null;
    private EditText patientName;
    private Spinner spinner_sensors, s1, s2, s3, s4, allSpinners[];
    ;
    private TextView sensorText, t1, t2, t3, t4;
    private Button send;
    private String[] spinnersIds;
    private ImageView imageView_chosenSensor;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        String sId = "textView_choose_action";
        spinnersIds = new String[]{sId + 1, sId + 2, sId + 3, sId + 4};
        int tempSpinnerId;

        // for (int i = 0; i < spinnersIds.length; i++) {
        // tempSpinnerId = getResources().getIdentifier(spinnersIds[0], "id", getPackageName());
        //s1 = (Spinner) findViewById(tempSpinnerId);
        //}

        allSpinners = new Spinner[4];
        patientName = (EditText) findViewById(R.id.patientName);
        send = (Button) findViewById(R.id.button_send);
        imageView_chosenSensor = (ImageView) findViewById(R.id.imageView_chosenSensor);
        spinner_sensors = (Spinner) findViewById(R.id.spinner_choose_sensor);
        spinner_sensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenSensor = parent.getItemAtPosition(position).toString();
                String sensorJoystick = getString(R.string.sensor_joystick);

                linearLayout = findViewById(R.id.actionsLinearLayout);

               /* if (whatSensorYouChose == null) {
                    whatSensorYouChose = new TextView(Form.this);
                    whatSensorYouChose.setText("You chose the" + chosenSensor);
                    whatSensorYouChose.setTextColor(Color.RED);
                    whatSensorYouChose.setTextSize(20);
                    linearLayout.addView(whatSensorYouChose, 0);
                } else {
                    whatSensorYouChose.setText("You chose the" + chosenSensor);
                }*/

                linearLayout.removeAllViews();

                if (chosenSensor.equals(getString(R.string.sensor_joystick))) {
                    imageView_chosenSensor.setImageResource(R.drawable.joystick);

                    String[] actions = {"Up", "Right", "Down", "Left"};
                    addActions(actions);
                } else if (chosenSensor.equals(getString(R.string.sensor_potentiometer))) {
                    imageView_chosenSensor.setImageResource(R.drawable.potentiometer);

                    String[] actions = {"1/3", "2/3", "3/3"};
                    addActions(actions);

                } else if (chosenSensor.equals(getString(R.string.sensor_button))) {
                    imageView_chosenSensor.setImageResource(R.drawable.button);
                    String[] actions = {"Click", "Double Click"};
                    addActions(actions);

                } else if (chosenSensor.equals(getString(R.string.sensor_name_magnet))) {
                    imageView_chosenSensor.setImageResource(R.drawable.magnet);
                    String[] actions = {"Hit", "Double Hit"};
                    addActions(actions);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /*in this method we add  spinners for the actions*/
    public void addActions(String[] titles) {

        numberOfActions = titles.length;
        for (int i = 0; i < numberOfActions; i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout singleActionLayout = (LinearLayout) inflater.inflate(R.layout.single_action_layout, null, false);

            LinearLayout innerLayout = (LinearLayout) singleActionLayout.getChildAt(0);
            TextView text = (TextView) innerLayout.getChildAt(0);
            text.setText(titles[i]);

            allSpinners[i] = (Spinner) innerLayout.getChildAt(1);
            linearLayout.addView(singleActionLayout);
        }




        /* TextView text = new TextView(this);
        text.setText(title);
        text.setTextSize(36);
        text.setTextColor(Color.BLACK);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Spinner spinner = new Spinner(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.helpingSystemActions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setBackground(getResources().getDrawable(R.drawable.border));
        spinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);



        LinearLayout action1 = new LinearLayout(this);
        action1.setBackground(getResources().getDrawable(R.drawable.border));
        action1.setOrientation(LinearLayout.VERTICAL);


        action1.addView(text);
        action1.addView(spinner);

        linearLayout.addView(action1);


        View seperator = new View(this);
seperator.set
        linearLayout.addView(seperator);*/

    }

    public void send(View v) {
        ArrayList ArduinoCodeData = new ArrayList<String>();
        String chosen_sensor = spinner_sensors.getSelectedItem().toString();

        //We are passing all the four options, in the naxt activity we wil take only some of them
        // ArduinoCodeData.add(chose_sensor);
        String option = "";
        if (numberOfActions == -1 || chosen_sensor.equals(getString(R.string.sensor_blank))) {
            Toast.makeText(this, "You Have to choose a sensor!", Toast.LENGTH_SHORT).show();
            return;
        }


        boolean areAllFiled = true;
        for (int i = 0; i < numberOfActions; i++) {
            if (allSpinners[i].getSelectedItem().toString().equals(getString(R.string.action_blank))) {
                areAllFiled = false;
            }
        }
        if (!areAllFiled) {
            Toast.makeText(this, "You Have to choose all the actions", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < numberOfActions; i++) {
            option = allSpinners[0].getSelectedItem().toString();
            ArduinoCodeData.add(option);
        }


        Intent next = new Intent(this, EditCode.class);
        next.putStringArrayListExtra(getString(R.string.chosen_action_for_arduino), ArduinoCodeData);
        Log.i("name is", patientName.getText().toString());
        next.putExtra("PATIENT_NAME", patientName.getText().toString());
        next.putExtra("CHOSEN_SENSOR", chosen_sensor);
        next.putExtra("NUMBER_OF_ACTIONS",numberOfActions);
        startActivity(next);
    }
}
