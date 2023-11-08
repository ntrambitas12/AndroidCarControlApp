package com.example.carapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.carapp.R;
import com.example.carapp.VehicleConnections.ConnectionManager;
import com.example.carapp.VehicleConnections.BluetoothSearchHelper;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmCarSelection extends Fragment {

private ViewFlipper viewFlipper;
private BluetoothSearchHelper bluetoothSearchHelper;
private FirebaseManager firebaseManager;
private TextView VINDisplay;
private ConnectionManager connectionManager;

    public ConfirmCarSelection() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionManager = new ViewModelProvider(requireActivity()).get(ConnectionManager.class);
        bluetoothSearchHelper = new BluetoothSearchHelper(connectionManager);
        firebaseManager = new ViewModelProvider(requireActivity()).get(FirebaseManager.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_btdevice, container, false);
        viewFlipper = rootView.findViewById(R.id.view_flipper_bt_confirm);

        // Set buttons on views
        setScreenButtons(rootView);

        // Setup TextView that renders VIN
        VINDisplay = rootView.findViewById(R.id.VIN_display);
        // Setup Observers
        setupObservers();
        //Ensure the right view is displayed on load
        String VIN = bluetoothSearchHelper.VINLiveData.getValue();
        if (VIN.length() == 0) {
            bluetoothSearchHelper.startVINSearch();
        } else if (VIN == "ERROR") {
            viewFlipper.setDisplayedChild(2); // Set the unable to connect screen
        } else {
            VINDisplay.setText(VIN);
            viewFlipper.setDisplayedChild(1); // Set the confirm VIN screen
        }
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        connectionManager.endConnection();
    }

    private void setScreenButtons(View rootView) {
        // Set up button click for Unable to connect page
        Button tryAgain = rootView.findViewById(R.id.bt_unable_retry);
        tryAgain.setOnClickListener(click -> {

            NavDirections actionSearchBTDevices = ConfirmCarSelectionDirections.actionConfirmCarSelectionToCarSearch();
            connectionManager.endConnection();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(actionSearchBTDevices);
        });

        // Set up yes button to confirm VIN
        Button VINAccepted = rootView.findViewById(R.id.confirm_BT_yes);
        VINAccepted.setOnClickListener(click -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null ) {
                String VIN = bluetoothSearchHelper.VINLiveData.getValue();
                String uid = user.getUid();
                String BTMacAddr = connectionManager.getBluetoothLink().getConnectedDeviceUUID();
                //TODO: Add flow to get nickname and color
                String nickname = "TEST";
                String color = "#1234abc";
                firebaseManager.addNewCar(uid,BTMacAddr, nickname,VIN,color);
                // Officially Connect to the car by also passing in its VIN
                connectionManager.ConnectToCar(BTMacAddr, VIN);
                NavDirections actionDeviceConfirmed = ConfirmCarSelectionDirections.actionConfirmCarSelectionToDashboardFragment();
                // Request to bond device once user confirms
                connectionManager.getBluetoothLink().requestBond();
                bluetoothSearchHelper.destroyClass();
                Navigation.findNavController(getActivity(), R.id.Nav_Dashboard).navigate(actionDeviceConfirmed);
            } else {
                // Error has occured
                //TODO: handle this case here
            }
        } );

        // Set up no button to decline VIN
        Button VINDeclined = rootView.findViewById(R.id.confirm_BT_no);
        VINDeclined.setOnClickListener(click -> {
            NavDirections actionVINDeclined = ConfirmCarSelectionDirections.actionConfirmCarSelectionToCarSearch();
            connectionManager.endConnection();
            Navigation.findNavController(getActivity(), R.id.Nav_Dashboard).navigate(actionVINDeclined);

        });
    }

    private void setupObservers() {
        final Observer<String> VINFound = VIN -> {
            if (VIN == "ERROR") {
                // No VIN was detected
                viewFlipper.setDisplayedChild(2); // Set the unable to connect screen
            } else if (VIN.length() != 0) {
                VINDisplay.setText(VIN);
                viewFlipper.setDisplayedChild(1); // Set the confirm VIN screen
            }
        };
        bluetoothSearchHelper.VINLiveData.observe(getViewLifecycleOwner(), VINFound);
    }
}