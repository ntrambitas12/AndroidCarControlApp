package com.example.carapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.example.carapp.VehicleConnections.BluetoothConnection;
import com.example.carapp.VehicleConnections.IBluetooth;

import java.util.ArrayList;
import java.util.List;

public class CarSearch extends Fragment {
    private IBluetooth bluetoothLink;
    private ListView devicesList;
    private BluetoothViewModel viewModel;
    private BluetoothDeviceAdapter deviceAdapter;
    private boolean isLayoutRQEnable = false;
    private ViewFlipper viewFlipper;
    private ActivityResultLauncher<Intent> enableBtLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (!isLayoutRQEnable) {
            if (result.getResultCode() != Activity.RESULT_OK) {
                // Show user a screen to turn on Bluetooth to continue pairing
                isLayoutRQEnable = true;
                viewFlipper.setDisplayedChild(1);
            }
        } else {
            // In layout 2
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Switch back to first layout
                isLayoutRQEnable = false;
                viewFlipper.setDisplayedChild(0);
            }
        }
    });

    public CarSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Bluetooth-related components
        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothViewModel.class);
        bluetoothLink = new BluetoothConnection(viewModel, getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_carsearch, container, false);
        // Setup the button
        setupRQEnableBTButton(rootView);
        // Set the viewFlipper
        viewFlipper = rootView.findViewById(R.id.view_flipper);
        // Disable auto-rotation for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        observeBluetoothData();
        if (bluetoothLink.isBTEnabled()) {
            bluetoothLink.startScan(true); // Set isPairing to true as we want to scan for device{s
        } else {
            // Request to have BT enabled
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtLauncher.launch(turnOnBluetooth);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
       // bluetoothLink.endConnection();
        viewModel.clearDiscoveredDevices();
        deviceAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Reset the orientation to allow auto-rotation again
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void setupUI(View view) {
        devicesList = view.findViewById(R.id.deviceList);
        deviceAdapter = new BluetoothDeviceAdapter(getActivity(), new ArrayList<>());
        devicesList.setAdapter(deviceAdapter);
        setupRefreshButton(view);
        setupDeviceListClickListener();
    }

    private void observeBluetoothData() {
        final Observer<List<BluetoothDevice>> foundDevicesObserver = devices -> {
            deviceAdapter.clear();
            deviceAdapter.addAll(devices);
            deviceAdapter.notifyDataSetChanged();
        };

        final Observer<Boolean> isBluetoothDeviceConnected = state -> {
            if (state) {
                // Bluetooth device connected, move to the next screen
                Navigation.findNavController(getView()).navigate(R.id.BTConnected);
            }
        };


        final Observer<Boolean> isBluetoothOn = state -> {
            if (!state) {
                // If bluetooth state changes while searching, ask user to re-enable
                Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtLauncher.launch(turnOnBluetooth);
            }
        };

        viewModel.bluetoothEnabled.observe(getViewLifecycleOwner(), isBluetoothOn);
        viewModel.discoveredDevices.observe(getViewLifecycleOwner(), foundDevicesObserver);
        viewModel.bluetoothConnected.observe(getViewLifecycleOwner(), isBluetoothDeviceConnected);
    }


    private void setupRefreshButton(View view) {
        Button refresh = view.findViewById(R.id.refreshButton);
        refresh.setOnClickListener(click -> {
            deviceAdapter.clear();
            viewModel.clearDiscoveredDevices();
            deviceAdapter.notifyDataSetChanged();
        });
    }

    private void setupRQEnableBTButton(View rootView) {
        // Set up button for RQ BT on layout
        Button rqBTEnable = rootView.findViewById(R.id.ContinueBTPairing);
        rqBTEnable.setOnClickListener(click -> {
            // Request to have BT enabled
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtLauncher.launch(turnOnBluetooth);
        });
    }



    private void setupDeviceListClickListener() {
        devicesList.setOnItemClickListener((adapterView, view1, pos, id) -> {
            BluetoothDevice selectedDevice = deviceAdapter.getItem(pos);
            if (selectedDevice != null) {
                // Logic to start the pairing process
                bluetoothLink.connectToTargetDevice(selectedDevice.getAddress());
            }
        });
    }


}
