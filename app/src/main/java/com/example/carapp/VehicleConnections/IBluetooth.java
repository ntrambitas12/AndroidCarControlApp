package com.example.carapp.VehicleConnections;

import android.bluetooth.BluetoothDevice;
import android.widget.ListView;

import java.util.ArrayList;

public interface IBluetooth extends IConnection {
      void startScan(boolean isPairing);
      void connectToTargetDevice(String MACAddress);
      boolean isBTEnabled();
      boolean isConnected();
      void requestBond();
}
