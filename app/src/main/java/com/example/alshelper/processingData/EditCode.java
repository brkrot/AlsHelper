package com.example.alshelper.processingData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.alshelper.R;
public class EditCode extends AppCompatActivity {

    String fileName;
    File outputArduinoFile;
    String content;
    EditText mEditText;
    Map<String, String> actions;
    ArrayList chosenAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_code);

        /*This is the input from the user This is posted from the form*/

        chosenAction = (ArrayList<String>) getIntent().getStringArrayListExtra(getString(R.string.chosen_action_for_arduino));

        fileName = chosenAction.get(0)+".txt";
        /*outputArduinoFile = new File("data/data/com.example.testingtxtfiles/A.txt");

        if (!outputArduinoFile.exists()) {
            try {
                outputArduinoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


        content="";
        mEditText = findViewById(R.id.edit_text);

        {
            /*Setting a dictionary ,each codeword equal to some code in arduino*/
            actions = new HashMap<>();
            String[] helpingSystemActions = getResources().getStringArray(R.array.helpingSystemActions);
            String[] helpingSystemActionsCodes = getResources().getStringArray((R.array.helpingSystemActionsCodes));
            for(int i=0;i<helpingSystemActions.length;i++){
                actions.put(helpingSystemActions[i],helpingSystemActionsCodes[i]);
            }

        }
    }

    public void share(View v) {
       /* // Set up an Intent to send back to apps that request a file
        resultIntent =
                new Intent("com.example.myapp.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        privateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        imagesDir = new File(privateRootDir, "images");
        // Get the files in the images subdirectory
        imageFiles = imagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);*/
        /*

        System.out.println();
        String path = getBaseContext().getFilesDir().getAbsolutePath();

        File requestFile = new File(fileName);
        Uri fileUri = FileProvider.getUriForFile(
                EditCode.this,
                "com.example.testingtxtfiles.fileprovider",
                requestFile);
        if(fileUri != null){
            Intent resultIntent;
            resultIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType("*//**");//URLConnection.guessContentTypeFromName(file.getName()));*/
       /* intentShareFile.putExtra(Intent.EXTRA_STREAM,fileUri);

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));*/

    }

    public void load(View v) {
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {

            //Opening an input stream...
            InputStream inputStream = getAssets().open(chosenAction.get(0)+".ino");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = bufferedReader.readLine()) != null) {
                sb.append(text).append("\n");
            }
            content  = sb.toString();
            mEditText.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void replace(View v){

        String arduinoDataSensor = chosenAction.get(0).toString();

        if(arduinoDataSensor.equals(getString(R.string.sensor_joystick))){    //Joystick

            for(int i=1;i<=4;i++){
                content = content.replace
                        ("$"+Integer.toString(i-1),
                                actions.get(chosenAction.get(i)));
            }
            mEditText.setText(content.toString());

            Toast.makeText(this, "The code was changed",
                    Toast.LENGTH_LONG).show();
        }else if(arduinoDataSensor.equals(getString(R.string.sensor_potentiometer))){

        }else if(arduinoDataSensor.equals(getString(R.string.sensor_button))){

        } else if(arduinoDataSensor.equals(getString(R.string.sensor_magnet))){

        }
    }

    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            //outputArduinoFile = new File(getFilesDir()+"/"+fileName);
            mEditText.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + fileName,
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
