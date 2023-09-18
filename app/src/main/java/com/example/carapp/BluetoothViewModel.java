package com.example.carapp;

import android.bluetooth.BluetoothDevice;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class BluetoothViewModel extends ViewModel {
    // LiveData instances
    public final MutableLiveData<Boolean> bluetoothEnabled = new MutableLiveData<>();
    public final MutableLiveData<List<BluetoothDevice>> discoveredDevices = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<Boolean> bluetoothConnected = new MutableLiveData<>();


    // Add a new discovered device to the list
    public void addDiscoveredDevice(BluetoothDevice device) {
        List<BluetoothDevice> currentList = discoveredDevices.getValue();

        // Check if the currentList doesn't already contain the device
        if (currentList != null && !currentList.contains(device)) {
            currentList.add(device);
            discoveredDevices.setValue(currentList);
        }
    }

    // Clear the list of discovered devices and stop scanning
    public void clearDiscoveredDevices() {
        List<BluetoothDevice> currentList = discoveredDevices.getValue();
        if (currentList != null) {
            currentList.clear();
            discoveredDevices.setValue(currentList);
        }
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
