package com.example.carapp.VehicleConnections;

import org.json.JSONObject;

public class ConnectionManager implements IConnection{
    private static ConnectionManager instance = null;

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  IConnection WebLink;
    private ConnectionManager(IBluetooth BluetoothLink, IConnection WebLink) {
        // ConnectionManager Constructor
    }

    // Static method to create an instance of ConnectionManager
    public static synchronized ConnectionManager getInstance(IBluetooth BluetoothLink, IConnection WebLink) {
        if (instance == null) {
            instance = new ConnectionManager(BluetoothLink,  WebLink);
        }
        return  instance;
    }

    @Override
    public void endConnection() {
        // TBD on how to properly release resources
    }

    @Override
    public void sendToCar(Command Payload) {
        // If connected via Bluetooth, send via Bluetooth
        if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
            BluetoothLink.sendToCar(Payload);
        } else {
            WebLink.sendToCar(Payload);
        }
    }


    @Override
    public JSONObject receiveFromCar() {
        JSONObject carState;
        // If connected via Bluetooth, receive via Bluetooth
        if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
            carState = BluetoothLink.receiveFromCar();
        } else {
           carState = WebLink.receiveFromCar();
        }
        return carState;
    }
}
