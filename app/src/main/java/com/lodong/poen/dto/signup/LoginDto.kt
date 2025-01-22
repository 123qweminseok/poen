package com.lodong.poen.dto.signup

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("permissions")
    val permissions: List<String>
)