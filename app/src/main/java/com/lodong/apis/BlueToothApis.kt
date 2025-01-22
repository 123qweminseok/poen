package com.lodong.apis

import com.lodong.poen.dto.batteryinfo.SensorDataDto
import com.lodong.utils.ApiResponse
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface BlueToothApis {

    @GET("api/seller/batteries/data?keyword=1shot")
    suspend fun getDataoneTime(): Response<ApiResponse<String>>


    @GET("api/seller/batteries/data?keyword=continue")
    suspend fun getContinueData(): Response<ApiResponse<String>>

    @POST("api/seller/batteries/{batteryId}/measurements")
    suspend fun sendMeasurements(
        @Path("batteryId") batteryId: String,
        @Body body: List<SensorDataDto>
    ): Response<ApiResponse<String>>

}