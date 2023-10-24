package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;

public class NoCarDashboard extends Fragment {

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_no_car, container, false);
        Button addCar = rootView.findViewById(R.id.AddCarNoCar);
        addCar.setOnClickListener(createListener());
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
                if (view.getId() == R.id.AddCarNoCar) {
                    // go to the pair car screen
                    //NavDirections actionGoToCarSearch = NoCarDashboardDirections.actionNoCarDashboardToCarSearch2();
                    //navController.navigate(actionGoToCarSearch);

                    //for now, to debug, add a fake car
                    FirebaseManager firebaseManager = new ViewModelProvider(getActivity()).get(FirebaseManager.class);

                }
            }
        };
    }

}
