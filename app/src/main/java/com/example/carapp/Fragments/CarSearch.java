package com.example.carapp.Fragments;

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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.example.carapp.Adapters.BluetoothDeviceAdapter;
import com.example.carapp.VehicleConnections.ConnectionManager;
import com.example.carapp.VehicleConnections.WebConnection;
import com.example.carapp.VehicleConnections.BluetoothSearchHelper;
import com.example.carapp.R;
import com.example.carapp.VehicleConnections.BluetoothConnection;
import com.example.carapp.VehicleConnections.IBluetooth;

public class CarSearch extends Fragment {
    private final String TAG = "CarSearchFragment";
    private ListView devicesList;
    private BluetoothSearchHelper BTSearchHelper;
    private ConnectionManager connectionManager;
    private BluetoothDeviceAdapter deviceAdapter;
    private boolean isLayoutRQEnable = false;
    private ViewFlipper viewFlipper;
    private Observer<Boolean> isBluetoothDeviceConnected;
    private Observer<Boolean> isBluetoothOn;

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
        Log.d(TAG, "onCreate executed!");
        // Initialize Bluetooth-related components
        connectionManager = new ViewModelProvider(requireActivity()).get(ConnectionManager.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView executed!");
        if (BTSearchHelper != null) {
            BTSearchHelper.destroyClass();
        }
        connectionManager.endConnection(); // Make sure that we aren't connected to anything as we attempt to pair
        BTSearchHelper = new BluetoothSearchHelper(connectionManager);
        View rootView = inflater.inflate(R.layout.fragment_carsearch, container, false);
        // Setup the button
        setupRQEnableBTButton(rootView);
        // Set the viewFlipper
        viewFlipper = rootView.findViewById(R.id.view_flipper_carSearch);
        // Disable auto-rotation for this fragment
      getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Listen to changes on BT state change
        observeBluetoothData();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        setupUI(view);
        Log.d(TAG, "onViewCreated executed!");
        // Ensure BT is enabled
        if (Boolean.TRUE.equals(BluetoothConnection.BTPowerState.getValue())) {
            connectionManager.startBTScan(true); // Set isPairing to true as we want to scan for devices
        } else {
            // Request to have BT enabled
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtLauncher.launch(turnOnBluetooth);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop executed!");
        BTSearchHelper.clearDiscoveredDevices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView executed!");
        BTSearchHelper.destroyClass();

        // Reset the orientation to allow auto-rotation again
      getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume executed!");
    }

    private void setupUI(View view) {
        devicesList = view.findViewById(R.id.deviceList);
        deviceAdapter = BTSearchHelper.getDeviceAdapter(getActivity());
        devicesList.setAdapter(deviceAdapter);
        setupRefreshButton(view);
        setupDeviceListClickListener();
    }

    private void observeBluetoothData() {


        isBluetoothDeviceConnected = state -> {
            if (state) {
                // Bluetooth device connected, move to the next screen
                NavDirections actionConfirmVIN = CarSearchDirections.actionCarSearchToConfirmCarSelection();
                Activity a = getActivity();
                Navigation.findNavController(getActivity(), R.id.Nav_Dashboard).navigate(actionConfirmVIN);
            }
        };


         isBluetoothOn = state -> {
            if (!state) {
                // If bluetooth state changes while searching, ask user to re-enable
                Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtLauncher.launch(turnOnBluetooth);
            }
        };

        BluetoothConnection.BTPowerState.observe(getViewLifecycleOwner(), isBluetoothOn);
        BluetoothConnection.BTConnectedToPeripheral.observe(getViewLifecycleOwner(), isBluetoothDeviceConnected);
    }


    private void setupRefreshButton(View view) {
        Button refresh = view.findViewById(R.id.refreshButton);
        refresh.setOnClickListener(click -> {
            BTSearchHelper.clearDiscoveredDevices();
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
                // passing VIN as null as we haven't confirmed the device yet
                connectionManager.ConnectToCar(selectedDevice.getAddress(), null);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        BTSearchHelper.destroyClass();
    }

}
