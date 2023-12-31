package com.example.carapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.carapp.DashBoardActivity;
import com.example.carapp.LoginActivity;
import com.example.carapp.Model.Car;
import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;

public class CarInfoFragment extends Fragment {

    private NavController navController;
    private FirebaseManager firebaseManager;
    private EditText carNickname;
    private EditText carMacAddress;
    private EditText carVIN;
    private EditText carColor;
    private FirebaseAuth mAuth;
    private Car selectedCar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new ViewModelProvider(requireActivity()).get(FirebaseManager.class);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_car_info, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);

        Button save = view.findViewById(R.id.saveCarInfo);
        Button cancel = view.findViewById(R.id.cancelCarInfo);
        Button delete = view.findViewById(R.id.deleteCar);
        Button signOut = view.findViewById(R.id.signOutButton);

        carNickname = view.findViewById(R.id.editCarNickname);
        carMacAddress = view.findViewById(R.id.editCarMacAddress);
        carVIN = view.findViewById(R.id.editCarVIN);
        carColor = view.findViewById(R.id.editCarColor);

        selectedCar = CarInfoFragmentArgs.fromBundle(getArguments()).getCar();
        if (selectedCar != null) {
            carNickname.setText(selectedCar.getNickName());
            carMacAddress.setText(selectedCar.getBTMacAddress());
            carVIN.setText(selectedCar.getVIN());
            carColor.setText(selectedCar.getColorHEX());
        }


        save.setOnClickListener(this.createListener());
        cancel.setOnClickListener(this.createListener());
        delete.setOnClickListener(this.createListener());
        signOut.setOnClickListener(this.createListener());
    }

    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections actionGoToDashboard = CarInfoFragmentDirections.actionCarInfoFragmentToDashboardFragment2();
                if (view.getId() == R.id.saveCarInfo) {
                    firebaseManager.updateCurrentCarData(mAuth.getUid(), carMacAddress.getText().toString(), carNickname.getText().toString(), carVIN.getText().toString(), carColor.getText().toString());
                    navController.navigate(actionGoToDashboard);
                } else if (view.getId() == R.id.cancelCarInfo) {
                    navController.navigate(actionGoToDashboard);
                } else if (view.getId() == R.id.deleteCar) {
                    firebaseManager.deleteCar(mAuth.getUid(), selectedCar.getVIN());
                    navController.navigate(actionGoToDashboard);
                } else if (view.getId() == R.id.signOutButton) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        };
    }
}
