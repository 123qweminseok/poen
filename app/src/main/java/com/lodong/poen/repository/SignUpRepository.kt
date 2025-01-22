package com.lodong.poen.repository

import com.lodong.apis.BlueToothApis
import com.lodong.apis.SignUpApis
import com.lodong.utils.RetrofitClient

class SignUpRepository {
    val apiService: SignUpApis = RetrofitClient.getInstance("https://beri-link.co.kr").getApiService(
        SignUpApis::class.java)


    fun getSignUpApis(): SignUpApis {
        return apiService  // 직접 apiService 반환
    }

}