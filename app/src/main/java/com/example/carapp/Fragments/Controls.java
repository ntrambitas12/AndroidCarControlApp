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
import com.example.carapp.VehicleConnections.Command;
import com.example.carapp.VehicleConnections.ConnectionManager;

public class Controls extends Fragment {
    private ConnectionManager connectionManager;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get connectionManager instance
        connectionManager = new ViewModelProvider(requireActivity()).get(ConnectionManager.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.controls_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);

        // Implement button clicks
        Button unlockButton = view.findViewById(R.id.unlock);
        Button lockButton = view.findViewById(R.id.Lock);
        Button remoteStart = view.findViewById(R.id.RemoteStart);
        Button remoteStop = view.findViewById(R.id.remoteStop);
        Button hazardsOn = view.findViewById(R.id.hazardsOn);
        Button hazardsOff = view.findViewById(R.id.hazardsOff);
        Button openTrunk = view.findViewById(R.id.trunk);
        Button backButton = view.findViewById(R.id.backButton);

        // Set up click listeners for each button
        unlockButton.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.Unlock);
        });

        lockButton.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.Lock);
        });

        remoteStart.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.RemoteStart);
        });

        remoteStop.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.RemoteStop);
        });

        hazardsOn.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.HazardsOn);
        });

        hazardsOff.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.HazardsOff);
        });

        openTrunk.setOnClickListener(v -> {
            connectionManager.sendToCar(Command.OpenTrunk);
        });

        backButton.setOnClickListener(v -> {
            NavDirections actionGoToDashboard = ControlsDirections.actionControlsToDashboardModern();
            navController.navigate(actionGoToDashboard);
        });

    }
}
