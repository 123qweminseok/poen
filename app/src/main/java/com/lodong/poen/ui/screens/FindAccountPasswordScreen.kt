@file:OptIn(ExperimentalMaterial3Api::class)

package com.lodong.poen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lodong.poen.R
import com.lodong.poen.viewmodel.FindIdViewModel
import com.lodong.poen.viewmodel.FindPasswordViewModel

@Composable
fun FindAccountPasswordScreen(
    navController: NavController,
    findIdViewModel: FindIdViewModel,
    findPasswordViewModel: FindPasswordViewModel
) {
    val findIdState by findIdViewModel.findIdState.collectAsState()
    val findPasswordState by findPasswordViewModel.findPasswordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var idName by remember { mutableStateOf("") }
    var idEmail by remember { mutableStateOf("") }



    // 기존 로직 유지 (LaunchedEffect - ID 찾기)
    LaunchedEffect(findIdState) {
        when (findIdState) {
            is FindIdViewModel.UiState.Success -> {
                val result = (findIdState as FindIdViewModel.UiState.Success)
                val encodedId = java.net.URLEncoder.encode(result.identifier, "UTF-8")
                val encodedDate = java.net.URLEncoder.encode(result.regDate, "UTF-8")
                val encodedName = java.net.URLEncoder.encode(idName, "UTF-8")  // idName은 입력 필드의 값
                val encodedEmail = java.net.URLEncoder.encode(idEmail, "UTF-8") // idEmail은 입력 필드의 값
                navController.navigate("find_id_result/$encodedId/$encodedDate/$encodedName/$encodedEmail") {
                    popUpTo("find_account_password") { inclusive = true }
                }
            }
            is FindIdViewModel.UiState.Error -> {
                val errorMessage = (findIdState as FindIdViewModel.UiState.Error).message
                snackbarHostState.showSnackbar(message = errorMessage, duration = SnackbarDuration.Short)
            }
            else -> {}
        }
    }

    // 기존 로직 유지 (LaunchedEffect - 비밀번호 찾기)
    LaunchedEffect(findPasswordState) {
        when (findPasswordState) {
            is FindPasswordViewModel.UiState.Success -> {
                val result = (findPasswordState as FindPasswordViewModel.UiState.Success)
                val encodedId = java.net.URLEncoder.encode(result.identifier, "UTF-8")
                navController.navigate("find_password_result/$encodedId") {
                    popUpTo("find_account_password") { inclusive = true }
                }
            }
            is FindPasswordViewModel.UiState.Error -> {
                val errorMessage = (findPasswordState as FindPasswordViewModel.UiState.Error).message
                snackbarHostState.showSnackbar(message = errorMessage, duration = SnackbarDuration.Short)
            }
            else -> {}
        }
    }

    // 스낵바 등을 위한 Scaffold
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // 배경을 위해 Box 사용
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.logo_transparant),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)    // 기본적으로 중앙 정렬
                    .offset(y = (-60).dp)       // y축으로 -60dp만큼 이동 (위로 이동)
                    .size(300.dp),
                contentScale = ContentScale.Fit
            )
            // 메인 내용
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // ----------------- 아이디 찾기 섹션 -----------------
                Text(
                    text = "아이디 찾기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 40.dp)
                )


                OutlinedTextField(
                    value = idName,
                    onValueChange = { idName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    placeholder = {
                        Text("이름을 입력해주세요", color = Color(0xFFAAAAAA))
                    }
                )
                val baseColor = colorResource(id = R.color.primary) // colors.xml에 "#00C15B" 정의
                val buttonColor = baseColor.copy(alpha = 0.6f) // 원본보다 조금 투명하게

                OutlinedTextField(
                    value = idEmail,
                    onValueChange = { idEmail = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    placeholder = {
                        Text("이메일주소를 입력해주세요", color = Color(0xFFAAAAAA))
                    }
                )

                Button(
                    onClick = { findIdViewModel.findId(idName, idEmail) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }

                // 구분선 (사진처럼, 두 섹션 사이를 구분)
                Spacer(modifier = Modifier.height(40.dp))
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(40.dp))

                // ----------------- 비밀번호 찾기 섹션 -----------------
                Text(
                    text = "비밀번호 찾기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                var pwName by remember { mutableStateOf("") }
                var pwEmail by remember { mutableStateOf("") }
                var pwIdentifier by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = pwName,
                    onValueChange = { pwName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    placeholder = {
                        Text("이름을 입력해주세요", color = Color(0xFFAAAAAA))
                    }
                )

                OutlinedTextField(
                    value = pwEmail,
                    onValueChange = { pwEmail = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    placeholder = {
                        Text("이메일주소를 입력해주세요", color = Color(0xFFAAAAAA))
                    }
                )

                OutlinedTextField(
                    value = pwIdentifier,
                    onValueChange = { pwIdentifier = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    placeholder = {
                        Text("아이디를 입력해주세요", color = Color(0xFFAAAAAA))
                    }
                )

                Button(
                    onClick = { findPasswordViewModel.findPassword(pwName, pwEmail, pwIdentifier) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }
            }
        }
    }
}
