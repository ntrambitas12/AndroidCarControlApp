package com.example.carapp.ViewModels;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.carapp.Adapters.BluetoothDeviceAdapter;
import com.example.carapp.VehicleConnections.BluetoothConnection;
import com.example.carapp.VehicleConnections.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* This helper class contains the functions and logic necessary for finding
 and connecting to the car's Bluetooth device. Once the Bluetooth Adapter is paired,
 this class will be garbage-collected. The connectionManager viewModel should be used instead*/
public class BluetoothSearchHelper implements ConnectionManager.ConnectionManagerCallback{
    // LiveData instances
    private BluetoothDeviceAdapter deviceAdapter;
    private Set<BluetoothDevice> discoveredDevices = new HashSet<>();
    public final MutableLiveData<String> VINLiveData = new MutableLiveData<>("");
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> VINSearchCallbackFuture;
    private boolean isConfirmingVIN = false;
    private Observer<Boolean> btConnectedObserver;
    private Observer<BluetoothDevice> btDiscoveredObserver;
    private ConnectionManager connectionManager;

     public BluetoothSearchHelper(ConnectionManager connectionManager) {
         this.connectionManager = connectionManager;
         connectionManager.addObserver(this);

         // Create Observers
          btConnectedObserver = connected -> {
              if (!connected) {
                  VINLiveData.setValue("");
              }
          };

          btDiscoveredObserver = bluetoothDevices -> addDiscoveredDevice(bluetoothDevices);

          // Observe for changes in the observers
         BluetoothConnection.BTConnectedToPeripheral.observeForever(btConnectedObserver);
         BluetoothConnection.discoveredDevices.observeForever(btDiscoveredObserver);
     }

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



    /* This function starts a callback to be executed after 60 seconds of trying to communicate with
     * the vehicle. The callback is designed so that it can be prematurely canceled if the VIN is read
     * before the time limit is reached  */
    public void startVINSearch() {
        // Ensure that there isn't already a search started
        if (VINSearchCallbackFuture == null || VINSearchCallbackFuture.isDone()) {
            isConfirmingVIN = true;
            VINSearchCallbackFuture = executorService.schedule(() -> {
                // Post "" to indicate VIN not read
                if (VINLiveData.getValue() == "") {
                    VINLiveData.postValue("ERROR");
                }
                isConfirmingVIN = false;
            }, 30, TimeUnit.SECONDS);
        }
    }
    // Call this method once this helper class goes out of scope to prevent memory leaks!
    public void destroyClass() {
        connectionManager.removeObserver(this);
        BluetoothConnection.discoveredDevices.removeObserver(btDiscoveredObserver);
        BluetoothConnection.BTConnectedToPeripheral.removeObserver(btConnectedObserver);
    }
    private void cancelVINSearchCallback() {
        if (VINSearchCallbackFuture != null && !VINSearchCallbackFuture.isDone()) {
            VINSearchCallbackFuture.cancel(false); // Set to false to allow code inside thread to execute if cancelled
        }
    }

    @Override
    public void receivedFromCar(JSONObject state) {
        // Run only if confirming the selected device
        if (isConfirmingVIN) {
            // check if the VIN is set in the JSON object.
            // If it is, post the VIN to the UI and cancel callback
            try {
                String VIN = state.getString("VIN");
                if (VIN.length() > 0) {
                    cancelVINSearchCallback();
                    VINLiveData.postValue(VIN);
                }
            }  catch (JSONException e) {
                // JSON doesn't contain VIN
            }
        }
    }
}
