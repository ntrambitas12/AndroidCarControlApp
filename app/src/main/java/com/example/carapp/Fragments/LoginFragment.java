package com.example.carapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.carapp.DashBoardActivity;
import com.example.carapp.LoginActivity;
import com.example.carapp.R;
import com.example.carapp.VehicleConnections.ConnectionManager;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;


public class LoginFragment extends Fragment {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private NavController navController;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseManager firebaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseManager = new ViewModelProvider(requireActivity()).get(FirebaseManager.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_vertical, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Save the navController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Get the button and textview
        Button signUp = view.findViewById(R.id.SignUp);
        Button login = view.findViewById(R.id.login);

        signUp.setOnClickListener(this.createListener());
        login.setOnClickListener(this.createListener());

        // Get username & password text views
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
    }


    // Creates a listener that will increment the counter and update countDisplay onClick
    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.login) {
                    // Get email and password text and call signIn method
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    signIn(email, password);
                }
                else if (view.getId() == R.id.SignUp) {
                    //switch fragment to signup view
                    NavDirections actionGoToSignUp = LoginFragmentDirections.actionLoginFragmentToSignUpFragment();
                    navController.navigate(actionGoToSignUp);
                }
            }
        };
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(getActivity(), DashBoardActivity.class));

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}