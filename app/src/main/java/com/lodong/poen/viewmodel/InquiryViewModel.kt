package com.lodong.poen.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.apis.InquiryRequest
import com.lodong.apis.SignUpApis
import com.lodong.poen.network.BASE_URL
import com.lodong.poen.network.RetrofitClient

class InquiryViewModel(
    private val signUpApis: SignUpApis
) : ViewModel() {

    suspend fun submitInquiry(title: String, content: String) {
        try {
            Log.d("InquiryViewModel", "문의 등록 시도")
            Log.d("InquiryViewModel", "Request - 제목: $title, 내용: $content")

            val request = InquiryRequest(title, content)
            val response = signUpApis.submitInquiry(request)

            Log.d("InquiryViewModel", "API URL: /api/member/qna/questions")
            Log.d("InquiryViewModel", "응답 코드: ${response.code()}")
            Log.d("InquiryViewModel", "전체 응답: ${response.raw()}")
            Log.d("InquiryViewModel", "응답 헤더: ${response.headers()}")
            Log.d("InquiryViewModel", "응답 바디: ${response.body()}")

            if (response.isSuccessful) {
                Log.d("InquiryViewModel", "문의 등록 성공")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("InquiryViewModel", "문의 등록 실패")
                Log.e("InquiryViewModel", "에러 응답: $errorBody")
                Log.e("InquiryViewModel", "에러 코드: ${response.code()}")
                throw Exception("문의 등록 실패 (${response.code()}): $errorBody")
            }
        } catch (e: Exception) {
            Log.e("InquiryViewModel", "네트워크 에러", e)
            Log.e("InquiryViewModel", "스택 트레이스: ${e.stackTraceToString()}")
            throw e
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val preferencesHelper = PreferencesHelper.getInstance(ApplicationContextProvider.context)
                val retrofit = RetrofitClient.getInstance(BASE_URL, preferencesHelper)
                val signUpApis = retrofit.getApiService(SignUpApis::class.java)
                return InquiryViewModel(signUpApis) as T
            }
        }
    }
}

object ApplicationContextProvider {
    lateinit var context: Context
}