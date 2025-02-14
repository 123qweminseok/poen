package com.lodong.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApis {
    @GET("/api/member/notices")
    suspend fun getNotices(
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<NoticeResponse>


    // 상세 조회 API 추가
    @GET("/api/member/notices/{noticeId}")
    suspend fun getNoticeDetail(
        @Path("noticeId") noticeId: String
    ): Response<NoticeDetailResponse>





    data class NoticeResponse(
        val status: Int,
        val resultMsg: String,
        val divisionCode: String,
        val data: NoticeData
    )

    data class NoticeData(
        val content: List<NoticeItem>,
        val totalElements: Int,
        val currentPage: Int,
        val pageSize: Int
    )

    data class NoticeItem(
        val noticeId: String,
        val title: String,
        val regDate: String,
        val content: String
    )


    // 상세 조회용 Response 클래스 추가
        data class NoticeDetailResponse(
            val status: Int,
            val resultMsg: String,
            val divisionCode: String,
            val data: NoticeDetailData
        )

        data class NoticeDetailData(
            val title: String,
            val content: String,
            val regDate: String,
            val updateDate: String?
        )
    }



