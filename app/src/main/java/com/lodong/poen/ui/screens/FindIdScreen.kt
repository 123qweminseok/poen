package com.lodong.poen.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lodong.poen.R
import com.lodong.poen.factory.FindIdViewModelFactory
import com.lodong.poen.repository.SignUpRepository
import com.lodong.poen.viewmodel.FindIdViewModel

@Composable
fun FindIdResultScreen(
    navController: NavController,
    identifier: String,
    regDate: String,
    name: String,
    email: String
) {
    val context = LocalContext.current
    val preferenceHelper = PreferencesHelper.getInstance(context)
    val signUpRepository = SignUpRepository(preferenceHelper)
    val viewModel: FindIdViewModel = viewModel(
        factory = FindIdViewModelFactory(signUpRepository.apiService)
    )

    // 이메일 전송 상태
    val emailSendState by viewModel.emailSendState.collectAsState()

    // 다이얼로그 표시 여부를 기억하기 위한 State
    var showSuccessDialogState by remember { mutableStateOf(false) }
    var showErrorDialogState by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(emailSendState) {
        when (emailSendState) {
            is FindIdViewModel.EmailSendState.Success -> {
                // 성공 시 다이얼로그 띄우기
                showSuccessDialogState = true
            }
            is FindIdViewModel.EmailSendState.Error -> {
                val msg = (emailSendState as FindIdViewModel.EmailSendState.Error).message
                errorMessage = msg
                showErrorDialogState = true
            }
            else -> {}
        }
    }

    // 실제 UI 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = "아이디 찾기",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp), // 둥근 모서리
            colors = CardDefaults.cardColors(containerColor = Color.White), // 카드 배경을 화이트로 설정
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // 그림자 효과 추가

        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Text(
                    text = "고객님의 아이디 회원가입이 되어 있습니다.",
                    fontWeight = FontWeight.SemiBold, // 살짝 강조
                    color = Color.Black, // 텍스트 가독성 증가

                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "아이디 확인 후 로그인해 주세요.",
                    color = Color.DarkGray, // 연한 회색으로 변경하여 가독성 향상
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "확인된 아이디",
                    color = Color(0xFF4CAF50), // 강조를 위해 초록색

                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = identifier,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "가입일: $regDate",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.sendIdToEmail(name, email) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("아이디 이메일로 전송 ")
            }

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF90CAF9)
                )
            ) {
                Text("로그인")
            }
        }
    }

    // 성공 다이얼로그
    if (showSuccessDialogState) {
        SuccessDialog(
            onDismiss = { showSuccessDialogState = false }
        )
    }

    // 에러 다이얼로그
    if (showErrorDialogState) {
        ErrorDialog(
            errorMessage = errorMessage,
            onDismiss = { showErrorDialogState = false }
        )
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // 연한 그린 버튼
            ) {
                Text(text = "확인", color = Color.White) // 버튼 텍스트 흰색
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp), // 상단과의 간격 추가
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "이메일 전송 성공!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 깔끔한 원형 배경 아이콘
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFFE8F5E9), shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.success_image),
                        contentDescription = "success image",
                        tint = Color.Unspecified, // 이미지 색상 유지
                        modifier = Modifier
                            .size(64.dp)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "아이디가 이메일로 전송되었습니다.",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        },
        containerColor = Color.White, // 다이얼로그 배경을 흰색으로 설정
        tonalElevation = 4.dp
    )
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "확인")
            }
        },
        title = {
            Text(text = "이메일 전송 실패", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.error_image),
//                    contentDescription = "error image",
//                    tint = Color.Unspecified,
//                    modifier = Modifier
//                        .size(80.dp)
//                        .padding(bottom = 8.dp)
//                )
                Text(errorMessage)
            }
        }
    )
}
