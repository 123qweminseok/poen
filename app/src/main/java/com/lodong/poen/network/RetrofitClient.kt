package com.lodong.poen.network

import PreferencesHelper
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
const val BASE_URL = "https://beri-link.co.kr"

class RetrofitClient(baseUrl: String, private val preferencesHelper: PreferencesHelper) {
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            val token = preferencesHelper.getAccessToken()
            Log.d("RetrofitClient", "Token from preferences: $token")

            token?.let {
                val authHeader = "Bearer $it"
                Log.d("RetrofitClient", "Adding auth header: $authHeader")
                addHeader("Authorization", "Bearer $it")
            } ?: Log.e("RetrofitClient", "Token is null!")
        }.build()

        Log.d("RetrofitClient", "Final request: ${request.url}")
        Log.d("RetrofitClient", "Final headers: ${request.headers}")

        val response = chain.proceed(request)
        Log.d("RetrofitClient", "Response code: ${response.code}")

        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> getApiService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    companion object {
        @Volatile private var instance: RetrofitClient? = null

        fun getInstance(baseUrl: String, preferencesHelper: PreferencesHelper): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient(baseUrl, preferencesHelper).also { instance = it }
            }
        }
    }
}