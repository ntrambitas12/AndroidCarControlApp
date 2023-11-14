package com.example.carapp.VehicleConnections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebConnection implements IConnection{
    private final String URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private String VIN;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final MutableLiveData<JSONObject> carResp = new MutableLiveData<>();

    public WebConnection(String Address) {
        this.client  = new OkHttpClient();
        this.URL = Address;
    }

    public void setVIN(String VIN) { this.VIN = VIN; }

    @Override
    public void endConnection() {
        // Release any resources here
    }

    @Override
    public void sendToCar(Command Payload) {
        if (VIN != null) {
            executorService.submit(() -> {
                try {
                    // Create the request body
                    RequestBody requestBody = RequestBody.create(String.valueOf(Payload), JSON);
                    // Create PUT request
                    Request request = new Request.Builder()
                            .url(this.URL + "/putCommand")
                            .put(requestBody)
                            .addHeader("set-vin", this.VIN)
                            .build();
                    client.newCall(request).execute();
                } catch (IOException e) {
                    // Unable to send to webserver
                }

            });
        }
    }

    public LiveData<JSONObject> receiveFromCar() {
    if (VIN != null) {
        // Create GET request
        Request request = new Request.Builder()
                .url(this.URL + "/getStatus")
                .addHeader("set-vin", this.VIN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network errors
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        // Update liveData updated carState
                        carResp.setValue(new JSONObject(response.body().string()));
                    } else {
                        // Handle non-successful responses (e.g., 404, 500)
                    }
                } catch (IOException | JSONException e) {
                    // Handle parsing errors
                } finally {
                    response.close(); // Close the response body
                }
            }
        });
    }
        return carResp;
    }



}
