package com.lodong.poen.ui.screens

import android.webkit.JavascriptInterface

data class AddressData(
    val zonecode: String,
    val address: String,
    val jibunAddress: String
)

class WebAppInterface(private val onAddressSelected: (String) -> Unit) {
    @JavascriptInterface
    fun onAddressSelected(data: String) {
        onAddressSelected(data)
    }
}