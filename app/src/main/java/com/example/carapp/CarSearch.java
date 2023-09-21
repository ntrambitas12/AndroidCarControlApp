package com.example.carapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.carapp.VehicleConnections.BluetoothConnection;
import com.example.carapp.VehicleConnections.IBluetooth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CarSearch extends Fragment {
    private IBluetooth bluetoothLink;
    private ListView devicesList;
    private BluetoothViewModel viewModel;
    private BluetoothDeviceAdapter deviceAdapter;
    private ActivityResultLauncher<Intent> enableBtLauncher;

    public CarSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Bluetooth-related components
        initBluetooth();

        // Register observers
        registerObservers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        observeBluetoothData();
    }

    @Override
    public void onStop() {
        super.onStop();
       // bluetoothLink.endConnection();
        viewModel.clearDiscoveredDevices();
        deviceAdapter.notifyDataSetChanged();
    }



    private void initBluetooth() {
        enableBtLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                viewModel.setBluetoothEnabled(true);
                bluetoothLink.startScan(true); // Set isPairing to true as we want to scan for devices
            } else {
                showBluetoothTurnOnToast();
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothViewModel.class);
        bluetoothLink = new BluetoothConnection(viewModel, getContext());
        bluetoothLink.startScan(true); // Set isPairing to true as we want to scan for devices
    }

    private void registerObservers() {
        final Observer<Boolean> isBluetoothSupported = device -> {
            if (!device) {
                showBluetoothNotSupportedToast();
            }
        };

        final Observer<Boolean> isBluetoothOn = state -> {
            if (!state) {
                enableBluetooth();
            }
        };

        viewModel.bluetoothEnabled.observe(this, isBluetoothOn);
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
            showToastOnConnectionStateChange(state);
            if (state) {
                // Bluetooth device connected, move to the next screen
                Navigation.findNavController(getView()).navigate(R.id.BTConnected);
            }
        };

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

    private void setupDeviceListClickListener() {
        devicesList.setOnItemClickListener((adapterView, view1, pos, id) -> {
            BluetoothDevice selectedDevice = deviceAdapter.getItem(pos);
            if (selectedDevice != null) {
                initiatePairingProcess(selectedDevice);
            }
        });
    }

    private void showBluetoothTurnOnToast() {
        Toast.makeText(getContext(), "Turn Bluetooth on to connect", Toast.LENGTH_LONG).show();
    }

    private void showBluetoothNotSupportedToast() {
        Toast.makeText(getContext(), getString(R.string.bluetooth_not_supported), Toast.LENGTH_LONG).show();
    }

    private void enableBluetooth() {
        Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtLauncher.launch(turnOnBluetooth);
    }

    private void showToastOnConnectionStateChange(boolean isConnected) {
        Toast toast = Toast.makeText(getContext(), isConnected ? "Connected!" : "Disconnected", Toast.LENGTH_LONG);
        toast.show();
    }

    private void initiatePairingProcess(BluetoothDevice selectedDevice) {
        // Logic to start the pairing process
        bluetoothLink.connectToTargetDevice(selectedDevice.getAddress());
    }
}
