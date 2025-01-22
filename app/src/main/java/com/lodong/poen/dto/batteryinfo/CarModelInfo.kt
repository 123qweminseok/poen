package com.lodong.poen.dto.batteryinfo

import com.google.gson.annotations.SerializedName

data class CarModelInfo (
    @SerializedName("carModelId")
    val carModelId: String,

    @SerializedName("carModelName")
    val carModelName: String
)