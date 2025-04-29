package com.example.alshelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class HelpingActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;

    private SharedPreferences prefs;
    private BroadcastReceiver receiver = null;
    /*call*/
    private Button button_EditNumberToCall;
    private EditText numberToCall;

    private Button button_EditNumberToSMS;
    private EditText numberToSMS;

    private Button button_EditTextToSMS;
    private EditText textToSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helping);


        prefs = getSharedPreferences(getPackageName() + "_communication", 0); // 0 - for private mode

        button_EditNumberToCall = (Button) findViewById(R.id.PersonNumberEditTextButton);
        numberToCall = findViewById(R.id.PersonNumberEditText);
        numberToCall.setText(prefs.getString("CALL_NUMBER", ""));


        button_EditTextToSMS = findViewById(R.id.SMS_ContentEditTextButton);
        textToSMS = findViewById(R.id.SMS_ContentEditText);
        textToSMS.setText(prefs.getString("SMS_CONTENT", ""));


        button_EditNumberToSMS = findViewById(R.id.SMSeditPhoneNumberButton);
        numberToSMS = findViewById(R.id.SMSPersonNumberEditText);
        numberToSMS.setText(prefs.getString("SMS_NUMBER", ""));


        // here in onCreate()


    }


    public void editCallNumber(View v) {
        if (v.getTag().toString().equals("1")) {
            button_EditNumberToCall.setText("Lock Number");
            numberToCall.setEnabled(true);
            v.setTag(0);
        } else {
            numberToCall.setEnabled(false);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("CALL_NUMBER", numberToCall.getText().toString());
            editor.apply();
            button_EditNumberToCall.setText("Edit Number");
            v.setTag(1);
        }


    }

    public void editSMSText(View v) {
        if (v.getTag().toString().equals("1")) {
            button_EditTextToSMS.setText("Lock Text");
            textToSMS.setEnabled(true);
            v.setTag(0);
        } else {
            textToSMS.setEnabled(false);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("SMS_CONTENT", textToSMS.getText().toString());
            editor.apply();
            button_EditTextToSMS.setText("Edit Message");
            v.setTag(1);
        }


    }

    public void editSMSNumber(View v) {
        if (v.getTag().toString().equals("1")) {
            button_EditNumberToSMS.setText("Lock Number");
            numberToSMS.setEnabled(true);
            v.setTag(0);
        } else {
            numberToSMS.setEnabled(false);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("SMS_NUMBER", numberToSMS.getText().toString());
            editor.apply();
            button_EditNumberToSMS.setText("Edit Number");
            v.setTag(1);
        }


    }

    public void sendSMS(View view) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CALL_NUMBER", numberToCall.getText().toString());
        editor.putString("SMS_CONTENT", textToSMS.getText().toString());
        editor.putString("SMS_NUMBER", numberToSMS.getText().toString());
        editor.apply();


        String destinationAddress = numberToSMS.getText().toString();
        String smsMessage = textToSMS.getText().toString();

        if (ContextCompat.checkSelfPermission(HelpingActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelpingActivity.this,
                    new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {


            // Set the service center address if needed, otherwise null.
            String scAddress = null;
            // Set pending intents to broadcast
            // when message sent and when delivered, or set to null.
            PendingIntent sentIntent = null, deliveryIntent = null;
            // Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage
                    (destinationAddress, scAddress, smsMessage,
                            sentIntent, deliveryIntent);
            unregisterReceiver(receiver);
            receiver = null;
            onResume();
        }
    }

    public void makePhoneCall(View view) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CALL_NUMBER", numberToCall.getText().toString());
        editor.putString("SMS_CONTENT", textToSMS.getText().toString());
        editor.putString("SMS_NUMBER", numberToSMS.getText().toString());
        editor.apply();


        String number = numberToCall.getText().toString();
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(HelpingActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HelpingActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(HelpingActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Context context;


                View v = new View(this);
                makePhoneCall(v);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                if (!intent.getAction().equals("broadcast_data_from_bluetooth")) return;
                String data = intent.getStringExtra("incoming");
                if (data == null) return;


                Log.i("data", data);
                View v = new View(HelpingActivity.this);
                if (data.equals("SMS")) {
                    unregisterReceiver(receiver);
                    AppBase.INSTANCE.bluetoothConnector.stopReadingData();
                    sendSMS(v);
                } else if (data.equals("Call")) {
                    unregisterReceiver(receiver);
                    AppBase.INSTANCE.bluetoothConnector.stopReadingData();
                    makePhoneCall(v);
                }
            }
        };

        registerReceiver(receiver, new IntentFilter("broadcast_data_from_bluetooth"));

        if (AppBase.INSTANCE.isBluetoothConnected) {
            AppBase.INSTANCE.bluetoothConnector.readDataRepeating();
        } else {
            TextView noBT = new TextView(this);
            noBT.setText("You dont Have \n Bluetooth conection");
            noBT.setTextColor(Color.RED);
            noBT.setTextSize(30);
            noBT.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            LinearLayout linearLayout = findViewById(R.id.baseLinearLayout);
            linearLayout.addView(noBT, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CALL_NUMBER", numberToCall.getText().toString());
        editor.putString("SMS_CONTENT", textToSMS.getText().toString());
        editor.putString("SMS_NUMBER", numberToSMS.getText().toString());
        editor.apply();

        //if(unregisterReceiver(
        receiver = null;
        if (AppBase.INSTANCE.isBluetoothConnected) {
            AppBase.INSTANCE.bluetoothConnector.stopReadingData();
        }

    }

}

