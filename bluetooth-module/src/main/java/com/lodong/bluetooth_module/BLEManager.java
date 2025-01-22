package com.lodong.bluetooth_module;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import kotlin.text.Charsets;

public class BLEManager {
    private static final String TAG = "BLEManager";
    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private ScanCallback scanCallback;
    private final Set<String> discoveredDevices = new HashSet<>();
    private BluetoothGatt currentGatt;
    private NotificationCallback notificationCallback; // Notification 콜백 추가
    private BLEDataListener bleDataListener; // BLEDataListener 참조
    private final List<byte[]> originalDataList = new ArrayList<>();  // 원본 데이터 저장용 리스트 추가

    public BLEManager(Context context) {
        this.context = context.getApplicationContext();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.bluetoothAdapter == null) {
            throw new IllegalStateException("BluetoothAdapter is not available.");
        }
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    // BLE 스캔 시작
    @SuppressLint("MissingPermission")
    public void startScan(DeviceFoundCallback callback) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled.");
            return;
        }

        if (scanCallback != null) {
            stopScan();
        }
//스캔하는 부분임
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)  // 최대 스캔 속도
                .setReportDelay(0)  // 결과 즉시 보고
                .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                if (!discoveredDevices.contains(device.getAddress())) {
                    discoveredDevices.add(device.getAddress());
                    List<ParcelUuid> serviceUuids = result.getScanRecord() != null ?
                            result.getScanRecord().getServiceUuids() : null;

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onDeviceFound(device, serviceUuids, result.getRssi());
                        }
                    });
                }
            }


            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "Scan failed with error code: " + errorCode);
            }
        };

        try {
            bluetoothAdapter.getBluetoothLeScanner().startScan(null, settings, scanCallback);
        } catch (Exception e) {
            Log.e(TAG, "Error starting scan: " + e.getMessage());
        }
    }



    // BLE 기기 연결

    @SuppressLint("MissingPermission")
    public void stopScan() {
        if (scanCallback != null && bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            try {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                Log.d(TAG, "Scan stopped successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping scan: " + e.getMessage());
            } finally {
                scanCallback = null;
                discoveredDevices.clear();
            }
        }
    }

    public void setBLEDataListener(BLEDataListener listener) {
        this.bleDataListener = listener;
    }



    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device, GattCallback callback) {
        currentGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Connected to GATT server");
                    Log.d(TAG, gatt.toString());
                    callback.onConnectedGatt(gatt);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> gatt.discoverServices(), 2000);
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.d(TAG, "Device disconnected.");
                    callback.onDisconnected();
                    currentGatt = null;
                } else {
                    Log.e(TAG, "Connection failed with status: " + status);
                    callback.onConnectionFailed();
                }
            }
            // 서비스 발견 시 호출

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Services discovered");

                    // 서비스와 캐릭터리스틱 찾기
                    BluetoothGattService service = gatt.getService(UUID.fromString(BLEServerManager.DEFAULT_SERVICE_UUID)); // 대상 서비스 UUID
                    if (service != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BLEServerManager.DEFAULT_CHARACTERISTIC_UUID)); // 대상 캐릭터리스틱 UUID
                        if (characteristic != null) {
                            // 알림 활성화
                            boolean notificationSet = gatt.setCharacteristicNotification(characteristic, true);
                            if (notificationSet) {
                                Log.d(TAG, "Notifications enabled for characteristic: " + characteristic.getUuid());

                                // CCCD 디스크립터 탐색 및 설정
                                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                                    Log.d(TAG, "Descriptor UUID: " + descriptor.getUuid());
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                    Log.d(TAG, "CCCD written for characteristic: " + characteristic.getUuid());
                                }

                            } else {
                                Log.e(TAG, "Failed to enable notifications for characteristic: " + characteristic.getUuid());
                            }
                        } else {
                            Log.e(TAG, "Characteristic not found in service: " + service.getUuid());
                        }
                    } else {
                        Log.e(TAG, "Service not found: " + "SERVICE_UUID_HERE");
                    }
                } else {
                    Log.e(TAG, "Service discovery failed with status: " + status);
                }
            }

//중요0// 1. BLEManager에서 데이터 최초 수신
            // TODO 이파트
// BLEManager.java의 onCharacteristicChanged 메서드 수정
            //BLE장치에서 앱으로 데이터 수신하는 부분임. BLE 장치에서 데이터를 처음 수신하는 핵심적인 부분!!
@Override
public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    byte[] data = characteristic.getValue();

    // 원본 데이터 저장 및 로그
    originalDataList.add(data);
    StringBuilder hexString = new StringBuilder();
    for (byte b : data) {
        hexString.append(String.format("%02X ", b));
    }
    Log.d(TAG, "원본 데이터(" + originalDataList.size() + "번째): " + hexString.toString());
    Log.d(TAG, "누적된 원본 데이터 개수: " + originalDataList.size());

    if (bleDataListener != null) {
        bleDataListener.onDataReceived(data);
    }
}


        });
    }

    @SuppressLint("MissingPermission")
    public void subscribeToNotifications(BluetoothGatt gatt, String serviceUUID, String characteristicUUID, NotificationCallback callback) {
        BluetoothGattService service = gatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            Log.e(TAG, "Service not found: " + serviceUUID);
            return;
        }

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            Log.e(TAG, "Characteristic not found: " + characteristicUUID);
            return;
        }

        boolean notificationSet = gatt.setCharacteristicNotification(characteristic, true);
        if (!notificationSet) {
            Log.e(TAG, "Failed to set notification for characteristic: " + characteristicUUID);
            return;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
            this.notificationCallback = callback;
            Log.d(TAG, "Notification subscription successful for characteristic: " + characteristicUUID);
        } else {
            Log.e(TAG, "Descriptor not found for characteristic: " + characteristicUUID);
        }
    }

    public interface DeviceFoundCallback {
        void onDeviceFound(BluetoothDevice device, @Nullable List<ParcelUuid> serviceUuids, int rssi);
//        void onScanFailed(int errorCode);
    }

    public interface GattCallback {
        void onConnectedGatt(BluetoothGatt gatt);

        void onDisconnected();

        void onCharacteristicFound(BluetoothGattCharacteristic characteristic);

        void onDataRead(byte[] data);

        void onConnectionFailed();
    }

    public interface DataTransferCallback {
        void onDataTransferSuccess();

        void onDataTransferFailed(String error);
    }

    public void setNotificationCallback(NotificationCallback callback) {
        this.notificationCallback = callback;
    }

    public interface NotificationCallback {
        void onNotificationReceived(BluetoothGattCharacteristic characteristic, byte[] data);
    }
    public interface BLEDataListener {
        void onDataReceived(byte[] data);
    }

    public int getOriginalDataListSize() {
        return originalDataList.size();
    }
//갯수 초기화임. originalDataList랑 다 연결되어있음. 문제 없다.
    public void resetOriginalDataList() {
        originalDataList.clear();
    }


}
