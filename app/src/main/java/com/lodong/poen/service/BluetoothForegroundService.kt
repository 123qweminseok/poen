package com.lodong.poen.service
import com.lodong.poen.utils.HwToAppProtocol
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lodong.bluetooth_module.BLEManager
import com.lodong.bluetooth_module.BluetoothCallback
import com.lodong.poen.dto.batteryinfo.SensorDataDto
import com.lodong.poen.repository.BinaryBleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.UUID


class BluetoothForegroundService : Service() , BluetoothCallback {

    private val collectedData = mutableListOf<ByteArray>() // 수집된 BLE 데이터
    private lateinit var repository: BinaryBleRepository
    private val delayTimeMillis: Long = 3000 // 3초 대기
    private var sendDataJob: Job? = null

    private val hwToAppProtocol = HwToAppProtocol(this)


    private var isDataTransferScheduled = false // 전송 스케줄링 여부를 추적

    private var isTransferring = false  // 전송 중인지 확인하는 플래그 추가


    companion object {
        private const val CHANNEL_ID = "BluetoothForegroundServiceChannel"
        private const val NOTIFICATION_ID = 1001
    }

//    private val bluetoothViewModel = BluetoothViewModel.instance

    private var currentMtu: Int? = null
    public  lateinit var bleManager: BLEManager
    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothForegroundService = this@BluetoothForegroundService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BluetoothForegroundService", "Service created")
        bleManager = BLEManager(applicationContext)
        repository = BinaryBleRepository(applicationContext) // Repository 초기화
//        repository.setBluetoothService(this) // 서비스 인스턴스 전달


        bleManager.setBluetoothCallback(this)


