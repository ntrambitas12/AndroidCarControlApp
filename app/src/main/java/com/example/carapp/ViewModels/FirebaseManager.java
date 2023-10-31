package com.example.carapp.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseManager extends ViewModel {
    private FirebaseRepository repository;
    private LiveData<HashMap<String, Object>> userData;
    public FirebaseManager() {
        repository = new FirebaseRepository(); // Initialize class that performs CRUD operations to firebase
    }

    public void createNewProfile(String fullName, String uid) {
        repository.createNewUser(fullName, uid);
    }
    public void addNewCar(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        repository.addNewCar(uid, BTMacAddress, Nickname, VIN, Color);
    }
    public void deleteCar(String uid, String carVIN) {
        repository.deleteCar(uid, carVIN);
    }
    // Updates the currently selected car's data in firebase
    public void updateCurrentCarData(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        repository.updateCurrentCarData(uid, BTMacAddress, Nickname, VIN, Color);
    }

    public void deleteUser(String uid) {
        repository.deleteUser(uid);
    }

    public void updateCurrentlySelectedCar(String uid, int carID) {
        repository.updateCurrentlySelectedCar(uid, carID);
    }

    public void updateUserData(String uid, String name) {
        repository.updateUserData(uid, name);
    }

    // Call once upon initially loading a user's profile.
    public void loadProfile(String uid) {

        userData = repository.getUserData(uid);
    }


    public LiveData<HashMap<String, Object>> getUserData() {
        return userData; /* Returns a reference to liveData.
        When Fragment observes this liveData reference, t
        he completed callback within the repository fires and automatically notifies UI*/
    }

}
