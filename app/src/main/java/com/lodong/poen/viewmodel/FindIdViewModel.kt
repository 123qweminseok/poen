package com.lodong.poen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.apis.SignUpApis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindIdViewModel(private val signUpApis: SignUpApis) : ViewModel() {

    private val _findIdState = MutableStateFlow<UiState>(UiState.Idle)
    val findIdState: StateFlow<UiState> = _findIdState


        //메일로 쏴주기ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    private val _emailSendState = MutableStateFlow<EmailSendState>(EmailSendState.Idle)
    val emailSendState: StateFlow<EmailSendState> = _emailSendState
//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    fun findId(name: String, email: String) {
        viewModelScope.launch {
            _findIdState.value = UiState.Loading
            try {
                Log.d("FindIdViewModel", "Sending request with name: $name, email: $email")
                val response = signUpApis.findIdentifier(
                    mapOf(
                        "name" to name,
                        "email" to email
                    )
                )

                Log.d("FindIdViewModel", "Response received: ${response.body()}")
                if (response.isSuccessful && response.body()?.status == 200) {  // 여기를 status == 200으로 수정
                    val data = response.body()?.data
                    if (data != null) {
                        Log.d("FindIdViewModel", "Success with identifier: ${data.identifier}")
                        _findIdState.value = UiState.Success(
                            identifier = data.identifier,
                            regDate = data.regDate
                        )
                    } else {
                        Log.e("FindIdViewModel", "No data found")
                        _findIdState.value = UiState.Error("데이터를 찾을 수 없습니다")
                    }
                } else {
                    Log.e("FindIdViewModel", "Error: ${response.body()?.resultMsg}")
                    _findIdState.value = UiState.Error(
                        response.body()?.resultMsg ?: "알 수 없는 오류가 발생했습니다"
                    )
                }
            } catch (e: Exception) {
                Log.e("FindIdViewModel", "Exception occurred", e)
                _findIdState.value = UiState.Error("네트워크 오류가 발생했습니다")
            }
        }
    }

    fun sendIdToEmail(name: String, email: String) {
        viewModelScope.launch {
            try {
                _emailSendState.value = EmailSendState.Loading

                val requestData = mapOf(
                    "name" to name,
                    "email" to email
                )

                // 요청 데이터 로깅
                Log.d("FindIdViewModel", "Sending request with data: $requestData")
                Log.d("FindIdViewModel", "Name: $name")
                Log.d("FindIdViewModel", "Email: $email")

                val response = signUpApis.sendIdWithEmail(requestData)

                Log.d("FindIdViewModel", "Raw Response: ${response.raw()}")
                Log.d("FindIdViewModel", "Response Code: ${response.code()}")
                Log.d("FindIdViewModel", "Response Headers: ${response.headers()}")

                if (response.isSuccessful && response.body()?.status == 200) {
                    Log.d("FindIdViewModel", "Success Response Body: ${response.body()}")
                    _emailSendState.value = EmailSendState.Success
                } else {
                    Log.e("FindIdViewModel", "Error Response Code: ${response.code()}")
                    Log.e("FindIdViewModel", "Error Response Body: ${response.body()}")
                    Log.e("FindIdViewModel", "Error Body: ${response.errorBody()?.string()}")

                    _emailSendState.value = EmailSendState.Error(
                        response.body()?.resultMsg ?: "이메일 전송 실패 (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                Log.e("FindIdViewModel", "Exception occurred", e)
                Log.e("FindIdViewModel", "Exception message: ${e.message}")
                Log.e("FindIdViewModel", "Stack trace: ${e.stackTraceToString()}")

                _emailSendState.value = EmailSendState.Error(
                    e.message ?: "네트워크 오류"
                )
            }
        }
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ쏴주기
    sealed class EmailSendState {
        object Idle : EmailSendState()
        object Loading : EmailSendState()
        object Success : EmailSendState()
        data class Error(val message: String) : EmailSendState()
    }
    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ쏴주기



    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val identifier: String, val regDate: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}
