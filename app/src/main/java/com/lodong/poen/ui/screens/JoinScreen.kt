package com.lodong.poen.ui.screens

import android.content.Context
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.lodong.poen.R
import com.lodong.poen.SeverRequestResponse.SignUpViewModel
import com.lodong.poen.ui.CustomBrownButton
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.InfoInputField
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.SettingsCategory
import com.lodong.poen.ui.SettingsEntry
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lodong.apis.SignUpApis
import com.lodong.poen.SeverRequestResponse.SignUpViewModelFactory
import com.lodong.poen.ui.navigation.Routes
import kotlinx.serialization.json.Json

@Composable
fun SignUpDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("알림") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("확인", color = primaryColor)
                }
            },
            containerColor = Color.White
        )
    }
}


@Composable
fun SignUpScreen(
    context: Context, // 추가

    api: SignUpApis,
    navController: NavController // NavController 추가

) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp

    )


    val signupViewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(api)
    )

    // Dialog 상태 추가
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSeller = remember { mutableStateOf(false) }


    val labelSize = 96.dp
    val inputSize = 168.dp

    val inputHeight = 40.dp
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val emailCode = remember { mutableStateOf("") }
    val zipCode = remember { mutableStateOf("") }
    val defaultAddress = remember { mutableStateOf("") }
    val detailAddress = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") } // 선언 추가
    val businessNumber = remember { mutableStateOf("") }
    val bossName = remember { mutableStateOf("") }
    val businessOpenDate = remember { mutableStateOf("") }
    val businessName = remember { mutableStateOf("") }
    val businessZipCode = remember { mutableStateOf("") }
    val businessAddress = remember { mutableStateOf("") }
    val businessDetailAddress = remember { mutableStateOf("") }
    val businessAccountNumber = remember { mutableStateOf("") }

    val selectedBank = remember { mutableStateOf("") } // 은행 선택 상태 추가
    var showZipDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf("") }


    // 입력 검증 함수
