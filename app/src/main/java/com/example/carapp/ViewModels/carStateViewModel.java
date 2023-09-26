package com.example.carapp.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class carStateViewModel extends ViewModel {
    public final MutableLiveData<JSONObject> carState = new MutableLiveData<>(new JSONObject());

    public void updateCarState(JSONObject newState) {
        carState.postValue(newState);
    }
}