        startForegroundServiceWithType()
        initializeBLEListener() // BLEDataListener 초기화


    }
    ////////////필수 메서드 구현ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ



    override fun onDataReceived(data: ByteArray) {
        Log.d(TAG, "onDataReceived: ${data.size} bytes")
        synchronized(collectedData) {
            collectedData.add(data)
            CoroutineScope(Dispatchers.IO).launch {
//                sendDataToServer()
            }
        }
    }



    override fun onDataReadyForTransfer() {
        // 전송 중이면 새로운 전송 시작하지 않음
        if (isTransferring) {
            Log.d(TAG, "데이터 전송이 이미 진행 중입니다.")
            return
        }

        val currentDataSize = bleManager.getServerDataSize()
        Log.d(TAG, "현재 수집된 데이터 크기: $currentDataSize")

        if (currentDataSize > 2090) {
            isTransferring = true  // 전송 시작 표시

            sendDataJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "데이터가 2090개 초과하여 3초 후 전송을 시작합니다.")
                    delay(delayTimeMillis)

                    withContext(NonCancellable) {  // 취소 불가능한 컨텍스트 사용
                        sendDataToServer()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Data transfer failed: ${e.message}")
                } finally {
                    isTransferring = false  // 전송 완료 표시
                    hwToAppProtocol.isSendingData = false
                }
            }
        }
    }


    override fun onDataTransferComplete() {
        Log.d(TAG, "Data transfer completed")
        hwToAppProtocol.isSendingData = false
    }

















    private fun startForegroundServiceWithType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification 채널 생성
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bluetooth Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            // Notification 생성
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bluetooth Service")
                .setContentText("블루투스 서비스를 실행 중입니다.")
                .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            // 포그라운드 서비스 시작 시 타입 지정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
                Log.d("BluetoothForegroundService", "Foreground service started")
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } else {
            // Android O 미만 버전 처리
            startForeground(NOTIFICATION_ID, createNotification("Bluetooth Service Running"))
        }
    }

    private fun createNotification(contentText: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bluetooth Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bluetooth Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    // BLE 스캔 시작
    @SuppressLint("MissingPermission")
    fun startBleScan(onDeviceFound: (BluetoothDevice, List<ParcelUuid>?, Int) -> Unit) {
        Log.d("BluetoothForegroundService", "Attempting to start BLE scan")
        bleManager.startScan(object : BLEManager.DeviceFoundCallback {
            override fun onDeviceFound(
                device: BluetoothDevice,
                serviceUuids: List<ParcelUuid>?,
                rssi: Int
            ) {
                Log.d("BluetoothForegroundService", "Device found: ${device.address}, RSSI: $rssi")
                onDeviceFound(device, serviceUuids, rssi)
            }
        })
    }

    // BLE 스캔 중지
    fun stopBleScan() {
        bleManager.stopScan()
    }

    // BLE 기기 연결
    @SuppressLint("MissingPermission")
    fun connectToDevice(
        device: BluetoothDevice,
        serviceUUID: String,
        characteristicUUID: String,
        onConnected: (BluetoothGatt) -> Unit,
        onDisconnected: () -> Unit,
        onConnectionFailed: () -> Unit
    ) {
        bleManager.connectToDevice(device, object : BLEManager.GattCallback {
            override fun onConnectedGatt(gatt: BluetoothGatt) {
                Log.d("BluetoothForegroundService", "Device connected: ${device.address}")
                onConnected(gatt)

                subscribeToNotifications(
                    gatt,
                    serviceUUID,
                    characteristicUUID
                ) { data ->
                    Log.d(
                        "BluetoothForegroundService",
                        "Notification received: ${data.joinToString(", ")}"
                    )

                    // 데이터를 ViewModel에 전달
//                    bluetoothViewModel!!.addRawBytes(data.toList().map { it.toInt() })

                }
            }

            override fun onDisconnected() {
                Log.d("BluetoothForegroundService", "Device disconnected: ${device.address}")
                currentMtu = null // MTU 초기화
                onDisconnected()
            }

            override fun onCharacteristicFound(characteristic: BluetoothGattCharacteristic) {
                Log.d("BluetoothForegroundService", "Characteristic found: ${characteristic.uuid}")
            }

            override fun onDataRead(data: ByteArray) {
                Log.d("BluetoothForegroundService", "Data read: ${String(data)}")
            }

            override fun onConnectionFailed() {
                Log.e("BluetoothForegroundService", "Connection failed: ${device.address}")
                onConnectionFailed()
            }
        })
    }

///////////////////////블루투스 장치에 데이터를 보내기 위한것임.//
    //1) BLE 장치로 데이터 전송하는 부분
//각 청크를 writeCharacteristic()을 통해 BLE 장치로 전송
//하는 방식으로 동작합니다.
    @SuppressLint("MissingPermission")
    fun sendDataToDevice(
        gatt: BluetoothGatt?,
        serviceUUID: String,
        characteristicUUID: String,
        data: ByteArray,
        onChunkSent: ((ByteArray) -> Unit)? = null

) {
        if (gatt == null) {
            Log.e("BluetoothForegroundService", "BluetoothGatt is null")
            return
        }

        val service = gatt.getService(UUID.fromString(serviceUUID))
            ?: run {
                Log.e("BluetoothForegroundService", "Service not found: $serviceUUID")
                return
            }

        val characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID))
            ?: run {
                Log.e("BluetoothForegroundService", "Characteristic not found: $characteristicUUID")
                return
            }

        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) == 0) {
            Log.e("BluetoothForegroundService", "Characteristic not writable")
            return
        }

        // 기본 MTU 또는 협상된 MTU 사용

        val maxPayloadSize = (currentMtu ?: 23) - 3 // ATT 헤더 크기 3을 뺌
        val chunks = splitDataIntoChunks(data, maxPayloadSize)

        for (chunk in chunks) {
            characteristic.value = chunk
            val success = gatt.writeCharacteristic(characteristic)
            if (success) {
                onChunkSent?.invoke(chunk)  // 청크 전송 콜백
            }

            if (!success) {
                Log.e(
                    "BluetoothForegroundService",
                    "Failed to write chunk: ${chunk.contentToString()}"
                )
                break
            }
            Log.d("BluetoothForegroundService", "Chunk sent: ${chunk.contentToString()}")



            // BLE 장치에 따라 약간의 지연이 필요할 수 있음
            Thread.sleep(200)// 지연 시간을 적절히 조정
        }
    }



    //서버에서 받은 데이터를 앱으로 전달.
    private fun splitDataIntoChunks(data: ByteArray, chunkSize: Int): List<ByteArray> {
        val chunks = mutableListOf<ByteArray>()
        var index = 0
        while (index < data.size) {
            val end = (index + chunkSize).coerceAtMost(data.size)
            chunks.add(data.copyOfRange(index, end))
            index = end
        }
        return chunks
    }

    // BLE 알림 구독
    @SuppressLint("MissingPermission")
    fun subscribeToNotifications(
        gatt: BluetoothGatt,
        serviceUUID: String,
        characteristicUUID: String,
        onDataReceived: (ByteArray) -> Unit
    ) {
        val service = gatt.getService(UUID.fromString(serviceUUID))
            ?: run {
                Log.e("BluetoothForegroundService", "Service not found: $serviceUUID")
                return
            }

        val characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID))
            ?: run {
                Log.e("BluetoothForegroundService", "Characteristic not found: $characteristicUUID")
                return
            }

        if (!gatt.setCharacteristicNotification(characteristic, true)) {
            Log.e("BluetoothForegroundService", "Failed to enable notifications")
            return
        }

        val descriptor =
            characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        if (descriptor != null) {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
            Log.d("BluetoothForegroundService", "Notification descriptor written")
        } else {
            Log.e("BluetoothForegroundService", "Descriptor not found for notifications")
        }

