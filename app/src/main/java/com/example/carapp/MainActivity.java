package com.example.carapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        // Set local persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Navigate to login activity
        startActivity(new Intent(this, LoginActivity.class));
    }
}
