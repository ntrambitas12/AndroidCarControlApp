package com.example.carapp.Fragments;

import android.os.Bundle;
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
import androidx.navigation.Navigation;

import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;

import java.util.HashMap;

public class CarInfoFragment extends Fragment {

    private NavController navController;
    private FirebaseManager firebaseManager;
    private HashMap<String, Object> car;
    private EditText carNickname;
    private EditText carMacAddress;
    private EditText carVIN;
    private EditText carColor;
    private int vinNumber;

    public CarInfoFragment(int vinNumber) {
        this.vinNumber = vinNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new ViewModelProvider(requireActivity()).get(FirebaseManager.class);
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

        // TODO Need to pass args thru navigation to know which car to display

        save.setOnClickListener(this.createListener());
        cancel.setOnClickListener(this.createListener());

    }

    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.saveCarInfo) {

                } else if (view.getId() == R.id.cancelCarInfo) {

                }
            }
        };
    }
}
