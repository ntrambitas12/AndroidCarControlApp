package com.example.carapp.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class carStateViewModel extends ViewModel {
    public final MutableLiveData<JSONObject> carState = new MutableLiveData<>(new JSONObject());
    public final MutableLiveData<String> VINLiveData = new MutableLiveData<>("");
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> VINSearchCallbackFuture;
    private boolean isConfirmingVIN = false;

    public void updateCarState(JSONObject newState) {
        carState.postValue(newState);
        if (isConfirmingVIN) {
            // check if the VIN is set in the JSON object.
            // If it is, post the VIN to the UI and cancel callback
            try {
                String VIN = newState.getString("VIN");
                if (VIN.length() > 0) {
                    cancelVINSearchCallback();
                    VINLiveData.postValue(VIN);
                }
            }  catch (JSONException e) {
               // JSON doesn't contain VIN
            }
        }
    }

    /* This function starts a callback to be executed after 60 seconds of trying to communicate with
    * the vehicle. The callback is designed so that it can be prematurely canceled if the VIN is read
    * before the time limit is reached  */
    public void startVINSearch() {
        // Ensure that there isn't already a search started
        if (VINSearchCallbackFuture == null || VINSearchCallbackFuture.isDone()) {
            isConfirmingVIN = true;
            VINSearchCallbackFuture = executorService.schedule(() -> {
                // Post "" to indicate VIN not read
                if (VINLiveData.getValue() == "") {
                    VINLiveData.postValue("ERROR");
                }
                isConfirmingVIN = false;
            }, 30, TimeUnit.SECONDS);
        }
    }

    private void cancelVINSearchCallback() {
        if (VINSearchCallbackFuture != null && !VINSearchCallbackFuture.isDone()) {
            VINSearchCallbackFuture.cancel(false); // Set to false to allow code inside thread to execute if cancelled
        }
    }
}
