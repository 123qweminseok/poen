package com.lodong.poen.ui.screens

import kotlinx.serialization.Serializable
//이거 필요없음. 데이터 가지고 올라 했는데 안됨. ;; 왜 일단 대기임  연동은 DaumPostcodDialog로 되어있음.
@Serializable
data class ZipAddressData(
    val zonecode: String,
    val address: String
)
