package com.projetinfo.android.watchint10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    //
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private Button turnOnBlue;
    private Button turnOffBlue;
    private Button listBtn;
    private Button searchBtn;
    private Button synchroBtn;
    private Button turnOnLed;
    private Button turnOffLed;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // CHECK FOR PRESENCE OF BLUETOOTH ON THE DEVICE

        // The static method getDefaultAdapter() enables us to get the BluetoothAdapter that represents the device
        //If it returns null, that means that the device does not support Bluetooth

        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            turnOnBlue.setEnabled(false);
            turnOffBlue.setEnabled(false);
            listBtn.setEnabled(false);
            searchBtn.setEnabled(false);
            synchroBtn.setEnabled(false);
            turnOnLed.setEnabled(false);
            turnOffLed.setEnabled(false);
            text.setText("Status: not supported");
        }
        else {
            text = (TextView) findViewById(R.id.text);
            turnOnBlue = (Button)findViewById(R.id.turnOnBluetooth);
            turnOnBlue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    on(v);
                }
            });
            turnOffBlue = (Button) findViewById(R.id.turnOffBluetooth);
            listBtn = (Button) findViewById(R.id.list_device);
            searchBtn = (Button) findViewById(R.id.search);


        }


        // ENABLE BLUETOOTH

        // isEnabled() returns false or true whether the Bluetooth is enabled or not
        //If it is desabled, we enable it by calling startActivityForresult() and a Bluetooth dialog will appear to ask for turning it on
        //If BLuetooth is enabled, there will be a "RESULT_OK", otherwise a "RESULT CANCELED"
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }


        //RETRIEVAL OF KNOWN DEVICES
        //Before looking for new devices, we need to check the known devices by calling getBondedDevices() which returns a list of BluetoothDevice
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }



    //DISCOVERY OF NEW DEVICES
    //startDiscovery() enables to start discovering devices ( a scan evry 12 seconds)


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            }


    };

    public void on(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BLUETOOTH);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        unregisterReceiver(mReceiver);
    }
}





