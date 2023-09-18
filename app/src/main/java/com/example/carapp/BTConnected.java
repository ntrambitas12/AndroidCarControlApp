package com.example.carapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.carapp.VehicleConnections.BluetoothConnection;
import com.example.carapp.VehicleConnections.IBluetooth;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BTConnected} factory method to
 * create an instance of this fragment.
 */
public class BTConnected extends Fragment {


    private static final String ARG_PARAM1 = "BluetoothLink";

    // Variable to store bluetooth link
    private IBluetooth bluetoothLink;


    public BTConnected() {
        // Provide the XML layout of the fragment to base constructor here
        super(R.layout.fragment_first);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BTConnectedArgs args = BTConnectedArgs.fromBundle(getArguments());
        bluetoothLink = args.getBluetoothLink();


       Button increment = view.findViewById(R.id.incrementCounter);
        Button decrement = view.findViewById(R.id.decrementCounter);
        TextView countDisplay = view.findViewById(R.id.counterDisplayFragmentFirst);



            // Set onClickEvent Listener
            increment.setOnClickListener(click -> {
                bluetoothLink.sendToCar("Test");
            });
            decrement.setOnClickListener(click -> {
                bluetoothLink.sendToCar("Bye");
            });

    }

    @Override
    public void onStop() {
        super.onStop();
        bluetoothLink.endConnection();
    }
}