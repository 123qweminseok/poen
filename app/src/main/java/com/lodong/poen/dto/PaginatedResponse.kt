package com.lodong.poen.dto

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    @SerializedName("content") val content: T,
    val totalElements: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)