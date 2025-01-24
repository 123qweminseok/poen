package com.lodong.apis

import com.lodong.poen.dto.signup.MemberInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT

interface MemberApi {
    @GET("/api/member/accounts")
    suspend fun getMemberInfo(): Response<MemberInfoResponse>  // Header 파라미터 제거

    @PUT("/api/member/accounts/password")
    suspend fun changePassword(
        @Body request: PasswordChangeRequest
    ): Response<BaseResponse>

    @PUT("/api/buyer/accounts")
    suspend fun updateBuyerInfo(
        @Body request: BuyerUpdateRequest
    ): Response<BaseResponse>


    @HTTP(method = "DELETE", path = "/api/member/accounts", hasBody = true)
    suspend fun deleteAccount(
        @Body request: DeleteAccountRequest
    ): Response<BaseResponse>

    data class PasswordChangeRequest(
        val beforePassword: String,
        val newPassword: String
    )

    data class BaseResponse(
        val status: Int,
        val resultMsg: String,
        val divisionCode: String?,
        val data: Any?
    )


    data class BuyerUpdateRequest(
        val phoneNumber: String,
        val zipCode: String,
        val defaultAddress: String,
        val detailAddress: String
    )

    data class DeleteAccountRequest(
        val password: String,
        val reason: String
    )

    data class DeleteResponse(
        val status: Int,
        val resultMsg: String
    )
}
