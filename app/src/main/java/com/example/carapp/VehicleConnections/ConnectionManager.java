package com.example.carapp.VehicleConnections;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager extends ViewModel {
    public interface ConnectionManagerCallback {
        void receivedFromCar(JSONObject state);
    }

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  IConnection WebLink;
    private boolean BTConnected;
    private boolean BTPowerState;
    private final List<ConnectionManagerCallback> observers = new ArrayList<>();

    // Initialize only bluetooth (call only once)
    public void initialize(IBluetooth BluetoothLink, int refreshInterval) {
        // Set the references to the Bluetooth and Web controllers
        this.BluetoothLink = BluetoothLink;

        // Get initial values
        BTPowerState = BluetoothConnection.BTPowerState.getValue();
        BTConnected = BluetoothConnection.BTConnectedToPeripheral.getValue();

        registerCallbacks();

        // Create method to call receiveFromCar automatically based on refreshInterval
        Handler handler = new Handler();
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
    public void initialize(IBluetooth BluetoothLink, IConnection WebLink, int refreshInterval) {
        // Set the references to the Bluetooth and Web controllers
        this.BluetoothLink = BluetoothLink;
        this.WebLink = WebLink;

        // Get initial values
        BTPowerState = BluetoothConnection.BTPowerState.getValue();
        BTConnected = BluetoothConnection.BTConnectedToPeripheral.getValue();

        registerCallbacks();

        // Create method to call receiveFromCar automatically based on refreshInterval
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                receiveFromCar();
                handler.postDelayed(this, refreshInterval);
            }
        };
        handler.postDelayed(runnable, refreshInterval);
    }
    // If initialized already, use this method to add web connection
    public void addWebLink(IConnection WebLink) {
        this.WebLink = WebLink;
    }

    public void addObserver(ConnectionManagerCallback observer) {
        this.observers.add(observer);
    }
    public void removeObserver(ConnectionManagerCallback observer) {
        this.observers.remove(observer);
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
        if (BluetoothLink != null && WebLink != null)
        // If connected via Bluetooth, send via Bluetooth
        if (BTPowerState && BTConnected) {
            BluetoothLink.sendToCar(Payload);
        } else {
            WebLink.sendToCar(Payload);
        }
    }

    public IBluetooth getBluetoothLink() {
        return BluetoothLink;
    }

    private void receiveFromCar() {
        LiveData<JSONObject> resp;
        // If connected via Bluetooth, receive via Bluetooth
        if (BluetoothLink != null && WebLink != null) {
            if (BTPowerState && BTConnected) {
                resp = BluetoothLink.receiveFromCar();
            } else {
                resp = WebLink.receiveFromCar();
            }
            // Observe whenever the car responds back with its status
            resp.observeForever(new Observer<JSONObject>() {
                @Override
                public void onChanged(JSONObject jsonObject) {
                    // Notify the observers list that there has been an update
                    for (ConnectionManagerCallback observer : observers) {
                        observer.receivedFromCar(jsonObject);
                    }
                    resp.removeObserver(this); // Prevent memory leaks
                }

            });
        }
    }
}
