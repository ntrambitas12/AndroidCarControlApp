package com.example.carapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Bundle;




public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


    }


}