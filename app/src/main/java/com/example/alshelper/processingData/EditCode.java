package com.example.alshelper.processingData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

    private String outputFileName;
    private File outputArduinoFile;
    private String textContent;
    private EditText mEditText;
    private Map<String, String> allPossibleActions;
    private ArrayList allChosenActions;
    private String patientName = "", chosenSensor = "";
    private int numberOfActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_code);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        boolean debugMode = prefs.getBoolean("debug_mode", false);

        /*Getting the info from the caller intent*/
        Intent in;
        in = getIntent();
        chosenSensor = in.getStringExtra("CHOSEN_SENSOR");
        allChosenActions = (ArrayList<String>) in.getStringArrayListExtra(getString(R.string.chosen_action_for_arduino));
        patientName = in.getStringExtra("PATIENT_NAME");
        numberOfActions = in.getIntExtra("NUMBER_OF_ACTIONS", 1);

        outputFileName = patientName + "_Personal_Code_" + chosenSensor + ".ino";
        textContent = "";
        mEditText = findViewById(R.id.edit_text);

        /*Setting a dictionary ,each codeword equal to some code in arduino*/
        allPossibleActions = new HashMap<>();

        //Getting the actions names
        String[] helpingSystemActionsNames = getResources().getStringArray(R.array.helpingSystemActions);

        //getting the actions codes
        String[] helpingSystemActionsCodes = getResources().getStringArray((R.array.helpingSystemActionsCodes));

        /*Combining the names and the actual code lines to call any action, as pairs in hashmap*/
        for (int i = 0; i < helpingSystemActionsNames.length; i++) {
            allPossibleActions.put(helpingSystemActionsNames[i], helpingSystemActionsCodes[i]);
        }
        /* Do this "Shortcut" when we are not in debugging mode */
        if (!debugMode) {
            View v = new View(this);
            load(v);
            replace(v);
            save(v);
            share(v);
        }
    }

    /*
    loading the content of the blank file to the screen*/
    public void load(View v) {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            //Opening an input stream...
            InputStream inputStream = getAssets().open("Blank" + chosenSensor + ".ino");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String text;

            while ((text = bufferedReader.readLine()) != null) {
                stringBuilder.append(text).append("\n");
            }
            textContent = stringBuilder.toString();
            mEditText.setText(stringBuilder.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void replace(View v) {

        //Replace the generic name with the patient name
        textContent = textContent.replace("#PATIENT_NAME", patientName);

        String codeLine = "";
        for (int i = 1; i <= numberOfActions; i++) {

            /*Get the action code line corresponding to the chosen action at [i-1]*/
            /*example - the user chose "turn A/C, the corresponding action code is ="airCondition();" */
            codeLine = allPossibleActions.get(allChosenActions.get(i - 1));

            //Replace the code at the aimed place
            textContent = textContent.replace("//$" + i, codeLine);
        }
        mEditText.setText(textContent);
        Log.i("New File", textContent);

        Toast.makeText(this, "The code was changed", Toast.LENGTH_LONG).show();

    }

    public void save(View v) {
        String text = mEditText.getText().toString();
        File files = getFilesDir();
        File codesFolder = new File(files, "codes");
        File newFile = new File(codesFolder, outputFileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(newFile, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream.write(text.getBytes());
            //outputArduinoFile = new File(getFilesDir()+"/"+fileName);
            //mEditText.getText().clear();
            Toast.makeText(this, "Saved to " + newFile.getAbsolutePath(),
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

    public void share(View v) {

        File privateRootDir = getFilesDir();
        File codesDir;
        codesDir = new File(privateRootDir, "codes");
        File[] codesFiles;
        codesFiles = codesDir.listFiles();
        File myCode = new File(codesDir, outputFileName);
        if (myCode.exists()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            File requestFile = new File(myCode.getAbsolutePath());//"/data/user/0/com.example.alshelper/files/codes/test2.txt");
            Uri fileUri = null;
            try {
                fileUri = FileProvider.getUriForFile(EditCode.this, "com.example.alshelper.fileprovider", requestFile);
            } catch (IllegalArgumentException e) {
                Log.e("File Selector",
                        "The selected file can't be shared: " + requestFile.toString());
            }


            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            //sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");

            startActivity(Intent.createChooser(sendIntent, "Share image via..."));
        }


    }
}
