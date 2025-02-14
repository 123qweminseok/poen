package com.lodong.poen.viewmodel

import PreferencesHelper
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.poen.dto.batteryinfo.BatteryRequest
import com.lodong.poen.dto.batteryinfo.CarModelInfo
import com.lodong.poen.dto.batteryinfo.ManufacturerInfos
import com.lodong.poen.dto.batteryinfo.QRcodeRequest
import com.lodong.poen.repository.BatteryInfoRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID

class BatteryInfoViewModel(
    private val repository: BatteryInfoRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())

    val imageUris: StateFlow<List<Uri>> = _imageUris


    private val _manufacturers = MutableStateFlow<List<ManufacturerInfos>>(emptyList())
    val manufacturers: StateFlow<List<ManufacturerInfos>> get() = _manufacturers

    private val _car_models = MutableStateFlow<List<CarModelInfo>>(emptyList())
    val models: StateFlow<List<CarModelInfo>> get() = _car_models

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _batteryId = MutableStateFlow<String?>(null)
    val batteryId: StateFlow<String?> = _batteryId

    private val _navigateToQRScreen = MutableStateFlow(false)
    val navigateToQRScreen: StateFlow<Boolean> get() = _navigateToQRScreen
    init {
        // 초기화 시 배터리 ID 로드
        updateBatteryId()
        Log.d("BatteryInfoVM", "ViewModel 초기화됨, 초기 배터리 ID: ${_batteryId.value}")
    }

    // 배터리 ID 업데이트 함수
    private fun updateBatteryId() {
        val currentId = preferencesHelper.getBatteryInfo()["battery_id"]
        Log.d("BatteryInfoVM", "배터리 ID 업데이트: $currentId")
        _batteryId.value = currentId
    }



    fun addImages(newUris: List<Uri>) {
        _imageUris.value = _imageUris.value + newUris
    }

    fun clearImages() {
        _imageUris.value = emptyList()
    }






    fun onNavigatedToQRScreen() {
        Log.d("BatteryInfoVM", "[onNavigatedToQRScreen] 호출됨. _navigateToQRScreen.value = false 로 설정")
        _navigateToQRScreen.value = false
    }

    /**
     * 제조사 목록 조회
     */
    fun fetchManufacturers() {
        Log.d("BatteryInfoVM", "[fetchManufacturers] 함수 진입")
        viewModelScope.launch {
                Log.d("BatteryInfoVM", "[fetchManufacturers] viewModelScope.launch 시작")

            try {
                _loading.value = true
                Log.d("BatteryInfoVM", "[fetchManufacturers] _loading = true 설정")
                _error.value = null
                Log.d("BatteryInfoVM", "[fetchManufacturers] _error = null 설정")

                withContext(Dispatchers.IO + SupervisorJob()) {
                    Log.d("BatteryInfoVM", "[fetchManufacturers] withContext(Dispatchers.IO) 진입")
                    val result = repository.getManufacturerInfos()
                    Log.d("BatteryInfoVM", "[fetchManufacturers] repository.getManufacturerInfos() 호출 완료 => result: $result")

                    result.fold(
                        onSuccess = { response ->
                            Log.d("BatteryInfoVM", "[fetchManufacturers] onSuccess 진입. response.data = ${response.data}")
                            _manufacturers.value = response.data ?: emptyList()
                            Log.d("BatteryInfoVM", "[fetchManufacturers] _manufacturers.value = ${_manufacturers.value}")
                        },
                        onFailure = { exception ->
                            Log.e("BatteryInfoVM", "[fetchManufacturers] onFailure 진입. exception = $exception", exception)
                            if (exception is CancellationException) {
                                Log.d("BatteryInfoVM", "[fetchManufacturers] onFailure: CancellationException 발생")
                            } else {
                                _error.value = exception.message ?: "Unknown error occurred"
                                Log.d("BatteryInfoVM", "[fetchManufacturers] _error.value = ${_error.value}")
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("BatteryInfoVM", "[fetchManufacturers] 전체 try-catch 예외 발생. e = $e", e)
                when (e) {
                    is CancellationException -> {
                        Log.d("BatteryInfoVM", "[fetchManufacturers] CancellationException 발생")
                    }
                    else -> {
                        _error.value = e.message ?: "An unexpected error occurred"
                        Log.d("BatteryInfoVM", "[fetchManufacturers] _error.value = ${_error.value}")
                    }
                }
            } finally {
                Log.d("BatteryInfoVM", "[fetchManufacturers] finally 블록 진입. isActive = $isActive")
                if (isActive) {
                    _loading.value = false
                    Log.d("BatteryInfoVM", "[fetchManufacturers] _loading = false 설정")
                } else {
                    Log.d("BatteryInfoVM", "[fetchManufacturers] 코루틴이 이미 취소됨. _loading.value 설정 스킵")
                }
            }

            Log.d("BatteryInfoVM", "[fetchManufacturers] viewModelScope.launch 종료")
        }
    }

    /**
     * 특정 제조사 ID에 대응하는 모델 정보 조회
     */
    fun fetchModels(manufacturerId: String) {
        Log.d("BatteryInfoVM", "[fetchModels] 함수 진입. manufacturerId = $manufacturerId")
        viewModelScope.launch {
            Log.d("BatteryInfoVM", "[fetchModels] viewModelScope.launch 시작")
            _loading.value = true
            Log.d("BatteryInfoVM", "[fetchModels] _loading = true")
            _error.value = null
            Log.d("BatteryInfoVM", "[fetchModels] _error = null")

            try {
                val result = repository.getCarModelInfos(manufacturerId)
                Log.d("BatteryInfoVM", "[fetchModels] repository.getCarModelInfos() 완료 => result: $result")

                result.fold(
                    onSuccess = { response ->
                        Log.d("BatteryInfoVM", "[fetchModels] onSuccess. response.data = ${response.data}")
                        _car_models.value = response.data ?: emptyList()
                        Log.d("BatteryInfoVM", "[fetchModels] _car_models.value = ${_car_models.value}")
                    },
                    onFailure = { exception ->
                        Log.e("BatteryInfoVM", "[fetchModels] onFailure. exception = $exception", exception)
                        _error.value = exception.message ?: "Unknown error occurred"
                        Log.d("BatteryInfoVM", "[fetchModels] _error.value = ${_error.value}")
                    }
                )
            } catch (e: Exception) {
                Log.e("BatteryInfoVM", "[fetchModels] 전체 try-catch 예외 발생. e = $e", e)
                _error.value = e.message ?: "An unexpected error occurred"
                Log.d("BatteryInfoVM", "[fetchModels] _error.value = ${_error.value}")
            } finally {
                _loading.value = false
                Log.d("BatteryInfoVM", "[fetchModels] finally 블록 진입. _loading = false")
            }

            Log.d("BatteryInfoVM", "[fetchModels] viewModelScope.launch 종료")
        }
    }

    /**
     * 배터리 정보(Preferences) 저장
     */
    fun saveBatteryInfoToPreferences(
        batteryId: String,
        carManufacturerId: String,
        carModelId: String,
        carNo: String,
        productNo: String,
        productionDate: String,
        romId: String,
        carManufacturerName: String,
        carModelName: String
    ) {
        Log.d("BatteryInfoVM", """
            [saveBatteryInfoToPreferences] 호출됨
            - Battery ID: $batteryId
            - Manufacturer ID: $carManufacturerId (Name: $carManufacturerName)
            - Model ID: $carModelId (Name: $carModelName)
            - Car No: $carNo
            - Product No: $productNo
            - Production Date: $productionDate
            - ROM ID: $romId
        """.trimIndent())

        preferencesHelper.saveBatteryInfo(
            batteryId = batteryId,
            carManufacturerId = carManufacturerId,
            carModelId = carModelId,
            carNo = carNo,
            productNo = productNo,
            productionDate = productionDate,
            romId = romId,
            carManufacturerName = carManufacturerName,
            carModelName = carModelName
        )

        val savedInfo = preferencesHelper.getBatteryInfo()
        Log.d("BatteryInfoVM", "[saveBatteryInfoToPreferences] 저장 검증 => $savedInfo")
    }

    /**
     * 배터리 정보 서버 업로드
     */
    fun saveBatteryInfo(request: BatteryRequest, carManufacturerName: String, carModelName: String) {
        Log.d("BatteryInfoVM", "[saveBatteryInfo] 함수 진입 => request: $request")
        Log.d("BatteryInfoVM", "[saveBatteryInfo] ManufacturerName: $carManufacturerName, ModelName: $carModelName")

        viewModelScope.launch {
            Log.d("BatteryInfoVM", "[saveBatteryInfo] viewModelScope.launch 시작")
            _loading.value = true
            Log.d("BatteryInfoVM", "[saveBatteryInfo] _loading = true")
            val result = repository.uploadBatteryInfo(request)
            Log.d("BatteryInfoVM", "[saveBatteryInfo] repository.uploadBatteryInfo() 완료 => result: $result")

            result.onSuccess { response ->
                Log.d("BatteryInfoVM", "[saveBatteryInfo] onSuccess. response = $response")
                response.data?.let { batteryId ->
                    Log.d("BatteryInfoVM", "[saveBatteryInfo] 서버로부터 받은 배터리 ID: $batteryId")
                    saveBatteryInfoToPreferences(
                        batteryId = batteryId,
                        carManufacturerId = request.carManufacturerId,
                        carModelId = request.carModelId,
                        carNo = request.carNo,
                        productNo = request.productNo,
                        productionDate = request.productionDate,
                        romId = request.romId,
                        carManufacturerName = carManufacturerName,
                        carModelName = carModelName
                    )
                    updateBatteryId()  // 배터리 ID 상태 업데이트
                    _navigateToQRScreen.value = true
                    Log.d("BatteryInfoVM", "[saveBatteryInfo] _navigateToQRScreen = true")
                } ?: run {
                    Log.e("BatteryInfoVM", "[saveBatteryInfo] 응답에 배터리 ID가 null입니다.")
                }
            }.onFailure { error ->
                Log.e("BatteryInfoVM", "[saveBatteryInfo] onFailure 발생. error = $error", error)
                _error.value = "저장 중 문제가 발생했습니다. 다시 시도해주세요. Error: ${error.message}"
                Log.d("BatteryInfoVM", "[saveBatteryInfo] _error.value = ${_error.value}")
            }.also {
                _loading.value = false
                Log.d("BatteryInfoVM", "[saveBatteryInfo] finally-like 구문. _loading = false")
            }

            Log.d("BatteryInfoVM", "[saveBatteryInfo] viewModelScope.launch 종료")
        }
    }

    /**
     * QR 코드 등록
     */
    fun registerQRCode(qrCode: String, onSuccess: () -> Unit) {
        Log.d("BatteryInfoVM", "[registerQRCode] 함수 진입 => qrCode: $qrCode")
        val batteryId = preferencesHelper.getBatteryInfo()["battery_id"] ?: run {
            Log.e("BatteryInfoVM", "[registerQRCode] batteryId가 존재하지 않아 함수 중단")
            return
        }
        val savedQrCode = preferencesHelper.getBatteryInfo()["qrcode"]

        if (qrCode == savedQrCode) {
            Log.d("BatteryInfoVM", "[registerQRCode] 이미 동일한 QR 코드가 저장돼 있음. onSuccess() 바로 호출.")
            onSuccess()
            return
        }

        viewModelScope.launch {
            Log.d("BatteryInfoVM", "[registerQRCode] viewModelScope.launch 시작")
            _loading.value = true
            Log.d("BatteryInfoVM", "[registerQRCode] _loading = true")
            _error.value = null
            Log.d("BatteryInfoVM", "[registerQRCode] _error = null")

            val request = QRcodeRequest(qrCode)
            val result = repository.registerQRCode(batteryId, request)
            Log.d("BatteryInfoVM", "[registerQRCode] repository.registerQRCode() 완료 => result: $result")

            result.onSuccess {
                Log.d("BatteryInfoVM", "[registerQRCode] onSuccess. QR코드 등록 성공")
                preferencesHelper.saveQRCode(qrCode)
                Log.d("BatteryInfoVM", "[registerQRCode] 로컬에 QR 코드 저장: $qrCode")
                _loading.value = false
                Log.d("BatteryInfoVM", "[registerQRCode] _loading = false")
                onSuccess()
            }.onFailure {
                Log.e("BatteryInfoVM", "[registerQRCode] onFailure 발생. exception = $it", it)
                _error.value = it.message
                Log.d("BatteryInfoVM", "[registerQRCode] _error.value = ${_error.value}")
                _loading.value = false
                Log.d("BatteryInfoVM", "[registerQRCode] _loading = false")
            }

            Log.d("BatteryInfoVM", "[registerQRCode] viewModelScope.launch 종료")
        }
    }






    /**
     * 이미지 여러 장 업로드
     */
    fun uploadMultipleImages(
        context: Context,
        imageUris: List<Uri>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("BatteryInfoVM", "[uploadMultipleImages] 함수 진입 => imageUris: $imageUris")
        val batteryId = _batteryId.value
        Log.d("BatteryInfoVM", "[uploadMultipleImages] 현재 배터리 ID: $batteryId")

        if (batteryId.isNullOrEmpty()) {
            val errorMsg = "Battery ID is not available."
            Log.e("BatteryInfoVM", "[uploadMultipleImages] $errorMsg => onError() 호출")
            onError(errorMsg)
            return
        }

        viewModelScope.launch {
            Log.d("BatteryInfoVM", "[uploadMultipleImages] viewModelScope.launch 시작")
            _loading.value = true
            _error.value = null

            try {
                // MultipartBody.Part 목록 생성
                val multipartBodies = imageUris.map { uri ->
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                    val inputStream = contentResolver.openInputStream(uri)
                    val extension = mimeType.substringAfter("/")

                    // UUID를 사용하여 고유한 파일명 생성
                    val uniqueFileName = "${UUID.randomUUID()}.${extension}"

                    // 캐시 디렉토리에 임시 파일 생성
                    val file = File(context.cacheDir, uniqueFileName).apply {
                        outputStream().use { outputStream ->
                            inputStream?.use { input ->
                                input.copyTo(outputStream)
                            }
                        }
                    }

                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    // 파일명도 고유한 이름 사용
                    MultipartBody.Part.createFormData("multipartFiles", uniqueFileName, requestFile)
                }
                Log.d("BatteryInfoVM", "[uploadMultipleImages] multipartBodies 준비 완료 => size: ${multipartBodies.size}")

                // 서버 업로드
                val result = repository.uploadImages(batteryId, multipartBodies)
                Log.d("BatteryInfoVM", "[uploadMultipleImages] repository.uploadImages() 완료 => result: $result")

                result.onSuccess {
                    Log.d("BatteryInfoVM", "[uploadMultipleImages] onSuccess. 이미지 업로드 성공")
                    // 임시 파일들 정리
                    context.cacheDir.listFiles()?.forEach { file ->
                        if (file.extension in listOf("jpg", "jpeg", "png", "gif")) {
                            file.delete()
                        }
                    }
                    onSuccess()
                }.onFailure { exception ->
                    Log.e("BatteryInfoVM", "[uploadMultipleImages] onFailure 발생. exception = $exception", exception)
                    onError(exception.message ?: "Failed to upload images")
                }


            } catch (e: Exception) {
                Log.e("BatteryInfoVM", "[uploadMultipleImages] 전체 try-catch 예외 발생. e = $e", e)
                onError(e.message ?: "An unexpected error occurred")
            } finally {
                _loading.value = false
                Log.d("BatteryInfoVM", "[uploadMultipleImages] finally 블록 진입 => _loading = false")
            }

            Log.d("BatteryInfoVM", "[uploadMultipleImages] viewModelScope.launch 종료")
        }
    }
}
