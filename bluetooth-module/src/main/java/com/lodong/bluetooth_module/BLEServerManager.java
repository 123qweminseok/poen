package com.lodong.bluetooth_module;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.UUID;

public class BLEServerManager {
    private static final String TAG = "BLEServerManager";

    // 기본 상수 기반 UUID
    public static final String DEFAULT_SERVICE_UUID = "49535343-FE7D-4AE5-8FA9-9FAFD205E455";
    public static final String DEFAULT_CHARACTERISTIC_UUID = "49535343-1E4D-4BD9-BA61-23C647249616";


    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private BluetoothGattServer gattServer;

    public BLEServerManager(Context context) {
        this.context = context.getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            throw new IllegalStateException("BluetoothManager is not available.");
        }
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        if (this.bluetoothAdapter == null) {
            throw new IllegalStateException("BluetoothAdapter is not available.");
        }
    }

    /**
     * 상수 기반 GATT 서버 시작
     */
    @SuppressLint("MissingPermission")
    public void startGattServerWithConstants() {
        startGattServer(DEFAULT_SERVICE_UUID, DEFAULT_CHARACTERISTIC_UUID);
    }

    /**
     * 동적 UUID 기반 GATT 서버 시작
     */
    @SuppressLint("MissingPermission")
    public void startGattServerWithDynamicUUID(String serviceUUID, String characteristicUUID) {
        startGattServer(serviceUUID, characteristicUUID);
    }

    /**
     * 내부적으로 GATT 서버를 초기화
     */
    @SuppressLint("MissingPermission")
    private void startGattServer(String serviceUUID, String characteristicUUID) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        gattServer = bluetoothManager.openGattServer(context, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    Log.d(TAG, "Device connected: " + device.getAddress());

                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.d(TAG, "Device disconnected: " + device.getAddress());
                }
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic,
                                                     boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                String receivedData = new String(value);
                Log.d(TAG, "Received data: " + receivedData);

                String responseData = "Response: " + receivedData;
                characteristic.setValue(responseData.getBytes());
                if (responseNeeded) {
                    gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, responseData.getBytes());
                }
            }
        });

        // GATT 서비스 추가
        BluetoothGattService service = new BluetoothGattService(UUID.fromString(serviceUUID), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(characteristicUUID),
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ
        );
        service.addCharacteristic(characteristic);
        gattServer.addService(service);

        Log.d(TAG, "GATT server started with service UUID: " + serviceUUID);
        startAdvertising(serviceUUID);
    }

    /**
     * Advertising 시작
     */
    @SuppressLint("MissingPermission")
    private void startAdvertising(String serviceUUID) {
        BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null) {
            Log.e(TAG, "BLE Advertiser not available");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(new ParcelUuid(UUID.fromString(serviceUUID)))
                .build();

        advertiser.startAdvertising(settings, data, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "Advertising started successfully.");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertising failed with error code: " + errorCode);
            }
        });
    }

    /**
     * GATT 서버 중지
     */
    @SuppressLint("MissingPermission")
    public void stopGattServer() {
        if (gattServer != null) {
            gattServer.close();
            gattServer = null;
            Log.d(TAG, "GATT server stopped.");
        }
    }
}
