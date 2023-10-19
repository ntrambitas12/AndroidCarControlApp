package com.example.carapp.VehicleConnections;

import org.json.JSONObject;

public interface IConnection {
    /*
        Starts a connection to the vehicle.
            Input Address represents the Address needed for the medium to start connection
            Ex: For web, Address = API URL Address
            For Bluetooth, Address = Adapter MAC Address
    */

    /*
        Ends the connection
     */
    public void endConnection();
    /*
        Sends a command formatted as a String to the car.
     */
    public void sendToCar(Command Payload);

     public void receiveFromCar();


    /* Returns the car's current state as an object
     */
}
