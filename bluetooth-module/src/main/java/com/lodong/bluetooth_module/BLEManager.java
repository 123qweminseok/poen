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

//import com.lodong.poen.service.BluetoothForegroundService;

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
    private final List<String> serverDataList = new ArrayList<>();


    private static final byte[] SENSOR_DATA_HEADER = {0x02, (byte)0x82, (byte)0x82, 0x00, 0x0E};
    private static final byte[] END_HEADER = {(byte)0xAA, (byte)0xAA, 0x03};
    private static final int HEADER_SIZE = 5;
    private static final int END_SIZE = 3;
    private static final int SENSOR_DATA_SIZE = 17;

    private static final long DATA_TIMEOUT = 13500; // 15초

    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    private long lastDataReceivedTime = 0;





    public BLEManager(Context context) {
        this.context = context.getApplicationContext();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initTimeoutCheck(); // 타임아웃 체크 초기화

        if (this.bluetoothAdapter == null) {
            throw new IllegalStateException("BluetoothAdapter is not available.");
        }
    }


    private BluetoothCallback bluetoothCallback;

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }



    public void setBluetoothCallback(BluetoothCallback callback) {
        this.bluetoothCallback = callback;
    }



    // BLE 스캔 시작
    @SuppressLint("MissingPermission")
    public void startScan(DeviceFoundCallback callback) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled.");
            lastDataReceivedTime = System.currentTimeMillis(); // 초기 시간 설정
            timeoutHandler.postDelayed(timeoutRunnable, 1000);
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
                timeoutHandler.removeCallbacks(timeoutRunnable);  // 여기에 추가

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

                    // 연결 성공 시 타임아웃 체크 시작
                    lastDataReceivedTime = System.currentTimeMillis();
                    timeoutHandler.postDelayed(timeoutRunnable, 1000);
                    Log.d(TAG, "타임아웃 체크 시작됨");



                    new Handler(Looper.getMainLooper()).postDelayed(() -> gatt.discoverServices(), 2000);
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.d(TAG, "Device disconnected.");
                    timeoutHandler.removeCallbacks(timeoutRunnable);  // 타임아웃 체크 중지
                    Log.d(TAG, "타임아웃 체크 중지됨");
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
                lastDataReceivedTime = System.currentTimeMillis(); // 데이터 수신 시간 업데이트



                // 기존 originalDataList 처리 유지
                originalDataList.add(data);
                StringBuilder hexString = new StringBuilder();
                for (byte b : data) {
                    hexString.append(String.format("%02X ", b));
                }
                Log.d(TAG, "원본 데이터(" + originalDataList.size() + "번째): " + hexString.toString());
                Log.d(TAG, "누적된 원본 데이터 개수: " + originalDataList.size());

                // 데이터 파싱 및 처리
                processPacketData(data);

                // 기존 리스너 콜백 유지
                if (bleDataListener != null) {
                    bleDataListener.onDataReceived(data);
                }

                if (bluetoothCallback != null) {
                    bluetoothCallback.onDataReceived(data);
                    bluetoothCallback.onDataReadyForTransfer();
                }
            }

            private void processPacketData(byte[] data) {
                int index = 0;
                while (index < data.length) {
                    // 셋업 패킷 스킵 (02 82 80 또는 02 82 81로 시작하는 패킷)
                    if (index + 2 < data.length &&
                            data[index] == 0x02 &&
                            data[index + 1] == (byte)0x82 &&
                            (data[index + 2] == (byte)0x80 || data[index + 2] == (byte)0x81)) {

                        // AA AA 03을 찾아서 그 다음으로 이동
                        index = findNextEndHeader(data, index) + 3;
                        continue;
                    }

                    // 센서 데이터 헤더 찾기 (02 82 82 00 0E)
                    if (index + 4 < data.length &&
                            data[index] == 0x02 &&
                            data[index + 1] == (byte)0x82 &&
                            data[index + 2] == (byte)0x82 &&
                            data[index + 3] == 0x00 &&
                            data[index + 4] == 0x0E) {

                        // 헤더(5바이트) 건너뛰기
                        index += 5;

                        // 센서 데이터 17바이트(END 포함) 추출
                        if (index + 17 <= data.length) {
                            byte[] chunk = Arrays.copyOfRange(data, index, index + 17);
                            synchronized(serverDataList) {
                                for (byte b : chunk) {
                                    serverDataList.add(String.format("%02X", b));
                                }
                                printServerData();

                                Log.d(TAG, "저장된 17바이트 데이터(END 포함): " + bytesToHexString(chunk));
                                Log.d(TAG, "현재 서버 데이터 크기: " + serverDataList.size());
                            }
                            index += 17;
                        } else {
                            break;
                        }
                    } else {
                        index++;
                    }
                } }
        });
    }


    //서버로 보내는 데이터 17바이트씩 자르기 모음
    // 유틸리티 메서드 추가
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }


    private boolean isSensorDataHeader(byte[] data) {
        for (int i = 0; i < HEADER_SIZE; i++) {
            if (data[i] != SENSOR_DATA_HEADER[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isEndHeader(byte[] data, int offset) {
        if (offset + END_SIZE > data.length) return false;
        for (int i = 0; i < END_SIZE; i++) {
            if (data[offset + i] != END_HEADER[i]) {
                return false;
            }
        }
        return true;
    }




    private int findNextEndHeader(byte[] data, int startIndex) {
        for (int i = startIndex; i < data.length - 2; i++) {
            if (data[i] == (byte)0xAA &&
                    data[i + 1] == (byte)0xAA &&
                    data[i + 2] == 0x03) {
                return i;
            }
        }
        return data.length - 3;
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

    ////ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 서버 전송용 데이터 반환
    public List<String> getServerData() {
        synchronized(serverDataList) {
            return new ArrayList<>(serverDataList);
        }
    }

    public int getServerDataSize() {
        synchronized(serverDataList) {
            return serverDataList.size();
        }
    }

    public void resetServerData() {
        synchronized(serverDataList) {
            serverDataList.clear();
            Log.d(TAG, "서버 전송용 데이터 초기화 완료. 현재 데이터 크기: " + serverDataList.size());
        }
    }

    public void resetAllData() {
        originalDataList.clear();
        synchronized(serverDataList) {
            serverDataList.clear();
            Log.d(TAG, "모든 데이터 초기화 완료. 현재 서버 데이터 크기: " + serverDataList.size());
        }
    }

    // BLEManager.java에 추가
    public void printServerData() {
        synchronized(serverDataList) {
            Log.d(TAG, "========== 서버 전송 데이터 목록 ==========");
            Log.d(TAG, "전체 데이터 크기: " + serverDataList.size());
            Log.d(TAG, "데이터: " + serverDataList);
            Log.d(TAG, "=========================================");
        }
    }


//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ서버측


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

    private void initTimeoutCheck() {
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastData = currentTime - lastDataReceivedTime;
                Log.d(TAG, "타임아웃 체크 중: 마지막 데이터로부터 " + timeSinceLastData + "ms 경과");

                if (timeSinceLastData > DATA_TIMEOUT) {
                    Log.w(TAG, "타임아웃 발생: " + DATA_TIMEOUT + "ms 동안 데이터 없음");
                    Log.d(TAG, "초기화 전 원본 데이터 크기: " + originalDataList.size());
                    Log.d(TAG, "초기화 전 서버 데이터 크기: " + serverDataList.size());
                    originalDataList.clear();  // serverDataList 대신 originalDataList 초기화
                    synchronized(serverDataList) {
                        serverDataList.clear(); // 서버 데이터 초기화
                    }

                    Log.d(TAG, "원본 데이터,서버 전송용 데이터  초기화 완료. 현재 크기: " + originalDataList.size());

                }
                timeoutHandler.postDelayed(this, 5000);  // 1초마다 체크
            }
        };
        Log.d(TAG, "타임아웃 체크 초기화됨 (타임아웃: " + DATA_TIMEOUT + "ms)");
    }


}