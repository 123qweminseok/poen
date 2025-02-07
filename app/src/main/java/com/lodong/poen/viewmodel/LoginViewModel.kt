package com.lodong.poen.viewmodel

import PreferencesHelper
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.poen.dto.signup.LoginDto
import com.lodong.poen.dto.signup.LoginRequest
import com.lodong.poen.repository.SignUpRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signUpRepository: SignUpRepository,
    application: Application
) : AndroidViewModel(application) {

    private val preferencesHelper = PreferencesHelper.getInstance(application)

    // MutableState for managing UI state
    val loginState = mutableStateOf<LoginUiState>(LoginUiState.Idle)

    fun login(identifier: String, password: String, stayLoggedIn: Boolean) {
        viewModelScope.launch {
            loginState.value = LoginUiState.Loading
            try {
                val response = signUpRepository.apiService.login(LoginRequest(identifier, password))
                if (response.isSuccessful && response.body()?.status == 200) {
                    val data = response.body()?.data
                    if (data != null) {
                        // Save tokens to SharedPreferences
                        preferencesHelper.saveTokens(data.accessToken, data.refreshToken)

                        // Save "stayLoggedIn" preference
                        preferencesHelper.saveStayLoggedIn(stayLoggedIn)

                        loginState.value = LoginUiState.Success(data)
                    } else {
                        loginState.value = LoginUiState.Error("Login failed: No data received")
                    }
                } else {
                    loginState.value = LoginUiState.Error(response.body()?.resultMsg ?: "Unknown error")
                }
            } catch (e: Exception) {
                loginState.value = LoginUiState.Error(e.message ?: "Network error")
            }
        }
    }



    fun findIdentifier(name: String, email: String, onSuccess: (String, String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = signUpRepository.apiService.findIdentifier(mapOf("name" to name, "email" to email))
                if (response.isSuccessful && response.body()?.status == 0) {
                    val data = response.body()?.data
                    if (data != null) {
                        onSuccess(data.identifier, data.regDate)
                    } else {
                        onError("아이디 찾기 실패: 데이터가 없습니다.")
                    }
                } else {
                    onError(response.body()?.resultMsg ?: "알 수 없는 오류")
                }
            } catch (e: Exception) {
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    fun checkIdentifierDuplicate(
        identifier: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                println("중복확인 시작 - 아이디: $identifier")
                // identifier를 Map으로 전달
                val response = signUpRepository.apiService.checkIdentifier(mapOf("identifier" to identifier))
                println("API 응답: ${response.body()}")

                if (response.isSuccessful) {
                    println("API 호출 성공")
                    if (response.body()?.status == 200) {
                        println("중복확인 성공")
                        onSuccess()
                    } else {
                        println("API 응답 에러: ${response.body()?.resultMsg}")
                        onError(response.body()?.resultMsg ?: "중복된 아이디입니다")
                    }
                } else {
                    println("API 호출 실패: ${response.errorBody()?.string()}")
                    onError("중복된 아이디입니다")
                }
            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
                onError("아이디 중복 확인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }






    // Check if user is logged in and "stayLoggedIn" is enabled
// Check if user is logged in and "stayLoggedIn" is enabled
    fun checkIfLoggedIn(): Boolean {
        val accessToken = preferencesHelper.getAccessToken()
        return preferencesHelper.isStayLoggedIn() && !accessToken.isNullOrEmpty()
        //preferencesHelper.isStayLoggedIn()
        //목적: 사용자가 로그인 유지 옵션을 활성화했는지 확인합니다
        //사용자가 로그인하면 서버가 발급하는 인증 토큰입니다.이 토큰이 없으면 사용자는 인증된 상태가 아니라고 간주됩니다.
    }
    //
    // UI State Sealed Class inside ViewModel
    sealed class LoginUiState {
        object Idle : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val data: LoginDto?) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }
}
