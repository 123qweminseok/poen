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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID

class BatteryInfoViewModel(
    private val repository: BatteryInfoRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _manufacturers = MutableStateFlow<List<ManufacturerInfos>>(emptyList())
    val manufacturers: StateFlow<List<ManufacturerInfos>> get() = _manufacturers

    private val _car_models = MutableStateFlow<List<CarModelInfo>>(emptyList())
    val models: StateFlow<List<CarModelInfo>> get() = _car_models

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _batteryId = MutableStateFlow(preferencesHelper.getBatteryInfo()["battery_id"])
    val batteryId: StateFlow<String?> = _batteryId

    private val _navigateToQRScreen = MutableStateFlow(false)
    val navigateToQRScreen: StateFlow<Boolean> get() = _navigateToQRScreen


    fun onNavigatedToQRScreen() {
        _navigateToQRScreen.value = false // Reset state after navigation
    }


    fun fetchManufacturers() {
        viewModelScope.launch {
            Log.d("BatteryInfoVM", "Fetching manufacturers")

            _loading.value = true
            _error.value = null
            try {
                val result = repository.getManufacturerInfos()
                result.fold(
                    onSuccess = { response ->
                        Log.d("BatteryInfoVM", "Manufacturers fetched successfully: ${response.data}")

                        _manufacturers.value = response.data!! // Adjusting based on repository response
                    },
                    onFailure = { exception ->
                        Log.e("BatteryInfoVM", "Failed to fetch manufacturers", exception)

                        _error.value = exception.message ?: "Unknown error occurred"
                    }
                )
            } catch (e: Exception) {
                Log.e("BatteryInfoVM", "Exception in fetchManufacturers", e)

                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchModels(manufacturerId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repository.getCarModelInfos(manufacturerId)
                result.fold(
                    onSuccess = { response ->
                        _car_models.value = response.data ?: emptyList()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Unknown error occurred"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _loading.value = false
            }
        }
    }


    fun saveBatteryInfoToPreferences(
        batteryId: String,
        carManufacturerId: String,
        carModelId: String,
        carNo: String,
        productNo: String,
        productionDate: String,
        romId: String,
        carManufacturerName:String,
        carModelName: String
    )
    {
        Log.d("BatteryInfoVM", """
        Saving to preferences:
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
            carModelName=carModelName
        )

        val savedInfo = preferencesHelper.getBatteryInfo()
        Log.d("BatteryInfoVM", "Saved info verification: $savedInfo")

    }

    fun saveBatteryInfo(request: BatteryRequest,carManufacturerName: String,carModelName: String) {
        viewModelScope.launch {
            Log.d("BatteryInfoVM", "Saving battery info - Request: $request")
            Log.d("BatteryInfoVM", "Manufacturer Name: $carManufacturerName, Model Name: $carModelName")

            _loading.value = true
            val result = repository.uploadBatteryInfo(request)
            result.onSuccess { response ->
                Log.d("BatteryInfoVM", "Upload success - Response: $response")

                response.data?.let { batteryId ->
                    Log.d("BatteryInfoVM", "Received battery ID: $batteryId")

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
                    _navigateToQRScreen.value = true // 상태 업데이트
                } ?: Log.e("BatteryInfoVM", "Battery ID is null in response")

            }.onFailure { error ->
                Log.e("BatteryInfoVM", "Upload failed", error)
                _error.value = "저장 중 문제가 발생했습니다. 다시 시도해주세요. Error: ${error.message}"

            }.also {
                _loading.value = false
            }
        }
    }


    fun registerQRCode(qrCode: String, onSuccess: () -> Unit) {
        val batteryId = preferencesHelper.getBatteryInfo()["battery_id"] ?: return
        val savedQrCode = preferencesHelper.getBatteryInfo()["qrcode"]

        if (qrCode == savedQrCode) {
            // QR 코드가 동일하다면 바로 성공 처리
            onSuccess()
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val request = QRcodeRequest(qrCode)
            val result = repository.registerQRCode(batteryId, request)

            result.onSuccess {
                // QR 코드 저장
                preferencesHelper.saveQRCode(qrCode)
                _loading.value = false
                onSuccess()
            }.onFailure {
                _error.value = it.message
                _loading.value = false
            }
        }
    }
    fun uploadMultipleImages(
        context: Context,
        imageUris: List<Uri>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val batteryId = _batteryId.value
        if (batteryId.isNullOrEmpty()) {
            onError("Battery ID is not available.")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val multipartBodies = imageUris.map { uri ->
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                    val inputStream = contentResolver.openInputStream(uri)
                    val file = File(context.cacheDir, "image.${mimeType.substringAfter("/")}").apply {
                        outputStream().use { inputStream?.copyTo(it) }
                    }
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    // 서버가 요구하는 이름으로 지정
                    MultipartBody.Part.createFormData("multipartFiles", file.name, requestFile)
                }

                val result = repository.uploadImages(batteryId, multipartBodies)

                result.onSuccess {
                    onSuccess()
                }.onFailure { exception ->
                    onError(exception.message ?: "Failed to upload images")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An unexpected error occurred")
            } finally {
                _loading.value = false
            }
        }
    }





}
