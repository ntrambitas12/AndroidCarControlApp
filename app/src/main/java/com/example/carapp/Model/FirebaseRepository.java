package com.example.carapp.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {
    private final FirebaseDatabase databaseSettings = FirebaseDatabase.getInstance();
    private final DatabaseReference database = databaseSettings.getReference();


    public FirebaseRepository() {

//        databaseSettings.setPersistenceEnabled(true);
//        databaseSettings.setPersistenceCacheSizeBytes(100*1024*1024); // Set the cache for 100mbs
    }

    public void createNewUser(String fullName, String uid) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", fullName);
        userData.put("defaultCar", 0); // Initially, set the default car to 0


        // Add any additional data to store within a user's profile here

        // Write to firebase
        database.child("users").child(uid).setValue(userData);

    }

    public void addNewCar(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        // Fetch the user's profile
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        FirebaseRepository repo = this;
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profile) {
                if (profile != null) {
                    ArrayList<HashMap<String, Object>> usersCars;
                    if (profile.containsKey("cars")) {
                        usersCars = (ArrayList<HashMap<String, Object>>) profile.get("cars");
                    }  else {
                        usersCars = new ArrayList<>();
                    }

                    // Create a new Car Object
                    HashMap<String, Object> car = new HashMap<>();
                    car.put("BTMacAddress", BTMacAddress);
                    car.put("nickName", Nickname);
                    car.put("VIN", VIN);
                    car.put("Color", Color);
                    car.put("Image", "");

                    //finds car picture async in the background
                    CarPicture pic = new CarPicture(repo, uid, VIN);
                    pic.FindCarPicture();

                    // Add the car to the arrayList
                    assert usersCars != null;
                    usersCars.add(car);

                    // Set the newly added car as the default car
                    int addedCarIdx = usersCars.size() - 1;
                    profile.replace("defaultCar", addedCarIdx);

                    //Add the arrayList reference to the hashMap if it doesn't exist
                    if (!profile.containsKey("cars")) {
                        profile.put("cars", usersCars);
                    }

                    // Write the updates back to Firebase
                    database.child("users").child(uid).setValue(profile);
                    // Remove observer to prevent extra callbacks and memory leaks
                    userData.removeObserver(this);
                }
            }
        });

    }

    // Retrieves the entire profile from Firebase. This will automatically get called upon updates
    public LiveData<HashMap<String, Object>> getUserData(String uid) {
        final MutableLiveData<HashMap<String, Object>> profileLiveData = new MutableLiveData<>();

        database.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GenericTypeIndicator<HashMap<String, Object>> T = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> profile = snapshot.getValue(T);
                    profileLiveData.setValue(profile); // Update the LiveData
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if necessary
            }
        });

        return profileLiveData;
    }

    /* Deletes a car from a user's profile. */
    public void deleteCar(String uid, String carVIN) {
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profile) {
                if (profile != null) {
                    int position = 0;
                    ArrayList<HashMap<String, Object>> newCars = new ArrayList<>();
                    for (HashMap<String, Object> car : (List<HashMap<String, Object>>)profile.get("cars")) {
                        if (!carVIN.equals(car.get("VIN").toString())) {
                            newCars.add(car);
                        } else if(profile.get("defaultCar").toString().equals(String.valueOf(position))) {
                            profile.replace("defaultCar", 0);
                        }
                        position++;
                    }
                    profile.replace("cars", newCars);
                    database.child("users").child(uid).setValue(profile);
                    userData.removeObserver(this);
                }
            }
        });
    }
    // Updates the selected Car's image in the database
    public void updateCarImage(String uid, String ImageLink, String VIN) {
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profile) {
                if (profile != null) {
                    ArrayList<HashMap<String, Object>> newCars = new ArrayList<>();
                    for (HashMap<String, Object> car : (List<HashMap<String, Object>>)profile.get("cars")) {
                        if (!VIN.equals(car.get("VIN").toString())) {
                            newCars.add(car);
                        } else {
                            HashMap<String, Object> newCar = new HashMap<>();
                            newCar.put("BTMacAddress", car.get("BTMacAddress"));
                            newCar.put("Color", car.get("Color"));
                            newCar.put("VIN", VIN);
                            newCar.put("nickName", car.get("nickName"));
                            newCar.put("Image", ImageLink);
                            newCars.add(newCar);
                        }
                    }
                    profile.replace("cars", newCars);
                    database.child("users").child(uid).setValue(profile);
                    userData.removeObserver(this);
                }
            }
        });
    }

    public void updateCarImageWithoutObserver(String uid, String ImageLink, String VIN) {
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        HashMap<String, Object> profile = userData.getValue();
        Log.i("FirebaseRepo",profile.toString());
        if (profile != null) {
            ArrayList<HashMap<String, Object>> newCars = new ArrayList<>();
            for (HashMap<String, Object> car : (List<HashMap<String, Object>>)profile.get("cars")) {
                if (!VIN.equals(car.get("VIN").toString())) {
                    newCars.add(car);
                } else {
                    HashMap<String, Object> newCar = new HashMap<>();
                    newCar.put("BTMacAddress", car.get("BTMacAddress"));
                    newCar.put("Color", car.get("Color"));
                    newCar.put("VIN", VIN);
                    newCar.put("nickName", car.get("nickName"));
                    newCar.put("Image", ImageLink);
                    newCars.add(newCar);
                }
            }
            profile.replace("cars", newCars);
            database.child("users").child(uid).setValue(profile);
        }
    }

    // Updates the currently selected car's data in firebase
    public void updateCurrentCarData(String uid, String BTMacAddress, String Nickname, String VIN, String Color) {
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profile) {
                if (profile != null) {
                    ArrayList<HashMap<String, Object>> newCars = new ArrayList<>();
                    for (HashMap<String, Object> car : (List<HashMap<String, Object>>)profile.get("cars")) {
                        if (!VIN.equals(car.get("VIN").toString())) {
                            newCars.add(car);
                        } else {
                            HashMap<String, Object> newCar = new HashMap<>();
                            newCar.put("BTMacAddress", BTMacAddress);
                            newCar.put("Color", Color);
                            newCar.put("VIN", VIN);
                            newCar.put("nickName", Nickname);
                            newCar.put("Image", car.get("Image"));
                            newCars.add(newCar);
                        }
                    }
                    profile.replace("cars", newCars);
                    database.child("users").child(uid).setValue(profile);
                    userData.removeObserver(this);
                }
            }
        });
    }

    // Updates the car that the user has currently selected in their profile
    public void updateCurrentlySelectedCar(String uid, int carID) {
        // Fetch the user's profile
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profile) {
                if (profile != null) {
                    profile.replace("defaultCar", carID);
                    // Write update
                    database.child("users").child(uid).setValue(profile);
                    // Remove observer to prevent repeat calls and memory leaks
                    userData.removeObserver(this);
                }
            }
        });


    }

    // Deletes a user's entry in Firebase
    public void deleteUser(String uid) {
        database.child("users").child(uid).removeValue();
    }
    //Updates a user's profile in Firebase
    public void updateUserData(String uid, String name) {
        // Fetch the user's profile
        LiveData<HashMap<String, Object>> userData = getUserData(uid);
        userData.observeForever(new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> profileData) {
                // Update the profile
                if (profileData != null) {
                    profileData.replace("name", name);

                    // Write update
                    database.child("users").child(uid).setValue(profileData);
                }
                // Remove observer to avoid multiple calls and memory leaks
                userData.removeObserver(this);
            }
        });

    }

}
