package com.lodong.poen.SeverRequestResponse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodong.apis.BusinessValidationRequest
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
                    Log.d("SignUp", "Server Response: ${response.raw()}")
                    Log.d("SignUp", "Response Body: ${response.body()}")
                    Log.d("SignUp", "Response Code: ${response.code()}")

                    if (response.isSuccessful && response.body()?.status == 200) {
                        onSuccess()
                    } else {
                        onError(response.body()?.resultMsg ?: "회원가입 실패")
                    }
                } else {
                    // 판매자 회원가입은 추후 구현
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


    fun validateBusiness(
        businessNumber: String,
        businessRepresentativeName: String,
        businessOpenDate: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                // 유효성 검사: 개업일자 형식 맞춰서 적으라고
                if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(businessOpenDate.trim())) {
                    onError("개업일자 YYYY-MM-DD 형식으로 입력해야 합니다.")
                    return@launch
                }

                val request = BusinessValidationRequest(
                    businessNumber = businessNumber.trim(),
                    businessRepresentativeName = businessRepresentativeName.trim(),
                    businessOpenDate = businessOpenDate.trim()
                )
                println("요청 데이터: $request") // 요청 데이터 확인

                val response = api.validateBusiness(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    println("응답 데이터: $body") // 응답 데이터 확인
                    if (body != null && body.status == 200) {
                        onSuccess()
                    } else {
                        onError(body?.resultMsg ?: "사업자 확인 실패")
                    }
                } else {
                    println("칸을 다시 채워주세요 : ${response.code()}, ${response.errorBody()?.string()}") // 오류 로그
                    onError("입력해 주세요")
                }
            } catch (e: Exception) {
                println("네트워크 예외: ${e.message}") // 예외 로그
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
    fun sellerSignup(
        identifier: String,
        password: String,
        name: String,
        email: String,
        phoneNumber: String,
        emailCode: String,
        zipCode: String,
        defaultAddress: String,
        detailAddress: String,
        businessNumber: String,
        businessRepresentativeName: String,
        businessOpenDate: String,
        businessName: String,
        businessZipCode: String,
        businessDefaultAddress: String,
        businessDetailAddress: String,
        businessAccountBank: String,
        businessAccountNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = SellerSignupRequest(
                    identifier = identifier,
                    password = password,
                    email = email,
                    phoneNumber = phoneNumber,
                    name = name,
                    emailCode = emailCode,
                    zipCode = zipCode,
                    defaultAddress = defaultAddress,
                    detailAddress = detailAddress,
                    businessNumber = businessNumber,
                    businessRepresentativeName = businessRepresentativeName,
                    businessOpenDate = businessOpenDate,
                    businessName = businessName,
                    businessZipCode = businessZipCode,
                    businessDefaultAddress = businessDefaultAddress,
                    businessDetailAddress = businessDetailAddress,
                    businessAccountBank = businessAccountBank,
                    businessAccountNumber = businessAccountNumber
                )

                Log.d("SignUp", "=== Request Data ===")
                Log.d("SignUp", "Request: $request")

                val response = api.sellerSignup(request)

                Log.d("SignUp", "=== Response Info ===")
                Log.d("SignUp", "Response Code: ${response.code()}")
                Log.d("SignUp", "Headers: ${response.headers()}")
                Log.d("SignUp", "Raw Response: ${response.raw()}")

                if (response.isSuccessful) {
                    Log.d("SignUp", "Success Body: ${response.body()}")
                    if (response.body()?.status == 200) {
                        onSuccess()
                    } else {
                        val msg = response.body()?.resultMsg ?: "회원가입 실패"
                        Log.e("SignUp", "API Error: $msg")
                        onError(msg)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SignUp", "Error Body: $errorBody")
                    onError("회원가입 실패: $errorBody")
                }

            } catch (e: HttpException) {
                Log.e("SignUp", "HTTP Exception", e)
                onError("서버 오류: ${e.message}")
            } catch (e: IOException) {
                Log.e("SignUp", "IO Exception", e)
                onError("네트워크 오류: ${e.message}")
            } catch (e: Exception) {
                Log.e("SignUp", "General Exception", e)
                onError("오류 발생: ${e.message}")
            }
        }
    }

}

data class SellerSignupRequest(
    val identifier: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val emailCode: String,
    val zipCode: String,
    val defaultAddress: String,
    val detailAddress: String,
    val businessNumber: String,
    val businessRepresentativeName: String,
    val businessOpenDate: String,
    val businessName: String,
    val businessZipCode: String,
    val businessDefaultAddress: String,
    val businessDetailAddress: String,
    val businessAccountBank: String,
    val businessAccountNumber: String
)
//판매자 클래스임.