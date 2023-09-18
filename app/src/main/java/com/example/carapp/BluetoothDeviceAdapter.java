package com.example.carapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @SuppressLint("MissingPermission")

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent, false);
        }

        // Lookup view for data population
        TextView deviceNameTextView = convertView.findViewById(R.id.deviceNameTextView);
        TextView deviceAddressTextView = convertView.findViewById(R.id.deviceAddressTextView);

        // Populate the data into the template view using the data object
        if (device != null) {
            deviceNameTextView.setText(device.getName());
            deviceAddressTextView.setText(device.getAddress());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

