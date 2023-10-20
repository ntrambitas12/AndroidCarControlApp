package com.example.carapp.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseManager extends ViewModel {
    private FirebaseRepository repository;
    private MutableLiveData<HashMap<String, Object>> userData;
    public FirebaseManager() {
        repository = new FirebaseRepository(); // Initialize class that performs CRUD operations to firebase
        userData = new MutableLiveData<>();
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
    public void getUserProfile(String uid) {
        userData.postValue(repository.getUserData(uid));
    }

    public boolean userHasCars() {
        HashMap<String, Object> data = userData.getValue();
        // Checking for null because uid passed in may not yet exist in our db
        if (data == null) {
            return false;
        }
        ArrayList<HashMap<String, Object>> cars = (ArrayList<HashMap<String, Object>>) data.get("cars");
        return cars.size() > 0;
    }

    public LiveData<HashMap<String, Object>> getUserData() {
        return userData;
    }

}
