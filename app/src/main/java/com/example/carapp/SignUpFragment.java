package com.example.carapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class SignUpFragment extends Fragment {

    private NavController navController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button creatAcc = view.findViewById(R.id.createAccount);

        signIn.setOnClickListener(this.createListener());
        creatAcc.setOnClickListener(this.createListener());
    }



    // Creates a listener that will increment the counter and update countDisplay onClick
    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.createAccount) {
                    //call script to create new user account/check params
                    //also be sure to check if there isnt a user that has the same email

                }
                else if (view.getId() == R.id.SignIn) {
                    //switch fragment to signup view
                    navController.navigate(R.id.loginFragment);
                }
            }
        };
    }
}