import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.bluetooth_module.BLEServerManager
import com.lodong.poen.service.BluetoothForegroundService

import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.utils.ApiResponseResult

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
//BluetoothViewModel 클래스는 Bluetooth Low Energy (BLE) 작업을 관리하고,



//BLE 데이터를 수신할 때마다 setBLEDataListener가 호출됩니다.
//updateDiagnosisProgress 메서드가 실행되며(비동기X), _diagnosisProgress 값이 변경됩니다.
//결론은 그냥 메서드 호출하면서 UI업데이트 ㅇㅇ 그니까 두개는 안겹침.


//sendDataToDevice 메서드는 suspend 함수로 정의되어 있어 비동기로 실행됩니다.



class BluetoothViewModel(
    val service: BluetoothForegroundService,
    val repository: BinaryBleRepository, // Repository 주입
    val preferencesHelper: PreferencesHelper


) : ViewModel() {

    private val _sendStatus = MutableLiveData<Result<Boolean>>()
    private val _diagnosisProgress = MutableStateFlow(0f)
    val diagnosisProgress: StateFlow<Float> = _diagnosisProgress
    private var totalReceivedBytes = 0
    private val expectedTotalBytes = 2125 // 예상되는 총 데이터 크기

    private var bleDataListener: ((ByteArray) -> Unit)? = null
    private var lastDataReceivedTime = 0L

    private var completionJob: Job? = null
    private  val PROGRESS_THRESHOLD = 99  // 데이터 기준점
    private  val INITIAL_PROGRESS_MAX = 0.99f  // 95%
    private  val FINAL_PROGRESS = 1.0f  // 100%



    private var chunkListener: ((ByteArray) -> Unit)? = null


    fun setChunkListener(listener: (ByteArray) -> Unit) {
        chunkListener = listener
    }


    private fun checkCompletion() {
        completionJob?.cancel()
        completionJob = viewModelScope.launch {
            delay(3000) // 3초 대기

            // 3초 동안 새로운 데이터가 없었다면 완료 처리
            if (System.currentTimeMillis() - lastDataReceivedTime >= 3000) {
                _diagnosisProgress.value = 1.0f
            }
        }
    }

    fun setBLEDataListener(listener: (ByteArray) -> Unit) {
        bleDataListener = listener

        service.bleManager.setBLEDataListener { data ->
            val dataCount = service.bleManager.getOriginalDataListSize()
            val hexString = data.joinToString(" ") { "%02X".format(it) }

            // 진행률 계산
            val progress = (dataCount.toFloat() / PROGRESS_THRESHOLD * INITIAL_PROGRESS_MAX).coerceIn(0f, INITIAL_PROGRESS_MAX)
            _diagnosisProgress.value = progress

            // 95% 도달시 3초 후 100%
            if (dataCount >= PROGRESS_THRESHOLD) {
                completionJob?.cancel()
                completionJob = viewModelScope.launch {
                    delay(3000)
                    _diagnosisProgress.value = FINAL_PROGRESS
                }
            }

            listener.invoke(data)
        }
    }

    // originalDataList 크기를 가져오는 메서드
    fun getOriginalDataListSize(): Int {
        return service.bleManager.getOriginalDataListSize()
    }


    private fun updateDiagnosisProgress(receivedBytes: Int) {
        totalReceivedBytes += receivedBytes
        val progress = (totalReceivedBytes.toFloat() / expectedTotalBytes).coerceIn(0f, 1f)
        _diagnosisProgress.value = progress
    }

    fun startDiagnosis() {
        totalReceivedBytes = 0
        _diagnosisProgress.value = 0f
        service.bleManager.resetOriginalDataList() // originalDataList 초기화 추가  ///로그 찍히는 부분 초기화임. 진단 시작 누를시 로그 찍히는데 첫번째부터 진행.

    }



    data class DeviceWithStatus(
        val device: BluetoothDevice,
        var status: PairingStatus = PairingStatus.Idle,
        var gatt: BluetoothGatt? = null // GATT 추가
    )

    sealed class PairingStatus {
        object Idle : PairingStatus()
        object Loading : PairingStatus()
        object Success : PairingStatus()
        object Failure : PairingStatus()
    }






    private val _devices = MutableStateFlow<List<DeviceWithStatus>>(emptyList())
    val devices: StateFlow<List<DeviceWithStatus>> = _devices
    val serviceUUID = BLEServerManager.DEFAULT_SERVICE_UUID
    val characteristicUUID = BLEServerManager.DEFAULT_CHARACTERISTIC_UUID

    private val _bondedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bondedDevices: StateFlow<List<BluetoothDevice>> = _bondedDevices



    companion object {
        var instance: BluetoothViewModel? = null

        fun getInstance(
            service: BluetoothForegroundService,
            repository: BinaryBleRepository,
            preferencesHelper: PreferencesHelper
        ): BluetoothViewModel {
            if (instance == null) {
                instance = BluetoothViewModel(service, repository, preferencesHelper)
            }
            return instance!!
        }
    }

    fun startBleScan() {
        val context = service.applicationContext

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BluetoothViewModel", "BLUETOOTH_SCAN permission is missing")
            return
        }



        service.startBleScan { device, serviceUuids, rssi ->
            viewModelScope.launch {
                val existingDevice = _devices.value.find { it.device.address == device.address }
                if (existingDevice == null) {
                    _devices.value = _devices.value + DeviceWithStatus(device)
                    Log.d(
                        "BluetoothViewModel",
                        "Device found: Name=${device.name ?: "Unknown"}, Address=${device.address}, RSSI=$rssi"
                    )
                }
            }
        }
    }


    fun stopBleScan() {
        service.stopBleScan()
    }

    fun clearDevices() {
        // 연결된 디바이스는 유지하도록 수정
        val connectedDevices = _devices.value.filter { it.status == PairingStatus.Success }
        _devices.value = connectedDevices
    }



    fun connectToDevice(
        context: Context,
        device: BluetoothDevice,
        serviceUUID: String,
        characteristicUUID: String,
        dataToSend: ByteArray
    ) {
        viewModelScope.launch {
            // 유효성 검증
            if (serviceUUID.isBlank() || characteristicUUID.isBlank() || dataToSend.size == 0) {
                Log.e(
                    "BluetoothViewModel",
                    "Invalid serviceUUID, characteristicUUID, or dataToSend"
                )
                return@launch
            }

            updateDeviceStatus(device, PairingStatus.Loading)

            service?.connectToDevice(
                device = device,
                serviceUUID = serviceUUID,
                characteristicUUID = characteristicUUID,
                onConnected = { gatt ->
                    Log.d("BluetoothViewModel", "Device connected: ${device.address}")
                    updateDeviceStatus(device, PairingStatus.Success, gatt)
                },
                onDisconnected = {
                    Log.d("BluetoothViewModel", "Device disconnected: ${device.address}")
                    updateDeviceStatus(device, PairingStatus.Failure)
                },
                onConnectionFailed = {
                    Log.e("BluetoothViewModel", "Connection failed for device: ${device.address}")
                    updateDeviceStatus(device, PairingStatus.Failure)
                }
            )

        }

    }



    //11111111111111111111111111111111111111111111 서버에서 가져옴.ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @SuppressLint("MissingPermission")
    suspend fun sendDataToDevice(
        gatt: BluetoothGatt?,
        serviceUUID: String,
        characteristicUUID: String,
        data: ByteArray,
        onChunkSent: ((ByteArray) -> Unit)? = null  // 파라미터 추가
    )
    {
        if (gatt == null) {
            Log.e("BluetoothViewModel", "BluetoothGatt is null")
            return
        }

        try {
            val result = repository.getData() //// 비동기 데이터 처리

            when (result) {
                is ApiResponseResult.Success -> {


                    /** 가공 **/
                    val rawData = result.data ?: ""

                    // 1. 문자열 데이터를 바이너리 데이터로 변환
                    val processedData = try {
                        hexStringToByteArray(rawData)
                    } catch (e: Exception) {
                        Log.e("BluetoothViewModel", "Data processing failed: ${e.localizedMessage}")
                        ByteArray(0) // 빈 데이터로 처리
                    }
                    Log.d("BluetoothViewModel", "Sending data: $data")


                    // 서버에서 받아온 데이터가 비어있지 않을시, BluetoothForegroundSevice의 메서드 호출.
                    if (processedData.isNotEmpty()) {
                        service.sendDataToDevice(
                            gatt,
                            serviceUUID,
                            characteristicUUID,
                            processedData,
                            onChunkSent = { chunk ->
                                chunkListener?.invoke(chunk)
                            }
                        )
                    } else {
                        Log.e("BluetoothViewModel", "Processed data is empty. Nothing to send.")
                    }
                }

                is ApiResponseResult.Error -> {
                    Log.e(
                        "BluetoothViewModel",
                        "Failed to get data from repository: ${result.message}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("BluetoothViewModel", "Exeption While sending data: ${e.localizedMessage}")
        }
        // BluetoothForegroundService의 sendDataToDevice 호출
    }


    @SuppressLint("MissingPermission")
    private fun updateDeviceStatus(
        device: BluetoothDevice,
        status: PairingStatus,
        gatt: BluetoothGatt? = null
    ) {
        _devices.value = _devices.value.map {
            if (it.device.address == device.address) it.copy(status = status, gatt = gatt) else it
        }


        // 페어링 성공 시 기기 정보 저장
        if (status is PairingStatus.Success) {
            _bondedDevices.value = _bondedDevices.value + device
            Log.d(
                "BluetoothViewModel",
                "Device bonded and added: ${device.name} (${device.address})"
            )
        }
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val cleanedHex = hexString.replace("\\s".toRegex(), "") // 공백 제거
        require(cleanedHex.length % 2 == 0) { "Invalid hex string: Length must be even" }
        return ByteArray(cleanedHex.length / 2) {
            val start = it * 2
            cleanedHex.substring(start, start + 2).toInt(16).toByte()
        }
    }
}
