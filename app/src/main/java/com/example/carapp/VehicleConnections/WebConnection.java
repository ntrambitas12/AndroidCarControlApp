package com.example.carapp.VehicleConnections;

import com.example.carapp.ViewModels.BluetoothSearchViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebConnection implements IConnection{
    private BluetoothSearchViewModel bluetoothSearchViewModel;
    private String URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final String VIN;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);



    public WebConnection(String VIN, String Address, BluetoothSearchViewModel bluetoothSearchViewModel) {
        this.bluetoothSearchViewModel = bluetoothSearchViewModel;
        this.client  = new OkHttpClient();
        this.VIN = VIN;
        this.URL = Address;
    }


    @Override
    public void endConnection() {
        // Release any resources here
    }

    @Override
    public void sendToCar(Command Payload) {
        executorService.submit(() -> {
            try {
                // Create the request body
                RequestBody requestBody = RequestBody.create(String.valueOf(Payload), JSON);

                // Create PUT request
                Request request = new Request.Builder()
                        .url(this.URL)
                        .put(requestBody)
                        .addHeader("set-vin", this.VIN)
                        .build();
                client.newCall(request).execute();
            } catch (IOException e) {
                // Unable to send to webserver
            }

        });
    }

@Override
public void receiveFromCar() {
    // Create a new thread to fetch the car state
    executorService.submit(() -> {
        try {
            // Create GET request
            Request request = new Request.Builder()
                    .url(this.URL)
                    .addHeader("set-vin", this.VIN)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    // Update the viewModel with updated carState
                    bluetoothSearchViewModel.updateCarState(new JSONObject(response.body().string()));
                }
            }
        } catch (IOException | JSONException e) {
            // Request error
        }
    });
}


}
