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
import com.lodong.poen.viewmodel.LoginViewModel

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


    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

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
                    checkedThumbColor = Color.Green,
                    uncheckedThumbColor = Color.Gray
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
                        println("우편번호 검색 버튼 클릭됨") // 로그 추가
                        showZipDialog = true
                    },
                    text = "우편번호 검색"
                )
// 기존 dialog 호출 부분 수정
                if (showZipDialog) {
                    DaumPostcodeDialog(
                        onAddressSelected = { addressJson ->
                            // 다이얼로그 내부에서 선택된 주소가 JSON 형태로 넘어옴
                            val data =
                                kotlinx.serialization.json.Json.decodeFromString<ZipAddressData>(
                                    addressJson
                                )
                            // ZipAddressData = (zonecode, address, extraAddress 등)

                            // 여기서 JoinScreen의 zipCode.value를 채운다
                            zipCode.value = data.zonecode
                            // 다른 필드도 필요하다면 더 설정
                            // defaultAddress.value = data.address

                            // 다이얼로그 닫기
                            showZipDialog = false
                        },
                        onDismissRequest = {
                            showZipDialog = false
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
                        ,hint = "ㅡ입력x"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(
                        onClick = {
                            signupViewModel.validateBusiness(
                                businessNumber = businessNumber.value,
                                businessRepresentativeName = businessRepresentativeName.value,
                                businessOpenDate = businessOpenDate.value,
                                onSuccess = {
                                    dialogMessage = "사업자 확인 완료"
                                    showDialog = true
                                },
                                onError = { error ->
                                    dialogMessage = error
                                    showDialog = true
                                }
                            )
                        },
                        text = "      사업자 확인\n(아래 4칸 모두 입력)",
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
                        onValueChange = { businessRepresentativeName.value = it }
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
                        onValueChange = { businessOpenDate.value = it },
                        hint = "YYYY-MM-DD"
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
                        onValueChange = { businessName.value = it }
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
                        onClick = { showZipDialog = true },
                        text = "우편번호 검색"
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
                        text = "사업장 주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessDefaultAddress.value,
                        onValueChange = { businessDefaultAddress.value = it }
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
                    onClick = {},
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
                        scope.launch {
                            try {
                                Log.d("UserInfo", "회원정보 수정 시작 - 모드: ${if (isSellerMode.value) "판매자" else "구매자"}")

                                val response = if (isSellerMode.value) {
                                    // 판매자 정보 업데이트 - 13개 필드
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
                                    Log.d("UserInfo", "판매자 요청 데이터: $sellerRequest")
                                    api.updateSellerInfo(sellerRequest)
                                } else {
                                    // 기존 구매자 정보 업데이트 코드는 그대로 유지
                                    val buyerRequest = MemberApi.BuyerUpdateRequest(
                                        phoneNumber = phoneNumber.value,
                                        zipCode = zipCode.value,
                                        defaultAddress = defaultAddress.value,
                                        detailAddress = detailAddress.value
                                    )
                                    Log.d("UserInfo", "구매자 요청 데이터: $buyerRequest")
                                    api.updateBuyerInfo(buyerRequest)
                                }

                                Log.d("UserInfo", "API 응답 코드: ${response.code()}")
                                Log.d("UserInfo", "API 응답 헤더: ${response.headers()}")

                                if (response.isSuccessful) {
                                    Log.d("UserInfo", "API 호출 성공")
                                    Log.d("UserInfo", "응답 데이터: ${response.body()}")
                                    showSuccessDialog = true
                                } else {
                                    Log.e("UserInfo", "API 호출 실패 - 상태 코드: ${response.code()}")
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("UserInfo", "에러 응답 body: $errorBody")
                                    Log.e("UserInfo", "에러 응답 헤더: ${response.headers()}")
                                    errorMessage = "회원정보 수정에 실패했습니다. (${response.code()})"
                                    showErrorDialog = true
                                }
                            } catch (e: Exception) {
                                Log.e("UserInfo", "예외 발생", e)
                                Log.e("UserInfo", "예외 메시지: ${e.message}")
                                Log.e("UserInfo", "예외 스택트레이스:", e)
                                errorMessage = "오류가 발생했습니다: ${e.message}"
                                showErrorDialog = true
                            }

                            Log.d("UserInfo", "코루틴 실행 종료")
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
        show = showDialog,
        message = dialogMessage,
        onDismiss = { showDialog = false }
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
