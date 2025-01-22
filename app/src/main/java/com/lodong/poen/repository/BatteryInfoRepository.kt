package com.lodong.poen.repository

import PreferencesHelper
import android.content.Context
import android.util.Log
import com.lodong.apis.BatteryInfoApis
import com.lodong.poen.dto.batteryinfo.BatteryRequest
import com.lodong.poen.dto.batteryinfo.CarModelInfo

import com.lodong.poen.dto.batteryinfo.ManufacturerInfos
import com.lodong.poen.dto.batteryinfo.QRcodeRequest
import com.lodong.poen.dto.signup.LoginDto

import com.lodong.utils.ApiResponse
import com.lodong.utils.RetrofitClient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Response
import java.util.UUID

class BatteryInfoRepository(context: Context) {
    private val preferencesHelper = PreferencesHelper.getInstance(context)
    private val apiService: BatteryInfoApis

    init {
        val accessToken = preferencesHelper.getAccessToken()
        apiService = RetrofitClient.getInstance("https://beri-link.co.kr")
            .setJwtToken(accessToken)
            .getApiService(BatteryInfoApis::class.java)
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
                val response = apiService.uploadBatteryInfo(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Log.e("에러",response.message())
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("에러",e.message.toString())
                Result.failure(e)
            }
        }
    }

    suspend fun registerQRCode(batteryId: String, request: QRcodeRequest): Result<String> {
        return try {
            val response = apiService.registerQRCode(batteryId, request)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: "Unknown success response")
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
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