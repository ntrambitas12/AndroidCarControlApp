package com.example.carapp.VehicleConnections;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebConnection implements IConnection{
    private final Activity activity;
    private JSONObject carState;
    private String URL;
    private boolean isConnected = false;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final String VIN;
    private final Object lock = new Object(); // Create a synchronization lock
    private volatile boolean isFetching = false; // Flag to ensure only 1 background thread can be created


    public WebConnection(Activity activity, String VIN) {
        this.activity = activity;
        this.carState = new JSONObject();
        this.client  = new OkHttpClient();
        this.VIN = VIN;
    }
    public void startConnection(String Address) {
        this.URL = Address;
        this.isConnected = true;
    }

    @Override
    public void endConnection() {
        this.URL = "";
        this.isConnected = false;
    }

    @Override
    public void sendToCar(Command Payload) {
        new Thread(() -> {
            // Create the request body
            RequestBody requestBody = RequestBody.create(String.valueOf(Payload), JSON);

            // Create PUT request
            Request request = new Request.Builder()
                    .url(this.URL)
                    .put(requestBody)
                    .addHeader("set-vin", this.VIN)
                    .build();
            try {
                 client.newCall(request).execute();

            } catch (IOException e) {
                throw new RuntimeException(e);
                // Unable to connect to webServer
            }
        }).start();
    }

@Override
public JSONObject receiveFromCar() {
    synchronized (lock) {
        // Check if a background thread is already fetching
        if (isFetching) {
            // A background thread is already fetching the car state, return the current state or an empty JSON
            if (carState == null) {
                return new JSONObject();
            } else {
                return carState;
            }
        }

        // Mark that a fetch operation is in progress
        isFetching = true;
    }

    // Create a new thread to fetch the car state
    new Thread(() -> {
        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(this.URL)
                    .addHeader("set-vin", this.VIN)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    this.carState = new JSONObject(response.body().string());
                }
            } catch (IOException | JSONException e) {
                // Unable to connect to webServer
                this.carState = new JSONObject(); // Set carState as an empty JSON
            }
        } finally {
            synchronized (lock) {
                // Mark the fetch operation as completed
                isFetching = false;
            }
        }
    }).start();

    // Return the current state
    if (carState == null) {
        return new JSONObject();
    } else {
        return carState;
    }
}


}
