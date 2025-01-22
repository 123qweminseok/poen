package com.lodong.poen.ui.screens

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.lodong.apis.SignUpApis
import com.lodong.poen.SeverRequestResponse.SignUpViewModelFactory


@Composable
fun SignUpScreen(
    api: SignUpApis,
) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp

    )


    val signupViewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(api)
    )


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
            val password = remember { mutableStateOf("") } // 선언 추가

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

            val name = remember { mutableStateOf("") }

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
            val email = remember { mutableStateOf("") }

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
                       // 이메일 인증 요청 API 호출
                       signupViewModel.sendEmailVerificationCode(
                           email = email.value,
                           onSuccess = { expiresAt ->
                               // 인증 코드 발송 성공 처리 (예: 성공 메시지 표시)
                               println("인증 코드 발송 성공, 만료 시간: $expiresAt")
                           },
                           onError = { errorMessage ->
                               // 에러 메시지 처리
                               println("인증 코드 발송 실패: $errorMessage")
                           }
                       )
                   },
                   text = "인증하기"
               )            }


            val phoneNumber = remember { mutableStateOf("") }
            // 전화번호 입력 필드

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
                                // 인증 성공 처리 (예: 성공 메시지 표시)
                                println("이메일 인증 성공")
                            },
                            onError = { errorMessage ->
                                // 에러 메시지 처리
                                println("이메일 인증 실패: $errorMessage")
                            }
                        )
                    },
                    text = "인증확인"
                )





            }

            val zipCode = remember { mutableStateOf("") }
            val defaultAddress = remember { mutableStateOf("") }
            val detailAddress = remember { mutableStateOf("") }

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
                CustomBrownButton(onClick = { /* 우편번호 검색 로직 */ }, text = "우편번호 검색")
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







            val isSeller = remember { mutableStateOf(false) }
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



            val businessNumber = remember { mutableStateOf("") }
            val bossName = remember { mutableStateOf("") }


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
                        value = businessNumber.value,
                        onValueChange = {businessNumber.value = it})
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
                    CustomBrownButton(onClick = {}, text = "계좌확인")
                }
            }

            // 회원가입 버튼
            Spacer(modifier = Modifier.size(32.dp))

            Button(
                onClick = {
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
                            // 회원가입 성공 처리 (예: 로그인 화면으로 이동)
                        },
                        onError = { errorMessage ->
                            // 에러 메시지 표시
                        }
                    )
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
}
