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
import com.example.carapp.ViewModels.BluetoothSearchViewModel;

public class ConfirmCarSelection extends Fragment {

private ViewFlipper viewFlipper;
private BluetoothSearchViewModel viewModel;
private TextView VINDisplay;
private ConnectionManager connectionManager;

    public ConfirmCarSelection() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothSearchViewModel.class);
        connectionManager = new ViewModelProvider(requireActivity()).get(ConnectionManager.class);

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
        String VIN = viewModel.VINLiveData.getValue();
        if (VIN.length() == 0) {
            viewModel.startVINSearch();
        } else if (VIN == "ERROR") {
            viewFlipper.setDisplayedChild(2); // Set the unable to connect screen
        } else {
            VINDisplay.setText(VIN);
            viewFlipper.setDisplayedChild(1); // Set the confirm VIN screen
        }
        return rootView;
    }

    private void setScreenButtons(View rootView) {
        // Set up button click for Unable to connect page
        Button tryAgain = rootView.findViewById(R.id.bt_unable_retry);
        tryAgain.setOnClickListener(click -> {

            NavDirections actionSearchBTDevices = ConfirmCarSelectionDirections.actionConfirmCarSelectionToCarSearch();
            declinedDevice();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(actionSearchBTDevices);
        });

        // Set up yes button to confirm VIN
        Button VINAccepted = rootView.findViewById(R.id.confirm_BT_yes);
        VINAccepted.setOnClickListener(click -> {
            //TODO: SET NAVIGATION ONCE DEVICE IS CONFIRMED
            //NavDirections actionDeviceConfirmed = ConfirmCarSelectionDirections.actionConfirmCarSelectionToBTConnected();
            // Request to bond device once user confirms
           connectionManager.getBluetoothLink().requestBond();
            //Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(actionDeviceConfirmed);
        });

        // Set up no button to decline VIN
        Button VINDeclined = rootView.findViewById(R.id.confirm_BT_no);
        VINDeclined.setOnClickListener(click -> {
            NavDirections actionVINDeclined = ConfirmCarSelectionDirections.actionConfirmCarSelectionToCarSearch();
            declinedDevice();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(actionVINDeclined);

        });
    }
    private void declinedDevice() {
        connectionManager.endConnection();
        viewModel.VINLiveData.setValue("");
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
        viewModel.VINLiveData.observe(getViewLifecycleOwner(), VINFound);
    }
}