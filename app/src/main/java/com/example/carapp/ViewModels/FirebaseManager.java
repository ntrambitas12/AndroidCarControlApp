package com.example.carapp.ViewModels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class FirebaseManager extends ViewModel {

    private FirebaseUser user;
    // private variable to hold list of car info for user

    public void initialize(FirebaseUser user) {
        this.user = user;
    }

    // Method to get list of cars for user
    // Not exactly sure what the return type should be?
    // Will we need to create a model for the car info and then we can have a list of that object?
}
