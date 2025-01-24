package com.lodong.poen.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.apis.SignUpApis
import com.lodong.poen.viewmodel.FindIdViewModel

class FindIdViewModelFactory(
    private val signUpApis: SignUpApis
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FindIdViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FindIdViewModel(signUpApis) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}