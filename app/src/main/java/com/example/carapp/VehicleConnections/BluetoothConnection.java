package com.example.carapp.VehicleConnections;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carapp.BluetoothViewModel;
import com.example.carapp.R;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.BondState;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.WriteType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BluetoothConnection implements IBluetooth, Serializable {
    private JSONObject carState;

    private BluetoothViewModel viewModel;
    private Context context;

    private boolean isPairing = false;
    private boolean servicesDiscovered = false;
    private BluetoothCentralManager BTCentralManager;
    private BluetoothPeripheral connectedPeripheral;
    private final Handler handler = new Handler();
    private final UUID esp32ServiceUUID = UUID.fromString("91bad492-b950-4226-aa2b-4ede9fa42f59");
    private final UUID esp32CharacteristicUUID = UUID.fromString("cba1d466-344c-4be3-ab3f-189f80dd7518");

    private final BluetoothCentralManagerCallback BTCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(BluetoothPeripheral peripheral) {
            // Called when successfully connected. Ready to use device
            if (peripheral.getBondState() == BondState.NONE) {
                // Bond the connected device to reconnect to it later
                peripheral.createBond();
            }
            // Now that device is connected, we can stop scanning
            BTCentralManager.stopScan();
            // Since already connected, isPairing can be set to false
            isPairing = false;
            // Save the connected device
            connectedPeripheral = peripheral;
        }

        @Override
        public void onConnectionFailed(BluetoothPeripheral peripheral, HciStatus status) {
            // Connection Failed. Set flag and alert the activity that the connection was not successful
        }

        @Override
        public void onDisconnectedPeripheral(final BluetoothPeripheral peripheral, final HciStatus status) {
            viewModel.setBluetoothConnected(false);
            // Reconnect to this device when it becomes available again
            handler.postDelayed((Runnable) () -> BTCentralManager.autoConnectPeripheral(peripheral, peripheralCallback), 5000);
        }

        @Override
        public void onDiscoveredPeripheral(@NonNull BluetoothPeripheral peripheral, @NonNull ScanResult scanResult) {
            // Create two branches, one for trying to connect to a known device
            // and another for trying to pair to a new device
            if (isPairing) {
                // Code here to add discovered devices to the list so that user can pair to them
                viewModel.addDiscoveredDevice(scanResult.getDevice());
            } else {
                // We want to just connect to a device already bonded
                if (peripheral.getBondState() == BondState.BONDED) {
                    // If it is, stop scanning and directly connect to it
                    BTCentralManager.stopScan();
                    BTCentralManager.connectPeripheral(peripheral, peripheralCallback);
                }
            }

        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                startScan(isPairing);
                viewModel.setBluetoothEnabled(true);

            } else {
                // Bluetooth is off
                BTCentralManager.stopScan();
                // Set flag that bluetooth is off and alert user to turn it on
                viewModel.setBluetoothEnabled(false);
            }
        }
    };
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            super.onServicesDiscovered(peripheral);
            // Set servicesDiscovered to true
            servicesDiscovered = true;
            viewModel.setBluetoothConnected(true);

            // Set to automatically trigger callback for when car writes new data
            if (connectedPeripheral != null) {
                BluetoothGattService service = connectedPeripheral.getService(esp32ServiceUUID);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(esp32CharacteristicUUID);

                // Set up callback to retrieve the new status from the car
                connectedPeripheral.setNotify(characteristic, true);
            }
        }

        @Override
        public void onCharacteristicUpdate(@NonNull BluetoothPeripheral peripheral, @NonNull byte[] value, @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicUpdate(peripheral, value, characteristic, status);
            if (status == GattStatus.SUCCESS) {
                // Save the current state of the car
                // Parse byte[] as string
                String jsonString = new String(value);

                // Try to parse resp as a carObj
                try {
                    carState = new JSONObject(jsonString);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(@NonNull BluetoothPeripheral peripheral, @NonNull byte[] value, @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicWrite(peripheral, value, characteristic, status);
        }

        @Override
        public void onBondingStarted(@NonNull BluetoothPeripheral peripheral) {
            super.onBondingStarted(peripheral);
        }

        @Override
        public void onBondingSucceeded(@NonNull BluetoothPeripheral peripheral) {
            super.onBondingSucceeded(peripheral);

        }

        @Override
        public void onBondingFailed(@NonNull BluetoothPeripheral peripheral) {
            super.onBondingFailed(peripheral);
        }
    };

    public BluetoothConnection(BluetoothViewModel viewModel, Context context) {
        this.viewModel = viewModel;
        this.context = context;

        carState = new JSONObject();
        BTCentralManager = new BluetoothCentralManager(this.context, BTCentralManagerCallback,
                new Handler(Looper.getMainLooper()));


    }

    // Method to start scanning for ESP32 device. Activity needs to set whether to go into pairing
    // or if to reconnect to a bonded device
    public void startScan(boolean isPairing) {
        if (!BTCentralManager.isScanning()) {
            this.isPairing = isPairing;
            handler.postDelayed((Runnable) () -> BTCentralManager.scanForPeripherals(), 2000);
        }

    }


    // Connect to a specific target device
    public void connectToTargetDevice(String MACAddress) {
        // Get the BT Device
        BluetoothPeripheral peripheral = BTCentralManager.getPeripheral(MACAddress);
        // Ensure that the peripheral is not null
        if (peripheral != null) {
            BTCentralManager.autoConnectPeripheral(peripheral, peripheralCallback);
        }

    }

    @Override
    public boolean isBTEnabled() {
        return BTCentralManager.isBluetoothEnabled();
    }

    @Override
    public boolean isConnected() {
        return viewModel.bluetoothConnected.getValue();
    }

    @Override
    public void endConnection() {
        BTCentralManager.close();
    }

    @Override
    public void sendToCar(Command Payload) {
        // Ensure that we have a device connected
        if (connectedPeripheral != null) {
            BluetoothGattService service = connectedPeripheral.getService(esp32ServiceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(esp32CharacteristicUUID);

                // Send data by writing to the characteristic
                byte[] data = String.valueOf(Payload).getBytes();
                connectedPeripheral.writeCharacteristic(characteristic, data, WriteType.WITHOUT_RESPONSE);
            }
        }
    }

    @Override
    public JSONObject receiveFromCar() {
        // Return either an empty JSON or carState. CarState will eventually get set
        if (this.carState == null) {
            return  new JSONObject();
        } else {
            return this.carState;
        }
    }
}
