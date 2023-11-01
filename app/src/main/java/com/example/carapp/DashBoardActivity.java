package com.example.carapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import com.example.carapp.Fragments.NoCarDashboardDirections;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class DashBoardActivity extends AppCompatActivity {

    private NavController navController;
    private FirebaseManager firebaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseManager = new ViewModelProvider(this).get(FirebaseManager.class);

        setContentView(R.layout.dashboard_activity);

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.navController = Navigation.findNavController(this, R.id.Nav_Dashboard);

        FirebaseManager firebaseManager = new ViewModelProvider(this).get(FirebaseManager.class);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseManager.loadProfile(user.getUid());

        //set observer
        firebaseManager.getUserData().observe(this, userProfile -> {
            checkData(userProfile);
        });
        checkData(firebaseManager.getUserData().getValue());
    }

    private void checkData(HashMap<String, Object>  userData)
    {
        // Check that user has at least one car in their profile
        if ((userData != null && userData.containsKey("cars"))) {
            navController.navigate(R.id.dashboardFragment2);
        }
        else {
            navController.navigate(R.id.noCarDashboard);
        }
    }
}


