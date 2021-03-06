package com.example.alshelper.bluetoothUtils;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alshelper.AppBase;
import com.example.alshelper.MainActivity;
import com.example.alshelper.processingData.Form;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class BluetoothConnector {

    private final static int BUFFER_SIZE = 256;

    private final static int READING_DATA_GAP = 300;

    Context context;

    // todo LEARN - executor
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // todo LEARN - atomic boolean
    public AtomicBoolean isReadingData = new AtomicBoolean(false);

    /*private*//*todo is it sould be public? what indicator do i have to bluetooth connection?*/
    public BluetoothSocket btSocket = null;

    private final String address;

    private BluetoothAdapter myBluetooth = null;

    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Activity connectingToBluetooth;

    private InputStream inputStream = null;

    /*----------------------------------------------------------------------*/
    /*Constructor*/

    public BluetoothConnector(Context context, Activity activity, String address) {
        this.context = context.getApplicationContext();
        this.address = address;
        connectingToBluetooth = activity;
        connectBT();
    }

    /*----------------------------------------------------------------------*/
    /*connectBT - Establishing new BlueTooth connection*/
    private void connectBT() {
        new ConnectBT().execute(); //Call the class to connect
    }

    //todo what if some disconnect, how do I re-connect?
    /*----------------------------------------------------------------------*/
    /*readDataRepeating*/
    public void readDataRepeating() {
        isReadingData.set(true);        //We set the boolean var as we staert to read the data from the BT

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // here we are in the executor thread!
                if (isReadingData.get() != true) {      //This is the stop condition
                    return;
                }

                try {
                    if (inputStream == null) {
                        inputStream = btSocket.getInputStream();
                    }
                    readDataOnceAndScheduleFuture(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*----------------------------------------------------------------------*/
    /*Repeat reading data*/
    private void readDataOnceAndScheduleFuture(InputStream inputStream) {
        String incomingData = null;

        if (isReadingData.get() != true) {
            /*try {
                inputStream.close(); // todo ERR catch exceptions ...
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            return;
        }

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            if (inputStream.available() > 0) {
                inputStream.read(buffer);
                int i;
                /*
                 * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                 */
                for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                }/*TODO*/
                String tempStr = new String(buffer, 0, i);
                incomingData = tempStr;  //todo erase the double use of strings
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendDataBroadcast(incomingData);

        try {
            Thread.sleep(READING_DATA_GAP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readDataOnceAndScheduleFuture(inputStream);
    }

    /*----------------------------------------------------------------------*/
    // todo LEARN- broadcasts and receivers
    /*Broadcast to the world*/
    private void sendDataBroadcast(String data) {
        Intent intent = new Intent();
        intent.setAction("broadcast_data_from_bluetooth");
        intent.putExtra("incoming", data);
        context.sendBroadcast(intent);
    }

    /*----------------------------------------------------------------------*/
    public void stopReadingData() {
        isReadingData.set(false);
        // writeToArduino("S0");
    }

    /*----------------------------------------------------------------------*/
    //Writing some data out to the arduino
    public void writeToArduino(String s) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(s.getBytes());
            } catch (IOException e) {
                msg("Error", 1);
            }
        }

    }

    /*----------------------------------------------------------------------*/
    public void disconnectBT() {

        new DisConnectBT().execute();
    }

    //An easy way to make Toasts messsages
    private void msg(String s, int l) {
        if (l == 1) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *
     *
     ********************************* INNER CLASSES*********************
     *
     */

    //*inner class for BT connection*//
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        private ProgressDialog progress;

        /*----------------------------------------------------------------------*/
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(connectingToBluetooth, "Connecting to Bluetooth...", "Please wait!!!");  //show a progress dialog
        }

        /*----------------------------------------------------------------------*/
        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !AppBase.INSTANCE.isBluetoothConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed :( \n Try again.", 1);

            } else {
                msg("Connected.", 1);
                AppBase.INSTANCE.isBluetoothConnected = true;
            }

            SharedPreferences pref =
                    connectingToBluetooth.getSharedPreferences
                            (connectingToBluetooth.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("LAST_BT_DEVICE_ADDRESS",address);
            editor.apply();

            progress.dismiss();//todo progress bar
           if(!connectingToBluetooth.getLocalClassName().equals("MainActivity")){
               connectingToBluetooth.finish();
           }else {
              /*Intent intent = new Intent(connectingToBluetooth, MainActivity.class);
               connectingToBluetooth.finish();
               connectingToBluetooth.startActivity(intent);*/
           }
        }
    }


    private class DisConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... devices) {

            if (isReadingData.get() == true) {
                stopReadingData();
            }

            try {
                btSocket.close();
                AppBase.INSTANCE.isBluetoothConnected = false;
                //msg("Disconnected", 1);
            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
