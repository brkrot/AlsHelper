package com.example.alshelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.example.alshelper.bluetoothUtils.*;
import com.example.alshelper.sensors.*;
import com.example.alshelper.processingData.Form;

public class MainActivity extends AppCompatActivity {


    private ImageView bt;
    Button connectBT;
    Button disconnect;
    Button sensors;
    Button form;
    private Menu menu = null;
    private SharedPreferences sharedPreferences;

    public void connectBT() {
        Intent intent = new Intent(this, PairedDevicesList.class);
        startActivity(intent);
    }

    private void diconnectBT() {
        Log.i("DISCCONCT", "bla");
        AppBase.INSTANCE.bluetoothConnector.disconnectBT();
        bt.animate().alpha(0.1f).setDuration(500);
        connectBT.setEnabled(true);
        disconnect.setEnabled(false);
        //form.setEnabled(false);
        sensors.setEnabled(false);
    }

    public void createHelpingSystem(View v) {
        Intent intent = new Intent(this, Form.class);
        startActivity(intent);
    }

    public void goToSensorMenu(View v) {
        if (AppBase.INSTANCE.isBluetoothConnected) {
            Intent intent = new Intent(this, SensorMenu.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Context context;
        sharedPreferences = getSharedPreferences(
                getPackageName()+"_preferences", Context.MODE_PRIVATE);
        bt = (ImageView) findViewById(R.id.bluetoothImageView);
        bt.setAlpha(0.1f);
        connectBT = findViewById(R.id.connectButton);
        disconnect = findViewById(R.id.disconnectButton);
        form = findViewById(R.id.formBtn);
        sensors = findViewById(R.id.sensorsButton);

        disconnect.setEnabled(false);
        // form.setEnabled(false);
        sensors.setEnabled(false);


        findViewById(R.id.bluetoothImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppBase.INSTANCE.isBluetoothConnected) {
                    connectBT();
                } else {
                    diconnectBT();
                }
            }
        });

        findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diconnectBT();
            }
        });

        findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBT();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppBase.INSTANCE.bluetoothConnector != null) {
            bt.animate().alpha(1f).setDuration(2000);
            disconnect.setEnabled(true);
            //form.setEnabled(true);
            sensors.setEnabled(true);
            connectBT.setEnabled(false);
        }

        boolean debugMode = sharedPreferences.getBoolean("debug_mode", false);
        Log.i("Debug mode",String.valueOf(debugMode));
        if(menu!=null){
            if (debugMode) {
                showOption(R.id.terminalItem);
            } else {
                hideOption(R.id.terminalItem);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        boolean debugMode = sharedPreferences.getBoolean("debug_mode", false);
        Log.i("Debug mode",String.valueOf(debugMode));
        if (debugMode) {
            showOption(R.id.terminalItem);
        } else {
            hideOption(R.id.terminalItem);
        }

        return true;
    }

    public void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    public void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bluetoothItem:
                if (!AppBase.INSTANCE.isBluetoothConnected) {
                    connectBT();
                } else {
                    diconnectBT();
                }
                return true;
            case R.id.terminalItem:
                intent = new Intent(this, Terminal.class);
                startActivity(intent);
                return true;
            case R.id.MakeCodeItem:
                intent = new Intent(this, Form.class);
                startActivity(intent);
                return true;
            case R.id.SensorMenuItem:
                intent = new Intent(this, SensorMenu.class);
                startActivity(intent);
                return true;
            case R.id.aboutItem:
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
            case R.id.settingsItem:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
