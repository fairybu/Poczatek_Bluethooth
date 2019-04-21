package com.example.fairy_bu.blue;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutImflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourcesId;

    public DeviceListAdapter(Context context, int tvResourceId,ArrayList<BluetoothDevice> devices){
        super(context,tvResourceId);
        mLayoutImflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourcesId=tvResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        converView=mLayoutImflater.inflate(mViewResourcesId,null);

        BluetoothDevice device= mDevices.get(position);

        if(device!=null){
            TextView deviceName=(TextView) converView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress=(TextView) converView.findViewById(R.id.tvDeviceAddress);

                if(deviceName!=null){
                    deviceName.setText(device.getName());
                }
                if(deviceAdress!=null){
                      deviceAdress.setText(device.getAddress());
                }
        }
        return converView;
    }
}
