package com.example.carapp.ViewModels;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.Adapters.BluetoothDeviceAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BluetoothViewModel extends ViewModel {
    // LiveData instances
    public final MutableLiveData<Boolean> bluetoothEnabled = new MutableLiveData<>();
    public final MutableLiveData<Boolean> bluetoothConnected = new MutableLiveData<>();
    private BluetoothDeviceAdapter deviceAdapter;
    private Set<BluetoothDevice> discoveredDevices = new HashSet<>();

    // Add a new discovered device to the list
    public void addDiscoveredDevice(BluetoothDevice device) {

        // Check to ensure that there are no duplicates
        if (!discoveredDevices.contains(device) && deviceAdapter != null) {
            discoveredDevices.add(device);
            deviceAdapter.add(device);
            deviceAdapter.notifyDataSetChanged();
        }

    }

    // Clear the list of discovered devices
    public void clearDiscoveredDevices() {
       deviceAdapter.clear();
       discoveredDevices.clear();
       deviceAdapter.notifyDataSetChanged();
    }

    public BluetoothDeviceAdapter getDeviceAdapter(Activity activity) {
        if (deviceAdapter == null) {
            deviceAdapter = new BluetoothDeviceAdapter(activity, new ArrayList<>());
        }
        return deviceAdapter;
    }

    // Set Bluetooth connection status
    public void setBluetoothConnected(boolean status) {
        bluetoothConnected.postValue(status);
    }

    // Set Bluetooth enabled
    public void setBluetoothEnabled(boolean status) {
        bluetoothEnabled.setValue(status);
    }

}
