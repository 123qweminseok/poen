package com.lodong.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private const val BASE_URL = "https://beri-link.co.kr/"  // 실제 서버 URL로 변경 필요

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val memberApi: MemberApi by lazy {
        retrofit.create(MemberApi::class.java)
    }
}