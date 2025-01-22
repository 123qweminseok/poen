package com.lodong.poen.dto.batteryinfo

import com.google.gson.annotations.SerializedName

data class QRcodeRequest(

    @SerializedName("qrcode")
    val qrcode: String,
)