package com.lodong.poen.repository

import PreferencesHelper
import android.util.Log
import com.lodong.apis.BatteryInfoApis
import com.lodong.poen.dto.batteryinfo.BatteryRequest
import com.lodong.poen.dto.batteryinfo.CarModelInfo

import com.lodong.poen.dto.batteryinfo.ManufacturerInfos
import com.lodong.poen.dto.batteryinfo.QRcodeRequest

import com.lodong.utils.ApiResponse
import com.lodong.poen.network.RetrofitClient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Response

class BatteryInfoRepository(private val preferencesHelper: PreferencesHelper) {
    private val apiService: BatteryInfoApis







    init {
        apiService = RetrofitClient.getInstance(
            baseUrl = "https://beri-link.co.kr",
            preferencesHelper = preferencesHelper
        ).getApiService(BatteryInfoApis::class.java)
    }

    suspend fun getManufacturerInfos(): Result<ApiResponse<List<ManufacturerInfos>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getManufacturerInfos()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCarModelInfos(manufacturerId: String): Result<ApiResponse<List<CarModelInfo>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getModelInfos(manufacturerId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 배터리 정보 저장
    suspend fun uploadBatteryInfo(request: BatteryRequest): Result<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("BatteryInfoRepo", "Request body: $request")  // 요청 본문 확인

                val response = apiService.uploadBatteryInfo(request)
                Log.d("BatteryInfoRepo", "Response code: ${response.code()}")  // 응답 코드
                Log.d("BatteryInfoRepo", "Response headers: ${response.headers()}")  // 응답 헤더

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("BatteryInfoRepo", "Success response body: $it")  // 성공 응답 본문
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BatteryInfoRepo", "Error response code: ${response.code()}")  // 에러 코드
                    Log.e("BatteryInfoRepo", "Error body: $errorBody")  // 에러 본문
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()} - $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("BatteryInfoRepo", "Exception occurred", e)  // 예외 발생 시
                Result.failure(e)
            }
        }
    }



    suspend fun registerQRCode(batteryId: String, request: QRcodeRequest): Result<String> {
        return try {
            Log.d("BatteryInfoRepo", "QR Code Request - Battery ID: $batteryId, Request: $request")

            val response = apiService.registerQRCode(batteryId, request)
            Log.d("BatteryInfoRepo", "QR Code Response Code: ${response.code()}")
            Log.d("BatteryInfoRepo", "QR Code Response Headers: ${response.headers()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("BatteryInfoRepo", "QR Code Success Response: $responseBody")
                Result.success(responseBody?.data ?: "Unknown success response")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("BatteryInfoRepo", "QR Code Error Response Code: ${response.code()}")
                Log.e("BatteryInfoRepo", "QR Code Error Body: $errorBody")
                Result.failure(Exception("Error: ${response.code()} - ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("BatteryInfoRepo", "QR Code Exception", e)
            Result.failure(e)
        }
    }
    suspend fun uploadImages(batteryId: String, multipartBodies: List<MultipartBody.Part>): Result<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ApiResponse<String>> = apiService.uploadImages(batteryId, multipartBodies)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


}