package com.example.carapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {


    private static final String ARG_PARAM1 = "CounterRef";
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    // Variable to store Counter ref
    private Counter mParam1;

    public LoginFragment() {
        // Provide the XML layout of the fragment to base constructor here
        super(R.layout.login_vertical);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        // if any arguments are passed, pull them out here
        System.out.println("Created fragment");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the button and textview
        Button signUp = view.findViewById(R.id.SignUp);
        Button login = view.findViewById(R.id.login);

        signUp.setOnClickListener(this.createListener());
        login.setOnClickListener(this.createListener());
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContent, fragment);
        transaction.commit();
    }

    // Creates a listener that will increment the counter and update countDisplay onClick
    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.login) {
                    //call script to check if login details are a user within the database
                    //and go to main app view
                    // Get email and password text & call signIn method
                }
                else if (view.getId() == R.id.SignUp) {
                    //switch fragment to signup view
                    Fragment signUp = new SignUpFragment();
                    changeFragment(signUp);
                }
            }
        };
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}