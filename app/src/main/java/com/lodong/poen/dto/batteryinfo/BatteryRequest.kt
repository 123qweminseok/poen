package com.lodong.poen.dto.batteryinfo

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class BatteryRequest (
    @SerializedName("carManufacturerId") val carManufacturerId: String,
    @SerializedName("carModelId") val carModelId: String,
    @SerializedName("carNo") val carNo: String,
    @SerializedName("productNo") val productNo: String,
    @SerializedName("productionDate") val productionDate: String,
    @SerializedName("romId") val romId: String
)