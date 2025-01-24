package com.lodong.poen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.apis.SignUpApis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindPasswordViewModel(private val signUpApis: SignUpApis) : ViewModel() {

    private val _findPasswordState = MutableStateFlow<UiState>(UiState.Idle)
    val findPasswordState: StateFlow<UiState> = _findPasswordState

    fun findPassword(name: String, email: String, identifier: String) {
        viewModelScope.launch {
            _findPasswordState.value = UiState.Loading
            try {
                Log.d("FindPasswordViewModel", "Sending request with name: $name, email: $email, identifier: $identifier")
                val response = signUpApis.findPassword(
                    mapOf(
                        "name" to name,
                        "email" to email,
                        "identifier" to identifier
                    )
                )

                Log.d("FindPasswordViewModel", "Response received: ${response.body()}")
                if (response.isSuccessful && response.body()?.status == 200) {
                    _findPasswordState.value = UiState.Success(identifier)
                } else {
                    _findPasswordState.value = UiState.Error(
                        response.body()?.resultMsg ?: "알 수 없는 오류가 발생했습니다"
                    )
                }
            } catch (e: Exception) {
                Log.e("FindPasswordViewModel", "Exception occurred", e)
                _findPasswordState.value = UiState.Error("네트워크 오류가 발생했습니다")
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val identifier: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}