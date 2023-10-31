package com.example.carapp.Fragments;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
        carNickname = view.findViewById(R.id.editCarNickname);
        carMacAddress = view.findViewById(R.id.editCarMacAddress);
        carVIN = view.findViewById(R.id.editCarVIN);
        carColor = view.findViewById(R.id.editCarColor);

        String vin = CarInfoFragmentArgs.fromBundle(getArguments()).getCarVIN();
        HashMap<String, Object> car = getSelectedCar(vin);
        carNickname.setText(car.get("nickName").toString());
        carMacAddress.setText(car.get("BTMacAddress").toString());
        carVIN.setText(car.get("VIN").toString());
        carColor.setText(car.get("Color").toString());

        save.setOnClickListener(this.createListener());
        cancel.setOnClickListener(this.createListener());
    }

    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.saveCarInfo) {
                    firebaseManager.updateCurrentCarData(mAuth.getUid(), carMacAddress.getText().toString(), carNickname.getText().toString(), carVIN.getText().toString(), carColor.getText().toString());
                    NavDirections actionGoToDashboard = CarInfoFragmentDirections.actionCarInfoFragmentToDashboardFragment2();
                    navController.navigate(actionGoToDashboard);
                } else if (view.getId() == R.id.cancelCarInfo) {
                    NavDirections actionGoToDashboard = CarInfoFragmentDirections.actionCarInfoFragmentToDashboardFragment2();
                    navController.navigate(actionGoToDashboard);
                }
            }
        };
    }

    private HashMap<String, Object> getSelectedCar(String carVIN) {
        for (HashMap<String, Object> car : (List<HashMap<String, Object>>)firebaseManager.getUserData().getValue().get("cars")) {
            if (carVIN.equals(car.get("VIN").toString())) {
                return car;
            }
        }
        return null;
    }
}
