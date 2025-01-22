package com.lodong.apis

import com.lodong.poen.SeverRequestResponse.BuyerSignupRequest
import com.lodong.poen.dto.EmailCheckDto
import com.lodong.poen.dto.EmailVerificationResponse
import com.lodong.poen.dto.signup.LoginDto
import com.lodong.poen.dto.signup.LoginRequest
import com.lodong.utils.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpApis {

    // 이메일 인증 코드 발송
    @POST("/api/auth/verification-code/send")
    suspend fun sendVerificationCode(@Body request: Map<String, String>): Response<ApiResponse<EmailVerificationResponse>>

    // 이메일 인증 코드 확인
    @POST("/api/auth/verification-code/validate")
    suspend fun checkEmailWithCode(@Body request: Map<String, String>): Response<ApiResponse<String>>

    // 판매자 회원가입
    @POST("/api/auth/seller/signup")
    suspend fun sellerSignup(): Response<ApiResponse<Unit>>

    // 사업자 등록 번호 확인
    @POST("/api/auth/seller/business/validate")
    suspend fun validateBusinessNumber(): Response<ApiResponse<Unit>>

    // 비밀번호 찾기
    @POST("/api/auth/password/find")
    suspend fun findPassword(): Response<ApiResponse<Unit>>

    // 로그인
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginDto>>

    // 아이디 이메일로 전송
    @POST("/api/auth/identifier/send")
    suspend fun sendIdWithEmail(): Response<ApiResponse<Unit>>

    // 아이디 찾기
    @POST("/api/auth/identifier/find")
    suspend fun findIdentifier(): Response<ApiResponse<Unit>>

    // 아이디 중복 확인
    @POST("/api/auth/identifier/check")
    suspend fun checkIdentifier(): Response<ApiResponse<Unit>>

    // 구매자 회원가입
    @POST("/api/auth/buyer/signup")
    suspend fun buyerSignup(@Body request: BuyerSignupRequest): Response<ApiResponse<Unit>>

    // 관리자 회원가입
    @POST("/api/auth/admin/signup")
    suspend fun adminSignup(): Response<ApiResponse<Unit>>

    // 사용자 로그아웃
    @POST("/api/member/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>


}