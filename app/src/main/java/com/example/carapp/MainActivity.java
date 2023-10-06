package com.example.carapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Bundle;
import android.util.Log;

import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Log.d(TAG, "onCreate executed!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart executed!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume executed!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause executed!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop executed!");
    }
}