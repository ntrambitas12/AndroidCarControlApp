package com.example.carapp.VehicleConnections;

import org.json.JSONObject;

public class ConnectionManager implements IConnection{
    private static ConnectionManager instance = null;

    // Declare class variables here:
    private IBluetooth BluetoothLink;
    private  IConnection WebLink;
    private ConnectionManager(IBluetooth BluetoothLink, IConnection WebLink) {
        // ConnectionManager Constructor
        this.BluetoothLink = BluetoothLink;
        this.WebLink = WebLink;
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
    public void receiveFromCar() {
        // If connected via Bluetooth, receive via Bluetooth
        if (BluetoothLink.isBTEnabled() && BluetoothLink.isConnected()) {
             BluetoothLink.receiveFromCar();
        } else {
           WebLink.receiveFromCar();
        }
    }
}
