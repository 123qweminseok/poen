package com.lodong.poen.dto.signup

data class MemberInfoResponse(
    val status: Int,
    val resultMsg: String,
    val divisionCode: String,
    val data: MemberData
)

data class MemberData(
    val permissions: List<String>,
    val identifier: String,
    val name: String,
    val address: Address,
    val email: String,
    val phoneNumber: String,
    val businessInfo: BusinessInfo?
)

data class Address(
    val zipCode: String,
    val defaultAddress: String,
    val detailAddress: String,
    val addressString: String
)

data class BusinessInfo(
    val businessNumber: String,
    val businessRepresentativeName: String,
    val businessOpenDate: String,
    val businessName: String,
    val businessAddress: Address,
    val businessAccount: BusinessAccount
)

data class BusinessAccount(
    val accountBank: String,
    val accountNumber: String
)