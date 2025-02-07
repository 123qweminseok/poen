package com.lodong.apis

import com.lodong.poen.SeverRequestResponse.BuyerSignupRequest
import com.lodong.poen.SeverRequestResponse.SellerSignupRequest
import com.lodong.poen.dto.EmailCheckDto
import com.lodong.poen.dto.EmailVerificationResponse
import com.lodong.poen.dto.signup.IdentifierResponse
import com.lodong.poen.dto.signup.LoginDto
import com.lodong.poen.dto.signup.LoginRequest
import com.lodong.poen.dto.signup.MemberInfoResponse
import com.lodong.utils.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
    suspend fun sellerSignup(@Body request: SellerSignupRequest): Response<ApiResponse<Unit>>



    // 비밀번호 찾기
    @POST("/api/auth/password/find")
    suspend fun findPassword(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    // 로그인
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginDto>>

    @POST("/api/auth/identifier/send")
    suspend fun sendIdWithEmail(@Body request: Map<String, String>): Response<ApiResponse<Unit>>
    // 아이디 찾기
    @POST("/api/auth/identifier/find")
    suspend fun findIdentifier(@Body request: Map<String, String>): Response<ApiResponse<IdentifierResponse>>

    // 아이디 중복 확인
    @POST("/api/auth/identifier/check")
    suspend fun checkIdentifier(@Body request: Map<String, String>): Response<ApiResponse<Unit>>
    // 구매자 회원가입
    @POST("/api/auth/buyer/signup")
    suspend fun buyerSignup(@Body request: BuyerSignupRequest): Response<ApiResponse<Unit>>

    // 관리자 회원가입
    @POST("/api/auth/admin/signup")
    suspend fun adminSignup(): Response<ApiResponse<Unit>>

    // 사용자 로그아웃
    @POST("/api/member/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>



    @POST("/api/auth/seller/business/validate")
    suspend fun validateBusiness(
        @Body request: BusinessValidationRequest
    ): Response<ApiResponse<Any>>

    @POST("/api/member/qna/questions")
    suspend fun submitInquiry(@Body request: InquiryRequest): Response<ApiResponse<Unit>>


}

data class InquiryRequest(
    val title: String,
    val content: String
)


data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val loginType: String
)
data class BusinessValidationRequest(
    val businessNumber: String,
    val businessRepresentativeName: String,
    val businessOpenDate: String
)