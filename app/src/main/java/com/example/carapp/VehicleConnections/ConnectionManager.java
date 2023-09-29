package com.example.carapp.VehicleConnections;

import android.os.Handler;

import org.json.JSONObject;

public class ConnectionManager{
    private static ConnectionManager instance = null;

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  IConnection WebLink;
    private ConnectionManager(IBluetooth BluetoothLink, IConnection WebLink) {
        // ConnectionManager Constructor
        this.BluetoothLink = BluetoothLink;
        this.WebLink = WebLink;

        // Create method to call receiveFromCar every 2 seconds
        //TODO: refactor code to pass in parameter for refresh time
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ConnectionManager.getInstance().receiveFromCar();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    // Static method to create an instance of ConnectionManager
    public static synchronized ConnectionManager createInstance(IBluetooth BluetoothLink, IConnection WebLink) {
        if (instance == null) {
            instance = new ConnectionManager(BluetoothLink,  WebLink);
        }
        return instance;
    }

    public static synchronized ConnectionManager getInstance() {
        return instance;
    }


    public void endConnection() {
        if (BluetoothLink.isConnected()) {
            BluetoothLink.endConnection();
        } else {
            WebLink.endConnection();
        }
    }


    public void sendToCar(Command Payload) {
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
        if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
             BluetoothLink.receiveFromCar();
        } else {
           WebLink.receiveFromCar();
        }
    }
}
