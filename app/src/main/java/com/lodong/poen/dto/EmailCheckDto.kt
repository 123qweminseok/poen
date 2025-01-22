package com.lodong.poen.dto

import com.google.gson.annotations.SerializedName

data class EmailCheckDto(
    @SerializedName("now")
    val now: String,

    @SerializedName("expiresAt")
    val expiresAt: String
)