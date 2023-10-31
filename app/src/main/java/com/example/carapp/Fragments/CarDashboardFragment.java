package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;

public class CarDashboardFragment extends Fragment {

    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseManager firebaseManager;
    private String carName;
    private String carMakeModel;
    private String carVIN;

    public CarDashboardFragment(String carName, String carMakeModel, String carVIN)
    {
        this.carName = carName;
        this.carMakeModel = carMakeModel;
        this.carVIN = carVIN;
    }

    public CarDashboardFragment()
    {
        this.carName = "Car Name";
        this.carMakeModel = "Car Make And Model";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseManager = new ViewModelProvider(requireActivity()).get(FirebaseManager.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_with_car, container, false);
        TextView carNameText = rootView.findViewById(R.id.CarNameText);
        TextView carMakeText = rootView.findViewById(R.id.CarMakeModel);
        carNameText.setText(this.carName);
        carMakeText.setText(this.carMakeModel);

        Button removeCar = rootView.findViewById(R.id.RemoveCar);
        removeCar.setOnClickListener(this.createListener());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get navigation controller
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);
    }

    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.RemoveCar) {
                    firebaseManager.deleteCar(mAuth.getUid(), carVIN);
                }
            }
        };
    }
}
