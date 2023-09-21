package com.example.carapp.VehicleConnections;

public class ConnectionManager {
    private static ConnectionManager instance = null;

    // Declare class variables here:

    private ConnectionManager() {
        // ConnectionManager Constructor
    }

    // Static method to create an instance of ConnectionManager
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return  instance;
    }
}
