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
    public void sendToCar(String Payload) {
        new Thread(() -> {
            // Create the request body
            RequestBody requestBody = RequestBody.create(Payload, JSON);

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
    public void receiveFromCar() {
        new Thread(() -> {

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
                }
             catch (IOException | JSONException e) {
                throw new RuntimeException(e);
                // Unable to connect to webServer
            }
        }).start();
    }

    @Override
    public JSONObject getCarState() {
        return this.carState;
    }

}
