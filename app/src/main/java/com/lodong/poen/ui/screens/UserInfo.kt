package com.lodong.poen.ui.screens

import PreferencesHelper
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.lodong.apis.MemberApi
import com.lodong.poen.ui.CustomBrownButton
import com.lodong.poen.R
import com.lodong.poen.ui.InfoInputField
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.navigation.Routes
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter
import com.lodong.poen.viewmodel.UserInfoViewModel
import kotlinx.coroutines.launch
import com.lodong.apis.MemberApi.SellerUpdateRequest
import com.lodong.apis.SignUpApis
import com.lodong.poen.SeverRequestResponse.SignUpViewModel
import com.lodong.poen.SeverRequestResponse.SignUpViewModelFactory
import com.lodong.poen.ui.CustomBrownButton2
import com.lodong.poen.viewmodel.LoginViewModel
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive



private fun formatBusinessDate(input: String): String {
    // 하이픈 제거 및 숫자만 추출
    val digits = input.replace("-", "").filter { it.isDigit() }

    return if (digits.length >= 8) {
        val year = digits.substring(0, 4)
        val month = digits.substring(4, 6)
        val day = digits.substring(6, 8)
        "$year-$month-$day"
    } else {
        // 8자리가 안되면 그대로 원본 숫자 반환 (입력 도중에는 변경하지 않음)
        digits
    }
}






















@Composable
fun UserInfoEditScreen(
    isSeller: Boolean,
    api: MemberApi,
    viewModel: UserInfoViewModel,  // 추가
    preferencesHelper: PreferencesHelper,  // 추가
    navController: NavHostController, // navController 추가

    onBackButtonPressed: () -> Unit,


    api2: SignUpApis,
    loginViewModel: LoginViewModel,// 추가된 파라미터


    signupViewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(api2)
    )


) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp

    )
    val labelSize = 96.dp
    val inputSize = 168.dp
    val inputHeight = 40.dp
    val isSellerMode = remember { mutableStateOf(isSeller) } // 초기값을 설정

    val identifier = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }


    val phoneNumber = remember { mutableStateOf("") }
    val zipCode = remember { mutableStateOf("") }
    val defaultAddress = remember { mutableStateOf("") }
    val detailAddress = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") } // 에러 메시지 상태 변수 추가

        //ㅡㅡㅡㅡㅡㅡ회원탈퇴ㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    var showDeleteDialog by remember { mutableStateOf(false) }
    var passwordForDelete = remember { mutableStateOf("") }
    var deleteReason = remember { mutableStateOf("") }
    var showZipDialog by remember { mutableStateOf(false) }


    val dialogMessage = remember { mutableStateOf("") } // dialogMessage 상태 선언
    val showDialog = remember { mutableStateOf(false) }  // showDialog 상태 선언


// 각각의 다이얼로그 상태 분리
    var showPersonalZipDialog = remember { mutableStateOf(false) } // 개인 주소 검색 다이얼로그
    var showBusinessZipDialog = remember { mutableStateOf(false) } // 사업장 주소 검색 다이얼로그


    val isBusinessValidated = remember { mutableStateOf(false) } //칸 입력 x

// ㅡㅡㅡㅡ판매자 정보를 위한 상태 변수들 추가

    val businessNumber = remember { mutableStateOf("") }
    val businessRepresentativeName = remember { mutableStateOf("") }
    val businessOpenDate = remember { mutableStateOf("") }
    val businessName = remember { mutableStateOf("") }
    val businessZipCode = remember { mutableStateOf("") }
    val businessDefaultAddress = remember { mutableStateOf("") }
    val businessDetailAddress = remember { mutableStateOf("") }
    val businessAccountBank = remember { mutableStateOf("") }
    val businessAccountNumber = remember { mutableStateOf("") }




