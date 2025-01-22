package com.lodong.apis

import com.google.mlkit.common.sdkinternal.ModelInfo
import com.lodong.poen.dto.batteryinfo.BatteryRequest
import com.lodong.poen.dto.batteryinfo.CarModelInfo
import com.lodong.poen.dto.batteryinfo.ManufacturerInfos
import com.lodong.poen.dto.batteryinfo.QRcodeRequest
import com.lodong.poen.dto.signup.LoginDto
import com.lodong.poen.dto.signup.LoginRequest
import com.lodong.utils.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.UUID

interface BatteryInfoApis {


    @GET("api/seller/batteries/manufacturers")
    suspend fun getManufacturerInfos(): Response<ApiResponse<List<ManufacturerInfos>>>

    @GET("api/buyer/batteries/manufacturers/{carManufacturerId}/models")
    suspend fun getModelInfos(
        @Path("carManufacturerId") manufacturerId: String // UUID를 경로 변수로 전달
    ): Response<ApiResponse<List<CarModelInfo>>>


    @POST("api/seller/batteries")
    suspend fun uploadBatteryInfo(
        @Body request: BatteryRequest
    ): Response<ApiResponse<String>>

    @PUT("api/seller/batteries/{batteryId}/qrcode")
    suspend fun registerQRCode(
        @Path("batteryId") batteryId: String, // UUID를 경로 변수로 전달
        @Body request: QRcodeRequest
    ): Response<ApiResponse<String>>

    @Multipart
    @POST("/api/seller/batteries/{batteryId}/images")
    suspend fun uploadImages(
        @Path("batteryId") batteryId: String,
        @Part files: List<MultipartBody.Part> // 이름 제거
    ): Response<ApiResponse<String>>




}