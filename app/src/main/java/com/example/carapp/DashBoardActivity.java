package com.example.carapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.carapp.VehicleConnections.ConnectionManager;


public class DashBoardActivity extends AppCompatActivity {
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        connectionManager = new ViewModelProvider(this).get(ConnectionManager.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Navigation.findNavController(this, R.id.Nav_Dashboard);
    }

    @Override
    protected void onStop() {
        super.onStop();
        connectionManager.endConnection();
    }
}


