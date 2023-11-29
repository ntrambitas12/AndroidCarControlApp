package com.example.carapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.carapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if a user is already signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is already signed in, navigate them to the dashboard
            startActivity(new Intent(this, DashBoardActivity.class));
        } else {
            // No user signed in, send them to sign in screen
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        }
        // Get a reference to the navController and navigate user to the loginFragment

    }
}
