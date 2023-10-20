package com.example.carapp.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseManager extends ViewModel {


    private FirebaseRepository repository;
    public FirebaseManager() {
        repository = new FirebaseRepository(); // Initialize class that performs CRUD operations to firebase
    }

    public void createNewProfile(String fullName, String uid) {
        repository.createNewUser(fullName, uid);
    }
    public void addNewCar(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        repository.addNewCar(uid, BTMacAddress, Nickname, VIN, Color);
    }
    public void deleteCar(String uid, int carID) {
        repository.deleteCar(uid, carID);

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

    /* UI will observe this method. Whenever anything changes in Firebase,
    this method will automatically be fired by the firebaseRepository class and it will update the UI*/
    public LiveData<HashMap<String, Object>> getUserProfile(String uid) {
        return repository.getUserData(uid);
    }

}
