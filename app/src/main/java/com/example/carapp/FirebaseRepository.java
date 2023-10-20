package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseRepository {
    private FirebaseDatabase databaseSettings = FirebaseDatabase.getInstance();
    private DatabaseReference database = databaseSettings.getReference();
    private HashMap<String, Object> profile;
    public FirebaseRepository() {
//        databaseSettings.setPersistenceEnabled(true);
//        databaseSettings.setPersistenceCacheSizeBytes(100*1024*1024); // Set the cache for 100mbs
    }

    public void createNewUser(String fullName, String uid) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", fullName);
        userData.put("defaultCar", 0); // Initially, set the default car to 0

        // Initialize an arrayList that will hold an array of a user's cars
        ArrayList<HashMap<String, Object>> usersCars = new ArrayList<>();
        userData.put("cars", usersCars);

        // Add any additional data to store within a user's profile here

        // Write to firebase
        database.child("users").child(uid).setValue(userData);

    }

    public void addNewCar(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        // Fetch the user's profile
        HashMap<String, Object> userData = getUserData(uid);
        ArrayList<HashMap<String, Object>> usersCars = (ArrayList<HashMap<String, Object>>) userData.get("cars");

        // Create a new Car Object
        HashMap<String, Object> car = new HashMap<>();
        car.put("BTMacAddress", BTMacAddress);
        car.put("nickName", Nickname);
        car.put("VIN", VIN);
        car.put("Color", Color);

        // Add the car to the arrayList
        usersCars.add(car);

        // Set the newly added car as the default car
        int addedCarIdx = usersCars.size() - 1;
        userData.replace("defaultCar", addedCarIdx);

        // Write the updates back to Firebase
        database.child("users").child(uid).setValue(userData);
    }

    // Retrieves the entire profile from Firebase. This will automatically get called upon updates
    public HashMap<String, Object> getUserData(String uid) {
//        HashMap<String, Object> profile;
        database.child("users").child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profile = snapshot.getValue(HashMap.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error has occurred, set an empty hashmap
                profile = new HashMap<>();
            }
        });
        return profile;
    }

    /* Deletes a car from a user's profile.
    carID is the index in the arrayList of the car to be deleted */
    public void deleteCar(String uid, int carID) {
        //TODO: Fill this method in

    }
    // Updates the currently selected car's data in firebase
    public void updateCurrentCarData(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        //TODO: Fill this method in
    }

    // Updates the car that the user has currently selected in their profile
    public void updateCurrentlySelectedCar(String uid, int carID) {
        // Fetch the user's profile
        HashMap<String, Object> userData = getUserData(uid);
        userData.replace("defaultCar", carID);
        // Write update
        database.child("users").child(uid).setValue(userData);
    }

    // Deletes a user's entry in Firebase
    public void deleteUser(String uid) {
        database.child("users").child(uid).removeValue();
    }
    //Updates a user's profile in Firebase
    public void updateUserData(String uid, String name) {
        // Fetch the user's profile
        HashMap<String, Object> userData = getUserData(uid);
        userData.replace("name", name);

        // Write update
        database.child("users").child(uid).setValue(userData);
    }

}
