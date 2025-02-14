package com.lodong.apis

import com.lodong.poen.dto.signup.MemberInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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


    // 판매자 정보 업데이트를 위한 API 추가
    @PUT("/api/seller/accounts")
    suspend fun updateSellerInfo(@Body request: SellerUpdateRequest): Response<BaseResponse>


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


    // 페이지네이션 데이터를 담는 클래스
    data class PageableContent<T>(
        val content: List<T>,
        val totalElements: Int,
        val totalPages: Int,
        val currentPage: Int,
        val pageSize: Int
    )

    // API 응답을 위한 래퍼 클래스
    data class InquiriesResponse(
        val status: Int,
        val resultMsg: String,
        val divisionCode: String,
        val data: PageableContent<InquiryResponse>
    )

    // 기존 Request/Response 클래스는 유지
    data class InquiryRequest(
        val title: String,
        val content: String
    )

    data class InquiryResponse(
        val questionId: String,
        val title: String,
        val content: String,
        val regDate: String?
    )

    @POST("/api/member/qna/questions")
    suspend fun createInquiry(
        @Body request: InquiryRequest
    ): Response<BaseResponse>

    @GET("/api/member/qna/questions")
    suspend fun getInquiries(
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<InquiriesResponse>

    // MemberApi 인터페이스에 추가
    @PUT("/api/member/qna/questions/{questionId}")
    suspend fun updateInquiry(
        @Path("questionId") questionId: String,
        @Body request: InquiryRequest
    ): Response<BaseResponse>

    // MemberApi 인터페이스 내에 추가
    @DELETE("/api/member/qna/questions/{questionId}")
    suspend fun deleteInquiry(
        @Path("questionId") questionId: String
    ): Response<BaseResponse>



    // SellerUpdateRequest 데이터 클래스 추가
    data class SellerUpdateRequest(
        val phoneNumber: String,
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











    //상세 로깅 추가

    data class InquiryDetailResponse(
        val status: Int,
        val resultMsg: String,
        val divisionCode: String,
        val data: InquiryDetail
    )

    data class InquiryDetail(
        val questionId: String,
        val memberInfo: MemberInfo,
        val title: String,
        val content: String,
        val regDate: String,
        val updateDate: String,
        val answers: List<Answer>?
    )

    data class MemberInfo(
        val memberId: String,
        val name: String,
        val email: String,
        val permission: Map<String, String>
    )

    data class Answer(
        val answerId: String,
        val content: String,
        val regdate: String,
        val updateDate: String
    )

    // 문의사항 상세 조회 API
    @GET("/api/member/qna/questions/{questionId}")
    suspend fun getInquiryDetail(
        @Path("questionId") questionId: String
    ): Response<InquiryDetailResponse>




}
