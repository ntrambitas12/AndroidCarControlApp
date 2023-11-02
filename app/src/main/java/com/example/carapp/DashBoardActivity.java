package com.example.carapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Navigation.findNavController(this, R.id.Nav_Dashboard);
    }


}


