package com.lodong.bluetooth_module;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;





//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ해당 파일은 없엘 예정임 하는것 없음. ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private BluetoothDiscoveryListener discoveryListener;
    private BondStateListener bondStateListener;
    private Context appContext;

    // SPP UUID (예시)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothSocket currentSocket = null;

    public interface BluetoothDiscoveryListener {
        void onDeviceFound(BluetoothDevice device);
        void onDiscoveryFinished();
    }

    public interface BondStateListener {
        void onPairingSuccess(BluetoothDevice device);
        void onPairingFailure(BluetoothDevice device);
    }

    // 기기 검색용 리시버
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    bluetoothDevices.add(device);
                    if (discoveryListener != null) {
                        discoveryListener.onDeviceFound(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveryListener != null) {
                    discoveryListener.onDiscoveryFinished();
                }
            }
        }
    };

    // 페어링 상태 감지용 리시버
    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    @SuppressLint("MissingPermission") int bondState = device.getBondState();
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "Paired with: " + device.getAddress());
                        if (bondStateListener != null) bondStateListener.onPairingSuccess(device);
                    } else if (bondState == BluetoothDevice.BOND_NONE) {
                        Log.d(TAG, "Pairing failed or unpaired: " + device.getAddress());
                        if (bondStateListener != null) bondStateListener.onPairingFailure(device);
                    }
                }
            }
        }
    };

    public BluetoothManager(){

    }
    public BluetoothManager(Context context, BluetoothDiscoveryListener listener) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.discoveryListener = listener;
        this.appContext = context.getApplicationContext();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        appContext.registerReceiver(bluetoothReceiver, filter);

        IntentFilter bondFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        appContext.registerReceiver(bondStateReceiver, bondFilter);
    }

    public void setBondStateListener(BondStateListener listener) {
        this.bondStateListener = listener;
    }

    @SuppressLint("MissingPermission")
    public void startDiscovery() {
        if (!checkBluetoothPermissions(appContext)) {
            throw new SecurityException("Bluetooth permissions are not granted.");
        }
        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            bluetoothDevices.clear();
            bluetoothAdapter.startDiscovery();
            Log.d(TAG, "Discovery started");
        }
    }

    @SuppressLint("MissingPermission")
    public void stopDiscovery() {
        if (!checkBluetoothPermissions(appContext)) {
            throw new SecurityException("Bluetooth permissions are not granted.");
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Discovery stopped");
        }
    }

    public List<BluetoothDevice> getBluetoothDevices() {
        return new ArrayList<>(bluetoothDevices);
    }

    public void release() {
        try {
            appContext.unregisterReceiver(bluetoothReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            appContext.unregisterReceiver(bondStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkBluetoothPermissions(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public void pairDevice(BluetoothDevice device) {
        if (!checkBluetoothPermissions(appContext)) {
            throw new SecurityException("Bluetooth permissions are not granted.");
        }

        try {
            device.createBond();
            Log.d(TAG, "Pairing initiated with: " + device.getAddress());
        } catch (Exception e) {
            Log.e(TAG, "Pairing error: " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    public boolean connectToDevice(BluetoothDevice device) {
        if (!checkBluetoothPermissions(appContext)) {
            throw new SecurityException("Bluetooth permissions are not granted.");
        }

        stopDiscovery();
        try {
            if (currentSocket != null && currentSocket.isConnected()) {
                currentSocket.close();
            }
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();
            currentSocket = socket;
            Log.d(TAG, "Socket connected to: " + device.getAddress());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Could not connect to socket: " + e.getMessage());
            return false;
        }
    }

    public boolean sendCommand(byte[] command) {
        if (currentSocket == null || !currentSocket.isConnected()) {
            Log.e(TAG, "No connected socket available.");
            return false;
        }

        try {
            OutputStream out = currentSocket.getOutputStream();
            out.write(command);
            out.flush();
            Log.d(TAG, "Command sent: " + bytesToHex(command));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to send command: " + e.getMessage());
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
