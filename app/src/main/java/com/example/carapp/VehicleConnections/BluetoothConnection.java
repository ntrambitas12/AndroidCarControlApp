package com.example.carapp.VehicleConnections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

import java.io.Serializable;
import java.util.UUID;

public class BluetoothConnection implements IBluetooth, Serializable {
    /* Public Variables for viewModel*/
    public static final MutableLiveData<Boolean> BTConnectedToPeripheral = new MutableLiveData<>(false);
    public static final MutableLiveData<Boolean> BTPowerState = new MutableLiveData<>(true);
    public static final MutableLiveData<BluetoothDevice> discoveredDevices = new MutableLiveData<>();


    // Private variables
    private boolean isPairing = false;
    private final BluetoothCentralManager BTCentralManager;
    private BluetoothPeripheral connectedPeripheral;
    private final Handler handler = new Handler();
    private final MutableLiveData<JSONObject> carResp = new MutableLiveData<>();
    private final UUID esp32ServiceUUID = UUID.fromString("91bad492-b950-4226-aa2b-4ede9fa42f59");
    private final UUID esp32WriteCharacteristicUUID = UUID.fromString("cba1d466-344c-4be3-ab3f-189f80dd7518");
    private final UUID esp32ReadCharacteristicUUID = UUID.fromString("c8ca6af0-5c97-11ee-9b23-8b8ec8fa712a");
    private Runnable autoConnectRunnable;

    private final BluetoothCentralManagerCallback BTCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(BluetoothPeripheral peripheral) {
            // Called when successfully connected. Ready to use device
//            if (peripheral.getBondState() == BondState.NONE) {
//                peripheral.createBond();
//            }
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
            BTConnectedToPeripheral.setValue(false);
            // Reconnect to this device when it becomes available again
               autoConnectRunnable = () -> {
                        if (connectedPeripheral != null) {
                            BTCentralManager.autoConnectPeripheral(peripheral, peripheralCallback);
                        }
                    };
               handler.postDelayed(autoConnectRunnable, 5000);
        }

        @Override
        public void onDiscoveredPeripheral(@NonNull BluetoothPeripheral peripheral, @NonNull ScanResult scanResult) {
            // Create two branches, one for trying to connect to a known device
            // and another for trying to pair to a new device
            if (isPairing) {
                // Code here to add discovered devices to the list so that user can pair to them
                discoveredDevices.setValue(scanResult.getDevice());
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
                BTPowerState.setValue(true);

            } else {
                // Bluetooth is off
                BTCentralManager.stopScan();
                // Set flag that bluetooth is off and alert user to turn it on
                BTPowerState.setValue(false);
            }
        }
    };
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            super.onServicesDiscovered(peripheral);
            BTConnectedToPeripheral.setValue(true);
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
                        // Add newly received carState data to the queue
                        carResp.setValue(new JSONObject(jsonString));
                    } catch (JSONException e) {
                        // error not being able to parse JSON
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
        public void onBondLost(@NonNull BluetoothPeripheral peripheral) {
            super.onBondLost(peripheral);
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothPeripheral peripheral) {
            super.onBondingFailed(peripheral);
        }
    };

    public BluetoothConnection(Context context) {
        BTCentralManager = new BluetoothCentralManager(context, BTCentralManagerCallback,
                new Handler(Looper.getMainLooper()));
        BTPowerState.setValue(BTCentralManager.isBluetoothEnabled()); // Set the initial value
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
    public void endConnection() {
        if (connectedPeripheral != null) {
            connectedPeripheral.cancelConnection();
            connectedPeripheral = null;
            if (autoConnectRunnable !=  null) {
                handler.removeCallbacks(autoConnectRunnable);
            }
            BTConnectedToPeripheral.setValue(false);
        }

    }

    @Override
    public void sendToCar(Command Payload) {
        // Ensure that we have a device connected
        if (connectedPeripheral != null) {
            BluetoothGattService service = connectedPeripheral.getService(esp32ServiceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(esp32WriteCharacteristicUUID);

                // Send data by writing to the characteristic
                byte[] data = String.valueOf(Payload).getBytes();
                connectedPeripheral.writeCharacteristic(characteristic, data, WriteType.WITH_RESPONSE);
            }
        }
    }

    @Override
    public LiveData<JSONObject> receiveFromCar() {
        if (connectedPeripheral != null) {
            BluetoothGattService service = connectedPeripheral.getService(esp32ServiceUUID);
            if (service != null) {
                BluetoothGattCharacteristic readCharacteristic = service.getCharacteristic(esp32ReadCharacteristicUUID);
                connectedPeripheral.readCharacteristic(readCharacteristic);
            }
        }
        return carResp;
    }
    public void requestBond() {
        if (connectedPeripheral != null && connectedPeripheral.getBondState() != BondState.BONDED) {
            connectedPeripheral.createBond();
        }
    }

    @Override
    public String getConnectedDeviceUUID() {
        if (connectedPeripheral != null) {
            return connectedPeripheral.getAddress();
        } else {
            return "NO_MAC_ERR";
        }
    }
}


