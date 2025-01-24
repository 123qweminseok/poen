package com.lodong.poen.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.apis.MemberApi
import com.lodong.poen.viewmodel.UserInfoViewModel

// UserInfoViewModelFactory.kt
class UserInfoViewModelFactory(private val api: MemberApi,
                               private val token: String // token 추가
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserInfoViewModel(api, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}