package com.lodong.poen.repository

import PreferencesHelper
import com.lodong.apis.MemberApi
import com.lodong.apis.SignUpApis
import com.lodong.poen.network.RetrofitClient

class SignUpRepository(private val preferencesHelper: PreferencesHelper) {
    private val retrofit = RetrofitClient("https://beri-link.co.kr", preferencesHelper)
    val apiService: SignUpApis = retrofit.getApiService(SignUpApis::class.java)

    fun getSignUpApis(): SignUpApis = apiService
    fun createMemberApi(): MemberApi = retrofit.getApiService(MemberApi::class.java)
}