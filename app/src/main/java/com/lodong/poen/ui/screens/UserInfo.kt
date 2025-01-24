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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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

@Composable
fun UserInfoEditScreen(
    isSeller: Boolean,
    api: MemberApi,
    viewModel: UserInfoViewModel,  // 추가
    preferencesHelper: PreferencesHelper,  // 추가
    navController: NavHostController, // navController 추가

    onBackButtonPressed: () -> Unit


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


//    val token = preferencesHelper.getAccessToken() ?: ""
//    if (token.isNullOrEmpty()) {
//        // 토큰이 없으면 로그인 화면으로 이동하거나 에러 처리
//        navController.navigate(Routes.LoginScreen.route)
//        return
//    }



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
                CustomBrownButton(onClick = {}, text = "검색")
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
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
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
                        value = "",
                        onValueChange = {})
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
                        value = "",
                        onValueChange = {})
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
                        value = "",
                        onValueChange = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "사업자 확인")
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
                        value = "",
                        onValueChange = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "우편번호검색")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {},
                        hint = "나머지 주소를 입력해 주세요"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "확인")
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
                    SelectorField(
                        label = {},
                        selections = listOf("국민은행"),
                        selected = "국민은행",
                        borderColor = primaryLighter,
                        backgroundColor = lightSelector,
                        modifier = Modifier.width(inputSize)
                    ) { }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {}
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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
                        Log.d("UserInfo", "버튼 클릭됨")

                        if (phoneNumber.value.isEmpty() || zipCode.value.isEmpty() ||
                            defaultAddress.value.isEmpty() || detailAddress.value.isEmpty()
                        ) {
                            errorMessage = "모든 필드를 입력해주세요."
                            Log.e("UserInfo", "필드가 비어 있음 - 전화번호: ${phoneNumber.value}, 우편번호: ${zipCode.value}, 주소: ${defaultAddress.value}, 상세주소: ${detailAddress.value}")
                            showErrorDialog = true
                            return@Button
                        } else {
                            Log.d("UserInfo", "모든 필드가 정상적으로 입력됨 - 전화번호: ${phoneNumber.value}, 우편번호: ${zipCode.value}, 주소: ${defaultAddress.value}, 상세주소: ${detailAddress.value}")
                        }


                        scope.launch {
                            Log.d("UserInfo", "코루틴 실행 시작")

                            try {
                                // API 요청 전 로그 추가
                                Log.d(
                                    "API Request",
                                    "Request Body: ${
                                        MemberApi.BuyerUpdateRequest(
                                            phoneNumber.value,
                                            zipCode.value,
                                            defaultAddress.value,
                                            detailAddress.value
                                        )
                                    }"
                                )
                                Log.d(
                                    "API Request",
                                    "Authorization Header: Bearer ${preferencesHelper.getAccessToken()}"
                                )

                                // API 호출
                                val response = api.updateBuyerInfo(
                                    MemberApi.BuyerUpdateRequest(
                                        phoneNumber = phoneNumber.value,
                                        zipCode = zipCode.value,
                                        defaultAddress = defaultAddress.value,
                                        detailAddress = detailAddress.value
                                    )
                                )

                                // API 응답 로그 추가
                                if (response.isSuccessful) {
                                    Log.d("API Response", "Status Code: ${response.code()}")
                                    Log.d("API Response", "Response Body: ${response.body()}")
                                    showSuccessDialog = true
                                } else {
                                    errorMessage = "회원정보 수정에 실패했습니다. (상태 코드: ${response.code()})"
                                    Log.e("API Response", "Status Code: ${response.code()}")
                                    Log.e("API Response", "Error Body: ${response.errorBody()?.string()}")
                                    showErrorDialog = true
                                }
                            } catch (e: Exception) {
                                errorMessage = "오류가 발생했습니다: ${e.message}"
                                Log.e("UserInfo", "코루틴 내부 예외 발생", e)
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
}