//        bleManager.setNotificationCallback((characteristic, data) -> {
//            Log.d("BluetoothViewModel", "Data received from BLE: " + Arrays.toString(data));
//            // ViewModel로 데이터 전달
//            bluetoothViewModel.addIncomingData(data);
//        });

        bleManager.setNotificationCallback { _, data ->
            onDataReceived(data) // 데이터를 ViewModel로 전달
        }
    }


    private fun initializeBLEListener() {
        bleManager.setBLEDataListener { data ->
            Log.d(
                "BluetoothForegroundService",
                "Data received via BLEDataListener: ${data.joinToString(", ")}"
            )
            handleReceivedData(data) // 데이터를 수집 및 처리
        }
    }

    // BluetoothForegroundService.kt
    private fun handleReceivedData(data: ByteArray) {
        Log.d(TAG, "handleReceivedData 호출됨: ${data.size} bytes")

        // 배터리 ID 로그 추가
        val preferencesHelper = PreferencesHelper.getInstance(this)
        val batteryInfo = preferencesHelper.getBatteryInfo()
        Log.d("BatteryInfo", "현재 배터리 ID: ${batteryInfo["battery_id"]}")
        Log.d("BatteryInfo", "전체 배터리 정보: $batteryInfo")

//        synchronized(collectedData) {
//            collectedData.add(data)
//            hwToAppProtocol.analyzeData(collectedData)
//
//        }
//        CoroutineScope(Dispatchers.IO).launch {
////            Log.d(TAG, "서버 전송 시작")
//
////            sendDataToServer()
//        }
        /** 서버송신 **/

    }


    fun notifyDataReadyForTransfer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
//                sendDataToServer()
                hwToAppProtocol.isSendingData = false
            } catch (e: Exception) {
                Log.e("BluetoothService", "Data transfer failed: ${e.message}")
                hwToAppProtocol.isSendingData = false
            }
        }
    }

    ////////// 5. 서버 전송//
    /// TODO: 이파트

    private suspend fun sendDataToServer() {
        try {
            Log.d(TAG, "서버 전송 시작")
            val batteryId = PreferencesHelper.getInstance(this).getString("battery_id")

            if (!batteryId.isNullOrBlank()) {
                //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ중요ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
                //지금 여기서 들어오는 Ble데이터  받고 아래에서 변환함! ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
                val serverData = bleManager.getServerData()

                if (serverData.isNotEmpty()) {
                    // 요청 데이터 로깅
                    Log.d(TAG, "배터리 ID: $batteryId")
                    Log.d(TAG, "전송할 데이터 크기: ${serverData.size}")
                    Log.d(TAG, "전송할 데이터 샘플(처음 10개): ${serverData.take(10)}")

                    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ여기가 변환하는 핵심 작업임!!!!ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
                    val intData = serverData.map { hexStr ->
                        hexStr.toInt(16)
                    }

                    val fullDataString = serverData.joinToString(", ")
                    Log.d(TAG, "전체 전송 데이터: $fullDataString")
                    Log.d(TAG, "전송할 데이터 크기: ${serverData.size}")


                    val sensorDataDtoList = listOf(SensorDataDto(data = intData))
                    Log.d(TAG, "DTO 데이터 구조: $sensorDataDtoList")

                    // API 요청 헤더 로깅
                    val token = PreferencesHelper.getInstance(this).getAccessToken()
                    Log.d(TAG, "Access Token 존재 여부: ${!token.isNullOrEmpty()}")

                    withContext(Dispatchers.IO) {
                        val result = repository.sendCollectedData(batteryId, sensorDataDtoList)
                        result.onSuccess {
                            Log.d(TAG, "데이터 전송 성공")
                            // 전송 성공 후 데이터 초기화
                            bleManager.resetServerData()
                            bleManager.resetOriginalDataList()
                            // 전송 성공 후 모든 데이터 초기화
                            bleManager.resetAllData()

                            Log.d(TAG, "데이터 초기화 완료. 현재 데이터 크기: ${bleManager.getServerDataSize()}")
                        }.onFailure { error ->
                            Log.e(TAG, "데이터 전송 실패: ${error.message}")
                            Log.e(TAG, "Stack trace: ${error.stackTraceToString()}")
                            bleManager.resetServerData()
                            bleManager.resetOriginalDataList()
                            bleManager.resetAllData()

                        }
                    }


                } else {
                    Log.d(TAG, "전송할 데이터가 없습니다")
                }
            } else {
                Log.e(TAG, "배터리 ID가 없습니다")
            }
        } catch (e: Exception) {
            Log.e(TAG, "전송 중 오류 발생: ${e.message}")
            throw e
        }
    }

}