// 검증 함수 수정
    val validateInputs = remember {
        {
            when {
                userId.value.isEmpty() -> {
                    dialogMessage.value = "아이디를 입력해주세요"
                    false
                }
                password.value.isEmpty() -> {
                    dialogMessage.value = "비밀번호를 입력해주세요"
                    false
                }
                name.value.isEmpty() -> {
                    dialogMessage.value = "이름을 입력해주세요"
                    false
                }
                email.value.isEmpty() -> {
                    dialogMessage.value = "이메일을 입력해주세요"
                    false
                }
                phoneNumber.value.isEmpty() -> {
                    dialogMessage.value = "전화번호를 입력해주세요"
                    false
                }
                emailCode.value.isEmpty() -> {
                    dialogMessage.value = "이메일 인증을 완료해주세요"
                    false
                }
                zipCode.value.isEmpty() -> {
                    dialogMessage.value = "우편번호를 입력해주세요"
                    false
                }
                defaultAddress.value.isEmpty() -> {
                    dialogMessage.value = "기본 주소를 입력해주세요"
                    false
                }
                isSeller.value && (businessNumber.value.length != 10 || !businessNumber.value.all { it.isDigit() }) -> {
                    dialogMessage.value = "사업자 등록번호는 10개 입력해야 합니다"
                    false
                }
                // 판매자인 경우 추가 검증
                isSeller.value && businessNumber.value.isEmpty() -> {
                    dialogMessage.value = "사업자 등록번호를 입력해주세요"
                    false
                }
                isSeller.value && businessName.value.isEmpty() -> {
                    dialogMessage.value = "사업장명을 입력해주세요"
                    false
                }
                isSeller.value && bossName.value.isEmpty() -> {
                    dialogMessage.value = "대표자 성명을 입력해주세요"
                    false
                }
                isSeller.value && businessOpenDate.value.isEmpty() -> {
                    dialogMessage.value = "개업일자를 입력해주세요"
                    false
                }
                isSeller.value && businessAddress.value.isEmpty() -> {
                    dialogMessage.value = "사업장 주소를 입력해주세요"
                    false
                }
                isSeller.value && businessDetailAddress.value.isEmpty() -> {
                    dialogMessage.value = "사업장 상세주소를 입력해주세요"
                    false
                }
                isSeller.value && selectedBank.value.isEmpty() -> {
                    dialogMessage.value = "은행을 선택해주세요"
                    false
                }
                isSeller.value && businessAccountNumber.value.isEmpty() -> {
                    dialogMessage.value = "계좌번호를 입력해주세요"
                    false
                }
                else -> true
            }
        }
    }


    val bankList = listOf(
        "한국씨티은행", "우리은행", "전북은행", "NH농협은행", "하나은행",
        "카카오뱅크", "KB국민은행", "IBK기업은행", "경남은행", "대구은행",
        "부산은행", "케이뱅크", "수협은행", "신한은행", "KDB산업은행",
        "제주은행", "광주은행", "SC제일은행"
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 상단 헤더 (회원가입 제목)

        Header(text = "회원가입", onBackButtonPressed = {})


        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )


        // 본문 입력 섹션
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 아이디 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "아이디", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "아이디를 입력하세요",
                    value = userId.value, // 상태 값 연결
                    onValueChange = { userId.value = it } // 상태 업데이트
                )
            }

            // 비밀번호 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "비밀번호", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "비밀번호를 입력하세요",
                    value = password.value, // 상태 값 연결
                    onValueChange = { password.value = it } // 상태 업데이트
                )
            }


            // 이름 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "이름", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = name.value,
                    hint = "홍길동",
                    onValueChange = { name.value = it }
                )
            }

            // 이메일 입력 필드
           Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
               Text(
                   modifier = Modifier.width(labelSize),
                   text = "이메일 주소",
                   style = labelTextStyle
               )
               InfoInputField(
                   modifier = Modifier.width(inputSize),
                   hint = "test@gmail.com",
                   value = email.value,
                   onValueChange = { email.value = it }
               )
               Spacer(modifier = Modifier.width(8.dp))
               CustomBrownButton(
                   onClick = {
                       println("인증하기 버튼 클릭됨") // 로그 추가
                       signupViewModel.sendEmailVerificationCode(
                           email = email.value,
                           onSuccess = { expiresAt ->
                               println("이메일 인증 코드 전송 성공") // 성공 로그
                               dialogMessage.value = "전송 성공"
                               showDialog.value = true
                           },
                           onError = { errorMessage ->
                               println("이메일 인증 코드 전송 실패: $errorMessage") // 실패 로그
                               dialogMessage.value = errorMessage
                               showDialog.value = true
                           }
                       )
                   },
                   text = "인증하기"
               )



           }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "전화번호", style = labelTextStyle)
                InfoInputField(modifier = Modifier.width(inputSize), value = phoneNumber.value, onValueChange = {phoneNumber.value = it})
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "이메일 인증 코드",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "인증 코드를 입력하세요",
                    value = emailCode.value,
                    onValueChange = { emailCode.value = it }
                )

                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        signupViewModel.verifyEmailCode(
                            email = email.value,
                            code = emailCode.value,
                            onSuccess = {
                                dialogMessage.value = "인증 완료되었습니다"
                                showDialog.value = true
                                println("이메일 인증 성공")
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                                println("이메일 인증 실패: $errorMessage")
                            }
                        )
                    },
                    text = "인증확인"
                )





            }


// 우편번호 입력
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "우편번호",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "우편번호 입력",
                    value = zipCode.value,
                    onValueChange = { zipCode.value = it }
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
                    ZipCodeSearchDialog(
                        onClose = { showZipDialog = true },
                        onAddressSelected = { addressJson ->
                            val address = Json.decodeFromString<AddressData>(addressJson)
                            zipCode.value = address.zonecode
                            defaultAddress.value = address.address
                            showZipDialog = false
                        }
                    )
                }

            }

// 기본 주소 입력
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "기본 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "기본 주소 입력",
                    value = defaultAddress.value,
                    onValueChange = { defaultAddress.value = it }
                )
            }





