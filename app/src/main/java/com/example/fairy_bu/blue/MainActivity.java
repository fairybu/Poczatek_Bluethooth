package com.example.fairy_bu.blue;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG="MainActivity";

    BluetoothAdapter myBluethoothAdapter;
    Button button2;
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices =new ArrayList<>();
    public DeviceListAdapter mDevicesListAdapter;
    ListView lvNewDEvices;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReciver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(myBluethoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,myBluethoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"onReceive: STATE OFF ");
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"mBroadcasteReceiver1: STATE Turning OFF ");
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"mBroadcasteReceiver1: STATE ON ");
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"mBroadcasteReceiver1: STATE Turning ON");
                    break;
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReciver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){

                int mode=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);

                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG,"mBroadcasteReceiver2: Discoverability Enabled  ");
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG,"mBroadcasteReceiver2: Discoverability Disabled. Able to receive connections ");
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"mBroadcasteReceiver2: Discoverability Disabled. Not able to receive connection");
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG,"mBroadcasteReceiver2: Connecting...");
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"mBroadcasteReceiver2: Connected");
                        break;
                }
            }
        }
    };

    // Broadcast Receiver for listing devices that are not yet paired
    private BroadcastReceiver mBroadcastReciver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
             final String action = intent.getAction();
            Log.d(TAG,"onReceive: ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG,"onReceive: " + device.getName() +": "+ device.getAddress());
                mDevicesListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_viev,mBTDevices);
                lvNewDEvices.setAdapter(mDevicesListAdapter);
                
            }
        }
    };

    private  BroadcastReceiver mBroadcastReciver4=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action=intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //bonded already
                if(mDevice.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.d(TAG,"BroadcastReceiver: BOND_BOUNDED");
                    mBTDevice=mDevice;
                }
                //creating a bond
                if(mDevice.getBondState()==BluetoothDevice.BOND_BONDING){
                    Log.d(TAG,"BroadcastReceiver: BOND_BOUNDING");
                }
                //breaking a bond
                if(mDevice.getBondState()==BluetoothDevice.BOND_NONE){
                    Log.d(TAG,"BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy: called");
        super.onDestroy();
        unregisterReceiver(mBroadcastReciver1);
        unregisterReceiver(mBroadcastReciver2);
        unregisterReceiver(mBroadcastReciver3);
        unregisterReceiver(mBroadcastReciver4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btOnOFF = (Button) findViewById(R.id.button);
        button2=(Button) findViewById(R.id.button2);
        lvNewDEvices=(ListView) findViewById(R.id.lvNewDEvices);
        mBTDevices =new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReciver4,filter);

        myBluethoothAdapter= BluetoothAdapter.getDefaultAdapter();

        lvNewDEvices.setOnItemClickListener(MainActivity.this);

        btOnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onCLick: enabling/disabling BT");
                enableDisableBT();
            }
        });
    }






    public void enableDisableBT(){

        if(myBluethoothAdapter==null){
            Log.d(TAG,"enableDisableBT : Does not have BT capabilities.");
        }
        if(myBluethoothAdapter.isEnabled()){
            Log.d(TAG,"enableDisableBT: enabling BT");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciver1,BTintent);
        }
        if(myBluethoothAdapter.isEnabled()){
            Log.d(TAG,"enableDisableBT: disabling BT");
            IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciver1,BTintent);

        }
    }


    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG,"btnEnableDisable_Discoverable: Making device discoverable for 300 s");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter=new IntentFilter(myBluethoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReciver2,intentFilter);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {
        Log.d(TAG,"btnDiscover: Looking for unpaired devices");

        if(myBluethoothAdapter.isDiscovering()){
            myBluethoothAdapter.cancelDiscovery();
            Log.d(TAG,"btnDiscover: Canceling discovery");

            checkBTpermission();

            myBluethoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent= new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReciver3,discoverDevicesIntent);
        }

        if(!myBluethoothAdapter.isDiscovering()){

            checkBTpermission();
            myBluethoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent =new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReciver3,discoverDevicesIntent);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTpermission(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck= this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck!=0){
                this.requestPermissions(new String []{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }else{
                Log.d(TAG,"checkPermission: NO need persmission. SDK version< LOLIPOP");
            }


        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        // first cancel discovery because its very memory intensive
        myBluethoothAdapter.cancelDiscovery();

        Log.d(TAG,"onClick :  You Clicked on a devices");
        String deviceName=mBTDevices.get(i).getName();
        String deviceAddress=mBTDevices.get(i).getAddress();

        Log.d(TAG,"onClick:  deviceName = "+deviceName);
        Log.d(TAG,"onClick:  deviceName = "+deviceAddress);

        //create the band
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG,"Trying to pair with" + deviceName);
            mBTDevices.get(i).getBondState();

            mBTDevice = mBTDevices.get(i);
        }

    }
}