// 은행 목록
    val bankList = listOf(
        "한국씨티은행", "우리은행", "전북은행", "NH농협은행", "하나은행",
        "카카오뱅크", "KB국민은행", "IBK기업은행", "경남은행", "대구은행",
        "부산은행", "케이뱅크", "수협은행", "신한은행", "KDB산업은행",
        "제주은행", "광주은행", "SC제일은행"
    )







    val token = preferencesHelper.getAccessToken()
    Log.d("Authorization", "Retrieved Token: $token")
    if (token.isNullOrEmpty()) {
        Log.e("Authorization", "토큰이 없습니다.")
    } else if (!token.startsWith("Bearer ")) {
        Log.e("Authorization", "Bearer 형식이 아닙니다. 토큰: $token")
    } else {
        Log.d("Authorization", "토큰이 올바른 형식으로 설정됨: $token")
    }


    LaunchedEffect(Unit) {
        try {
            val token = preferencesHelper.getAccessToken() ?: ""
            Log.d("UserInfo", "Raw Token (from preferences): $token")

            // Bearer 토큰 형식 확인 및 변환
            val bearerToken = if (!token.startsWith("Bearer ")) "Bearer $token" else token
            Log.d("UserInfo", "Final Token (with Bearer): $bearerToken")

            // API 요청 URL 및 헤더 출력
            Log.d("UserInfo", "Request Headers: Authorization: $bearerToken")

            val response = api.getMemberInfo()  // 토큰 파라미터 제거
            // 응답 상태 확인
            if (response.isSuccessful && response.body()?.status == 200) {
                response.body()?.data?.let { data ->
                    identifier.value = data.identifier
                    userName.value = data.name
                    userEmail.value = data.email
                }
            } else {
                Log.d("UserInfo", "API Success: ${response.body()}")
            }
        } catch (e: Exception) {
            Log.e("UserInfo", "API Exception", e)
        }
    }





    fun validateInputs(
        isSeller: Boolean,
        userName: String,
        phoneNumber: String,
        zipCode: String,
        defaultAddress: String,
        detailAddress: String,
        businessNumber: String?,
        businessName: String?,
        businessZipCode: String?,
        businessAddress: String?,
        businessDetailAddress: String?
    ): Boolean {
        if (!isSeller) {
            if (userName.isEmpty()) {
                dialogMessage.value = "이름을 입력해주세요."
                showDialog.value = true
                return false
            }
            if (phoneNumber.isEmpty() || !phoneNumber.matches(Regex("^01[0-9]{8,9}$"))) {
                dialogMessage.value = "올바른 전화번호를 입력해주세요."
                showDialog.value = true
                return false
            }
            if (zipCode.isEmpty() || zipCode.length != 5) {
                dialogMessage.value = "올바른 우편번호를 입력해주세요."
                showDialog.value = true
                return false
            }
            if (defaultAddress.isEmpty()) {
                dialogMessage.value = "기본 주소를 입력해주세요."
                showDialog.value = true
                return false
            }
        } else { // 판매자일 때 유효성 검사
            if (businessNumber.isNullOrEmpty() || businessNumber.length != 10) {
                dialogMessage.value = "올바른 사업자 등록번호를 입력해주세요."
                showDialog.value = true
                return false
            }
            if (businessName.isNullOrEmpty()) {
                dialogMessage.value = "사업장명을 입력해주세요."
                showDialog.value = true
                return false
            }
            if (businessZipCode.isNullOrEmpty() || businessZipCode.length != 5) {
                dialogMessage.value = "올바른 사업장 우편번호를 입력해주세요."
                showDialog.value = true
                return false
            }
            if (businessAddress.isNullOrEmpty()) {
                dialogMessage.value = "사업장 주소를 입력해주세요."
                showDialog.value = true
                return false
            }

            if (!isBusinessValidated.value) {
                dialogMessage.value = "사업자 인증을 먼저 완료해주세요."
                showDialog.value = true
                return false
            }


        }
        return true
    }







    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "회원정보 수정", onBackButtonPressed = onBackButtonPressed)
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )




        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "구매자 / 판매자 전환", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            androidx.compose.material3.Switch(
                checked = isSellerMode.value,
                onCheckedChange = { isSellerMode.value = it },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White,  // 선택 시 썸(thumb)을 하얀색
                    uncheckedThumbColor = Color.LightGray,  // 비선택 시 썸을 연한 회색
                    checkedTrackColor = Color(0xFF4CAF50),  // 선택 시 트랙을 녹색으로
                    uncheckedTrackColor = Color(0xFFBDBDBD)  // 비선택 시 트랙을 연한 회색으로
                )
            )
        }



        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "아이디", style = labelTextStyle)
                Text(text = identifier.value, color = Color(0x80262626))
            }




            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "비밀번호 변경", style = labelTextStyle)
                CustomBrownButton(
                    onClick = { navController.navigate(Routes.PasswordChangeScreen.route) },
                    text = "비밀번호 변경하기"
                )
            }


            val name = remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "이름", style = labelTextStyle)
                Text(text = userName.value, color = Color(0x80262626))
            }




            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "이메일", style = labelTextStyle)
                Text(text = userEmail.value, color = Color(0x80262626))
            }















            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "전화번호", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it } // 상태를 업데이트
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(modifier = Modifier.width(labelSize), text = "우편번호", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = zipCode.value,
                    onValueChange = { zipCode.value = it } // 상태를 업데이트
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        showPersonalZipDialog.value = true // 개인 주소 검색 다이얼로그 열기
                    },
                    text = "우편번호 검색"
                )