// 상세 주소 입력

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "상세 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "상세 주소 입력",
                    value = detailAddress.value,
                    onValueChange = { detailAddress.value = it }
                )
            }







            // 구매자/판매자 선택 (라디오 버튼)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "구매자/판매자\n관리 선택",
                    style = labelTextStyle
                )
                val radioButtonColor = Color(0xFFBAC784)
                RadioButton(
                    modifier = Modifier.size(24.dp),
                    selected = !isSeller.value,
                    onClick = { isSeller.value = false },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = radioButtonColor,
                        unselectedColor = radioButtonColor
                    )
                )
                Text(text = "매물정보(구매자)")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    modifier = Modifier.size(24.dp),
                    selected = isSeller.value,
                    onClick = { isSeller.value = true },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = radioButtonColor,
                        unselectedColor = radioButtonColor
                    )
                )
                Text(text = "진단정보(판매자)")
            }





            // 판매자로 선택된 경우 추가 입력 필드 표시
            if (isSeller.value) {
                // 사업자 등록번호 입력 필드
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
                        value = businessNumber.value,
                        onValueChange = { businessNumber.value = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(
                        onClick = {
                            signupViewModel.validateBusiness(
                                businessNumber = businessNumber.value,
                                businessRepresentativeName = bossName.value,
                                businessOpenDate = businessOpenDate.value,
                                onSuccess = {
                                    // 성공 처리
                                    dialogMessage.value = "사업자 확인 완료"
                                    showDialog.value = true
                                },
                                onError = { error ->
                                    // 실패 처리
                                    dialogMessage.value = error
                                    showDialog.value = true
                                }
                            )
                        },
                        text = "사업자 확인"
                    )                }

                // 대표자 성명
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
                        value = bossName.value,
                        onValueChange = { bossName.value = it }
                    )
                }

                // 사업장명
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

                // 개업일자
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

                // 사업장 우편번호
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
                    CustomBrownButton(onClick = {}, text = "우편번호검색")
                }

                // 사업장 기본주소
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
                        value = businessAddress.value,
                        onValueChange = { businessAddress.value = it }
                    )
                }

                // 사업장 상세주소
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

                // 계좌 은행
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
                        selections = bankList,
                        selected = selectedBank.value,
                        borderColor = primaryLighter,
                        backgroundColor = lightSelector,
                        modifier = Modifier.width(inputSize)
                    ) { newSelection ->
                        selectedBank.value = newSelection
                    }
                }

                // 계좌번호
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessAccountNumber.value,
                        onValueChange = { businessAccountNumber.value = it },
                        hint = "계좌번호를 입력해주세요"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // 회원가입 버튼
            Spacer(modifier = Modifier.size(32.dp))

            Button(
                onClick = {
                    if (validateInputs()){
                    if (isSeller.value) {
                        signupViewModel.sellerSignup(
                            identifier = userId.value,
                            password = password.value,
                            name = name.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            emailCode = emailCode.value,
                            zipCode = zipCode.value,
                            defaultAddress = defaultAddress.value,
                            detailAddress = detailAddress.value,
                            businessNumber = businessNumber.value,
                            businessRepresentativeName = bossName.value,
                            businessOpenDate = businessOpenDate.value,
                            businessName = businessName.value,
                            businessZipCode = businessZipCode.value,
                            businessDefaultAddress = businessAddress.value,
                            businessDetailAddress = businessDetailAddress.value,
                            businessAccountBank = selectedBank.value,  // 선택된 은행으로 변경
                            businessAccountNumber = businessAccountNumber.value,
                            onSuccess = {
                                dialogMessage.value = "회원가입이 완료되었습니다"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )
                    } else {
                        // 기존 구매자 회원가입 코드 유지
                        signupViewModel.register(
                            identifier = userId.value,
                            password = password.value,
                            name = name.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            emailCode = emailCode.value,
                            zipCode = zipCode.value,
                            defaultAddress = defaultAddress.value,
                            detailAddress = detailAddress.value,
                            isSeller = isSeller.value,
                            onSuccess = {
                                dialogMessage.value = "회원가입이 완료되었습니다"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )}}else{
                        showDialog.value = true  // 검증 실패시 다이얼로그 표시


                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, primaryLight),
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "회원가입하기", color = Color.White)
            }









            // 하단 로고 이미지

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

    SignUpDialog(
        show = showDialog.value,
        message = dialogMessage.value,
        onDismiss = { showDialog.value = false },
        onConfirm = {
            showDialog.value = false
            if (dialogMessage.value == "회원가입이 완료되었습니다") {
                navController.navigate(Routes.LoginScreen.route)
            }
        }
    )
}
