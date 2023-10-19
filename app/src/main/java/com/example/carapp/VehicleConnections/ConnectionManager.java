package com.example.carapp.VehicleConnections;

import android.os.Handler;

import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class ConnectionManager extends ViewModel {

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  IConnection WebLink;


    public void initialize(IBluetooth BluetoothLink, IConnection WebLink, int refreshInterval) {
        // Set the references to the Bluetooth and Web controllers
        this.BluetoothLink = BluetoothLink;
        this.WebLink = WebLink;

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

    public void endConnection() {
        if (BluetoothLink != null && WebLink != null) {
            if (BluetoothLink.isConnected()) {
                BluetoothLink.endConnection();
            } else {
                WebLink.endConnection();
            }
        }
    }


    public void sendToCar(Command Payload) {
        if (BluetoothLink != null && WebLink != null)
        // If connected via Bluetooth, send via Bluetooth
        if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
            BluetoothLink.sendToCar(Payload);
        } else {
            WebLink.sendToCar(Payload);
        }
    }

    public IBluetooth getBluetoothLink() {
        return BluetoothLink;
    }

    private void receiveFromCar() {
        // If connected via Bluetooth, receive via Bluetooth
        if (BluetoothLink != null && WebLink != null) {
            if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
                BluetoothLink.receiveFromCar();
            } else {
                WebLink.receiveFromCar();
            }
        }
    }
}
