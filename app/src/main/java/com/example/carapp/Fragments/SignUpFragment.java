package com.example.carapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.carapp.DashBoardActivity;
import com.example.carapp.LoginActivity;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.carapp.R;



public class SignUpFragment extends Fragment {

    private static final String TAG = "EmailPassword";
    private NavController navController;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
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
        View rootView = inflater.inflate(R.layout.signup_vertical, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Save the navController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Get the button and textview
        Button signIn = view.findViewById(R.id.SignIn);
        Button createAcc = view.findViewById(R.id.createAccount);

        signIn.setOnClickListener(this.createListener());
        createAcc.setOnClickListener(this.createListener());

        // Get username & password text views
        emailEditText = view.findViewById(R.id.emailSignup);
        passwordEditText = view.findViewById(R.id.passwordSignup);
        nameEditText = view.findViewById(R.id.name);
    }

    // Creates a listener that will increment the counter and update countDisplay onClick
    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.createAccount) {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String name = nameEditText.getText().toString();
                    createAccount(email, password, name);
                }
                else if (view.getId() == R.id.SignIn) {
                    //switch fragment to signup view
                    NavDirections actionGoToLogin = SignUpFragmentDirections.actionSignUpFragment2ToLoginFragment2();
                    navController.navigate(actionGoToLogin);
                }
            }
        };
    }

    private void createAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseManager.createNewProfile(name, user.getUid());
                            //NavDirections actionGoToPairing = SignUpFragmentDirections.actionSignUpFragmentToCarSearch();
                            //navController.navigate(actionGoToPairing);
                            startActivity(new Intent(getActivity(), DashBoardActivity.class));
                        } else {
                            // If create account fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Account creation failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}