// 기존 dialog 호출 부분 수정



                if (showPersonalZipDialog.value) {
                    DaumPostcodeDialog(
                        onAddressSelected = { addressJson ->
                            println("다음 우편번호 서비스에서 반환된 JSON: $addressJson")
                            try {
                                val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(addressJson)

                                // 우편번호 파싱 및 설정
                                val zonecode = jsonElement.jsonObject["zonecode"]?.jsonPrimitive?.content ?: ""
                                zipCode.value = zonecode

                                // 기본 주소 파싱 및 설정
                                val address = jsonElement.jsonObject["address"]?.jsonPrimitive?.content ?: ""
                                defaultAddress.value = address

                                // 상세 주소(건물명 등) 파싱 및 설정
                                val extraAddress = jsonElement.jsonObject["extraAddress"]?.jsonPrimitive?.content ?: ""
                                detailAddress.value = extraAddress

                                println("파싱된 주소 정보: 우편번호=$zonecode, 주소=$address, 상세=$extraAddress")
                            } catch (e: Exception) {
                                println("JSON 파싱 에러: $e")
                            }
                            showPersonalZipDialog.value = false  // 다이얼로그 닫기
                        },
                        onDismissRequest = {
                            showPersonalZipDialog.value = false
                        }
                    )

                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "주소", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = defaultAddress.value,
                    onValueChange = { defaultAddress.value = it } // 상태를 업데이트
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "상세 주소", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = detailAddress.value,
                    onValueChange = { detailAddress.value = it } // 상태를 업데이트
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

































            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
            }

            // 판매자 모드일 때 추가되는 필드들
            if (isSellerMode.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업자 등록번호",
                        style = labelTextStyle,

                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessNumber.value,
                        onValueChange = { businessNumber.value = it }
                        ,hint = " - 없이 입력",
                        enabled = !isBusinessValidated.value  // 인증 완료 시 수정 불가

                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    CustomBrownButton2(
                        onClick = {
                            signupViewModel.validateBusiness(
                                businessNumber = businessNumber.value,
                                businessRepresentativeName = businessRepresentativeName.value,
                                businessOpenDate = businessOpenDate.value,
                                onSuccess = {
                                    dialogMessage.value = "사업자 확인 완료"  // 성공 메시지
                                    showDialog.value = true  // 다이얼로그 표시
                                    isBusinessValidated.value = true  // 인증 완료 상태 업데이트

                                },
                                onError = { error ->
                                    dialogMessage.value = error  // 오류 메시지
                                    showDialog.value = true  // 다이얼로그 표시
                                }
                            )
                        },
                        text = stringResource(id = R.string.business_button_text)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "대표자 성명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessRepresentativeName.value,
                        onValueChange = { businessRepresentativeName.value = it },
                        enabled = !isBusinessValidated.value  // 인증 완료 시 수정 불가

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "개업일자",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessOpenDate.value,
                        onValueChange = { input ->
                            // 입력값을 포맷팅하여 저장 (8자리가 넘어가면 YYYY-MM-DD 형식)
                            businessOpenDate.value = formatBusinessDate(input)
                        },
                        hint = "YYYY-MM-DD 형식",
                        enabled = !isBusinessValidated.value  // 인증 완료 시 수정 불가

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessName.value,
                        onValueChange = { businessName.value = it },
                        enabled = !isBusinessValidated.value  // 인증 완료 시 수정 불가


                    )
                }

// 사업장 우편번호 입력 및 검색
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 우편번호",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessZipCode.value,
                        onValueChange = { businessZipCode.value = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(
                        onClick = {
                            showBusinessZipDialog.value = true // 사업자 주소 검색 다이얼로그 열기
                        },
                        text = "우편번호 검색"
                    )
                }

// 사업장 주소 검색 다이얼로그 (사업장 우편번호, 주소, 상세주소 업데이트)
                if (showBusinessZipDialog.value) {
                    DaumPostcodeDialog(
                        onAddressSelected = { addressJson ->
                            println("다음 우편번호 서비스에서 반환된 JSON: $addressJson")
                            try {
                                val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(addressJson)
                                val zonecode = jsonElement.jsonObject["zonecode"]?.jsonPrimitive?.content ?: ""
                                println("추출된 zonecode: $zonecode")

                                businessZipCode.value = zonecode // 사업장 우편번호 필드 업데이트
                                val address = jsonElement.jsonObject["address"]?.jsonPrimitive?.content ?: ""
                                businessDefaultAddress.value = address // 기본 주소 필드 업데이트

                                val extraAddress = jsonElement.jsonObject["extraAddress"]?.jsonPrimitive?.content ?: ""
                                businessDetailAddress.value = extraAddress // 상세 주소 필드 업데이트

                                println("파싱된 사업장 주소 정보: 우편번호=$zonecode, 주소=$address, 상세=$extraAddress")
                            } catch (e: Exception) {
                                println("JSON 파싱 에러: $e")
                            }
                            showBusinessZipDialog.value = false // 다이얼로그 닫기
                        },
                        onDismissRequest = {
                            showBusinessZipDialog.value = false
                        }
                    )
                }

