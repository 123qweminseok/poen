package com.lodong.poen.SeverRequestResponse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.apis.SignUpApis

class SignUpViewModelFactory(private val api: SignUpApis) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}