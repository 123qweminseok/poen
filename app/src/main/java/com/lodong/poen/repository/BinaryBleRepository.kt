package com.lodong.poen.repository

import com.lodong.poen.utils.HwToAppProtocol
import PreferencesHelper
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.lodong.apis.BlueToothApis
import com.lodong.poen.dto.batteryinfo.SensorDataDto
import com.lodong.poen.service.BluetoothForegroundService
import com.lodong.utils.ApiResponse
import com.lodong.utils.ApiResponseResult
import com.lodong.poen.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class BinaryBleRepository(private val context: Context) {
    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper.getInstance(context)
    }

    private var bluetoothService: BluetoothForegroundService? = null
    private var hwToAppProtocol: HwToAppProtocol? = null

    private val apiService: BlueToothApis by lazy {
        RetrofitClient.getInstance("https://beri-link.co.kr", preferencesHelper)
            .getApiService(BlueToothApis::class.java)
    }

    private val collectedData = mutableListOf<List<Int>>()

    suspend fun addRawBytes(bytes: List<Int>) {
        collectedData.add(bytes)
    }
    fun setBluetoothService(service: BluetoothForegroundService) {
        bluetoothService = service
        // 서비스가 설정될 때 HwToAppProtocol 생성
        hwToAppProtocol = HwToAppProtocol(service)
    }

    suspend fun getData(): ApiResponseResult<String> {
        return try {
            // HwToAppProtocol이 필요없다면 이 부분을 수정하거나
            // 다른 방식으로 데이터를 처리하도록 변경
            val response: Response<ApiResponse<String>> = apiService.getDataoneTime()

            if (response.isSuccessful) {

                val apiResponse = response.body()

                if (apiResponse != null && apiResponse.status == 200) {
                    val hexData = apiResponse.data ?: ""
                    val bytes = hexData.split(Regex("\\s+"))
                        .filter { it.isNotEmpty() }
                        .map { it.toInt(16).toByte() }
                        .toByteArray()

                    // hwToAppProtocol이 null이 아닐 때만 패킷 처리
                    hwToAppProtocol?.let {
                        it.analyzeData(mutableListOf(bytes))
                    }

                    ApiResponseResult.Success(apiResponse.data)
                } else {
                    ApiResponseResult.Error("오류 발생: ${apiResponse?.resultMsg ?: "알 수 없는 오류"}")
                }
            } else {
                ApiResponseResult.Error("API 호출 실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("ConsultRepository", "Error during getData: ${e.message}", e)
            ApiResponseResult.Error("API 호출 중 오류 발생: ${e.localizedMessage}")
        }
    }

// 실제로 데이터 날리는 부분 서버로.ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    suspend fun sendCollectedData(batteryId: String, dataToSend: List<SensorDataDto>): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (dataToSend.isNotEmpty()) {
                    val response = apiService.sendMeasurements(batteryId, dataToSend)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status == 200) {
                            Result.success(true)
                        } else {
                            val errorMessage = responseBody?.resultMsg ?: "Unknown error"
                            Log.e(TAG, "Error response body: $errorMessage")
                            Result.failure(Exception("Error: $errorMessage"))
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Error response code: ${response.code()}")
                        Log.e(TAG, "Error body: $errorBody")
                        Result.failure(Exception("API call failed: ${response.code()} - $errorBody"))
                    }
                } else {
                    Result.failure(Exception("No data to send"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during API call", e)
                Result.failure(e)
            }
        }
    }}