// 사업장 주소 입력
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessDefaultAddress.value,
                        onValueChange = { businessDefaultAddress.value = it }
                    )
                }

// 사업장 상세 주소 입력
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 상세주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessDetailAddress.value,
                        onValueChange = { businessDetailAddress.value = it }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "거래 은행",
                        style = labelTextStyle
                    )
                    SelectorField(
                        label = {},
                        selections = bankList,
                        selected = businessAccountBank.value,
                        borderColor = primaryLighter,
                        backgroundColor = lightSelector,
                        modifier = Modifier.width(inputSize)
                    ) { newSelection ->
                        businessAccountBank.value = newSelection
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "계좌번호",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessAccountNumber.value,
                        onValueChange = { businessAccountNumber.value = it }
                    )
                }
            }


            Spacer(modifier = Modifier.size(32.dp))




            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(
                    onClick = {
                        navController.navigate(Routes.DeleteAccount.route) {
                            // UserInfoEditScreen까지 백스택에서 제거
                            popUpTo(Routes.UserInfoEditScreen.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryColor),
                    modifier = Modifier.height(36.dp)

                ) {
                    Text(text = "회원탈퇴하기", color = Color.Black)
                }
                Spacer(modifier = Modifier.size(8.dp))


                Button(
                    onClick = {
                        val isValid = validateInputs(
                            isSeller = isSellerMode.value,
                            userName = userName.value,
                            phoneNumber = phoneNumber.value,
                            zipCode = zipCode.value,
                            defaultAddress = defaultAddress.value,
                            detailAddress = detailAddress.value,
                            businessNumber = businessNumber.value,
                            businessName = businessName.value,
                            businessZipCode = businessZipCode.value,
                            businessAddress = businessDefaultAddress.value,
                            businessDetailAddress = businessDetailAddress.value
                        )

                        if (isValid) {
                            // 유효성 검사 통과 후 API 호출
                            scope.launch {
                                try {
                                    Log.d("UserInfo", "회원정보 수정 시작 - 모드: ${if (isSellerMode.value) "판매자" else "구매자"}")

                                    // API 호출 (판매자 또는 구매자 데이터에 맞게)
                                    val response = if (isSellerMode.value) {
                                        val sellerRequest = MemberApi.SellerUpdateRequest(
                                            phoneNumber = phoneNumber.value,
                                            zipCode = zipCode.value,
                                            defaultAddress = defaultAddress.value,
                                            detailAddress = detailAddress.value,
                                            businessNumber = businessNumber.value,
                                            businessRepresentativeName = businessRepresentativeName.value,
                                            businessOpenDate = businessOpenDate.value,
                                            businessName = businessName.value,
                                            businessZipCode = businessZipCode.value,
                                            businessDefaultAddress = businessDefaultAddress.value,
                                            businessDetailAddress = businessDetailAddress.value,
                                            businessAccountBank = businessAccountBank.value,
                                            businessAccountNumber = businessAccountNumber.value
                                        )
                                        api.updateSellerInfo(sellerRequest)
                                    } else {
                                        val buyerRequest = MemberApi.BuyerUpdateRequest(
                                            phoneNumber = phoneNumber.value,
                                            zipCode = zipCode.value,
                                            defaultAddress = defaultAddress.value,
                                            detailAddress = detailAddress.value
                                        )
                                        api.updateBuyerInfo(buyerRequest)
                                    }

                                    if (response.isSuccessful) {
                                        Log.d("UserInfo", "API 호출 성공")
                                        showSuccessDialog = true
                                    } else {
                                        Log.e("UserInfo", "API 호출 실패 - 상태 코드: ${response.code()}")
                                        errorMessage = "회원정보 수정에 실패했습니다. (${response.code()})"
                                        showErrorDialog = true
                                    }
                                } catch (e: Exception) {
                                    Log.e("UserInfo", "예외 발생", e)
                                    errorMessage = "오류가 발생했습니다: ${e.message}"
                                    showErrorDialog = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryLight),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "회원정보수정", color = Color.White)
                }
            }


            Spacer(modifier = Modifier.size(32.dp))

            Image(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.logo_transparant),
                contentDescription = "transparent logo"
            )
        }
    }



    BusinessValidationDialog(
        show = showDialog.value,  // 상태 변수의 value 사용
        message = dialogMessage.value,  // 상태 변수의 value 사용
        onDismiss = { showDialog.value = false }
    )
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("성공") },
            text = { Text("회원정보 수정에 성공했습니다.") },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("확인", color = primaryColor)
                }
            },
            containerColor = Color.White
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("실패") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("확인", color = primaryColor)
                }
            },
            containerColor = Color.White
        )
    }

}

@Composable
fun BusinessValidationDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("알림") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("확인", color = primaryColor)
                }
            },
            containerColor = Color.White
        )
    }
}



//유효성 검사.



