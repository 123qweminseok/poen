package com.lodong.poen.ui.screens

import kotlinx.serialization.Serializable

@Serializable
data class ZipAddressData(
    val zonecode: String,
    val address: String,
    val extraAddress: String
)
