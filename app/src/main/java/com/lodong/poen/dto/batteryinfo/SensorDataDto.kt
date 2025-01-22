package com.lodong.poen.dto.batteryinfo

import com.google.gson.annotations.SerializedName

data class SensorDataDto (
    @SerializedName("data")
    val data: List<Int>
)