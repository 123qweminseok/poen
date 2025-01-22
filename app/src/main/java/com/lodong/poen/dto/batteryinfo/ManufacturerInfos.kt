package com.lodong.poen.dto.batteryinfo

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class ManufacturerInfos (
    @SerializedName("carManufacturerId")
    val carManufacturerId: String,

    @SerializedName("carManufacturerName")
    val carManufacturerName: String
)