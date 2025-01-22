package com.lodong.poen.SeverRequestResponse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.apis.SignUpApis
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// API 요청에 맞는 데이터 클래스 정의
data class BuyerSignupRequest(
    val identifier: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val emailCode: String,
    val zipCode: String,
    val defaultAddress: String,
    val detailAddress: String
)

class SignUpViewModel(private val api: SignUpApis) : ViewModel() {

    fun register(
        identifier: String,
        password: String,
        name: String,
        email: String,
        phoneNumber: String,
        emailCode: String,
        zipCode: String,
        defaultAddress: String,
        detailAddress: String,
        isSeller: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!isSeller) {
                    // 구매자 회원가입
                    val request = BuyerSignupRequest(
                        identifier = identifier,
                        password = password,
                        email = email,
                        phoneNumber = phoneNumber,
                        name = name,
                        emailCode = emailCode,
                        zipCode = zipCode,
                        defaultAddress = defaultAddress,
                        detailAddress = detailAddress
                    )

                    val response = api.buyerSignup(request)
                    if (response.isSuccessful && response.body()?.status == 0) {
                        onSuccess()
                    } else {
                        onError(response.body()?.resultMsg ?: "회원가입 실패")
                    }
                } else {
                    // 판매자 회원가입은 추후 구현
                    api.sellerSignup()
                }
            } catch (e: HttpException) {
                onError("서버 오류: ${e.message}")
            } catch (e: IOException) {
                onError("네트워크 오류: ${e.message}")
            }
        }
    }



    fun sendEmailVerificationCode(
        email: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.sendVerificationCode(mapOf("email" to email))
                if (response.isSuccessful && response.body()?.status == 200) {
                    // EmailVerificationResponse 객체에서 직접 expiresAt 가져오기
                    val expiresAt = response.body()?.data?.expiresAt
                    onSuccess(expiresAt ?: "")
                } else {
                    onError(response.body()?.resultMsg ?: "인증 코드 발송 실패")
                }
            } catch (e: Exception) {
                onError(e.message ?: "네트워크 오류")
            }
        }
    }

    // 이메일 인증 코드 확인
    fun verifyEmailCode(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = mapOf(
                    "email" to email,
                    "code" to code
                )
                val response = api.checkEmailWithCode(request)  // request 전달
                if (response.isSuccessful && response.body()?.status == 200) {  // status 200으로 수정
                    onSuccess()
                } else {
                    onError(response.body()?.resultMsg ?: "인증 코드 확인 실패")
                }
            } catch (e: Exception) {
                onError(e.message ?: "네트워크 오류")
            }
        }
    }








}