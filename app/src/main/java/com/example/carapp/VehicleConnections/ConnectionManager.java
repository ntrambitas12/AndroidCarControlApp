package com.example.carapp.VehicleConnections;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager extends AndroidViewModel {

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  WebConnection WebLink;
    private boolean BTConnected;
    private boolean BTPowerState;
    private final MutableLiveData<JSONObject> carResponse = new MutableLiveData<>();
    private final int refreshInterval = 1000; // Adjust this to change refresh interval
    private LiveData<JSONObject> resp = null;

    public ConnectionManager(Application application) {
        super(application);
        // Initialize connections
        WebLink = new WebConnection(
                "ENTER WEB URL HERE");
        BluetoothLink = new BluetoothConnection(application.getApplicationContext());
        // Get initial values
        BTPowerState = BluetoothConnection.BTPowerState.getValue();
        BTConnected = BluetoothConnection.BTConnectedToPeripheral.getValue();
        registerCallbacks();

        Handler handler = new Handler();

        // Create method to call receiveFromCar automatically based on refreshInterval
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                receiveFromCar();
                handler.postDelayed(this, refreshInterval);
            }
        };
        handler.postDelayed(runnable, refreshInterval);
    }

    // Initialize for both bluetooth and web communication (call only once)
    public void ConnectToCar(String BTMacAddress, String VIN) {
        // VIN can be optional. If using only BT, pass VIN as null
        if (VIN != null) {
            WebLink.setVIN(VIN);
        }

        // Before attempting to connect, I check that the new device I want to connect
        // to isn't the same device as the currently connected device
        if (BTConnected && BluetoothLink.getConnectedDeviceUUID() != BTMacAddress) {
            // If already connected to BT, disconnect first before attempting to connect to another device
            BluetoothLink.endConnection();
        }
        if (BluetoothLink.getConnectedDeviceUUID() != BTMacAddress) {
            BluetoothLink.connectToTargetDevice(BTMacAddress);
        }

    }

    public void startBTScan(boolean pairingMode) {
        BluetoothLink.startScan(pairingMode);
    }



    private void registerCallbacks() {
        // Register the appropriate observers
        BluetoothConnection.BTPowerState.observeForever( new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean powerState) {
                BTPowerState = powerState;
            }
        });

        BluetoothConnection.BTConnectedToPeripheral.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean status) {
                BTConnected = status;
            }
        });

    }

    public void endConnection() {
        if (BluetoothLink != null) {
            BluetoothLink.endConnection();
        }
        if (WebLink != null) {
            WebLink.endConnection();
        }
            BluetoothLink = null;
            WebLink = null;
        }


    public void sendToCar(Command Payload) {
        // If connected via Bluetooth, send via Bluetooth
        if (BluetoothLink != null && BTPowerState && BTConnected) {
            BluetoothLink.sendToCar(Payload);
        } else if (WebLink != null) {
            WebLink.sendToCar(Payload);
        }
    }

    public IBluetooth getBluetoothLink() {
        return BluetoothLink;
    }
    public LiveData<JSONObject> getReceivedFromCarListener() {
        return carResponse;
    }

    private void receiveFromCar() {
        // If connected via Bluetooth, receive via Bluetooth
            if (BluetoothLink != null && BTPowerState && BTConnected) {
                resp = BluetoothLink.receiveFromCar();
            } else if(WebLink != null) {
                resp = WebLink.receiveFromCar();
            }
            if (resp != null) {
                // Observe whenever the car responds back with its status
                resp.observeForever(new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {
                        // Post the new liveData here
                        carResponse.setValue(jsonObject);
                        resp.removeObserver(this); // Prevent memory leaks
                    }

                });
            }
    }
}
