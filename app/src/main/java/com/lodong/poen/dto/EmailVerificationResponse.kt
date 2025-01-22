package com.lodong.poen.dto

data class EmailVerificationResponse(
    val now: String,
    val expiresAt: String
)
