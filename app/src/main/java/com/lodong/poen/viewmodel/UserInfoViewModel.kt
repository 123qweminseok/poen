package com.lodong.poen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.apis.MemberApi
import com.lodong.apis.SignUpApis
import com.lodong.poen.dto.signup.MemberData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(private val api: MemberApi,    private val token: String // token 추가
) : ViewModel() {
    private val _userInfoState = MutableStateFlow<UiState>(UiState.Idle)
    val userInfoState: StateFlow<UiState> = _userInfoState

    init {
        getUserInfo()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfoState.value = UiState.Loading
            try {
                Log.d("UserInfo", "API 호출 시작")
                val response = api.getMemberInfo()  // 토큰 파라미터 제거
                Log.d("UserInfo", "API 응답: ${response.body()}")

                if (response.isSuccessful && response.body()?.status == 0) {
                    Log.d("UserInfo", "데이터 로드 성공: ${response.body()?.data}")
                    _userInfoState.value = UiState.Success(response.body()?.data)
                } else {
                    Log.e("UserInfo", "API 에러: ${response.body()?.resultMsg}")
                    _userInfoState.value = UiState.Error(response.body()?.resultMsg ?: "데이터 로드 실패")
                }
            } catch (e: Exception) {
                Log.e("UserInfo", "네트워크 오류", e)
                _userInfoState.value = UiState.Error("네트워크 오류")
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val data: MemberData?) : UiState()
        data class Error(val message: String) : UiState()
    }
}