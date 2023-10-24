package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.carapp.R;

public class CarDashboardFragment extends Fragment {

    private NavController navController;
    String carName;
    String carMakeModel;

    public CarDashboardFragment(String carName, String carMakeModel)
    {
        this.carName = carName;
        this.carMakeModel = carMakeModel;
    }

    public CarDashboardFragment()
    {
        this.carName = "Car Name";
        this.carMakeModel = "Car Make And Model";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_with_car, container, false);
        TextView carNameText = rootView.findViewById(R.id.CarNameText);
        TextView carMakeText = rootView.findViewById(R.id.CarMakeModel);
        carNameText.setText(this.carName);
        carMakeText.setText(this.carMakeModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get navigation controller
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);
    }

}
