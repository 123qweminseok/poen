package com.lodong.poen.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.R
import com.lodong.poen.ui.theme.hintColor
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter
import com.lodong.poen.viewmodel.LoginViewModel
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpNavigation: () -> Unit
) {
    val loginState = loginViewModel.loginState.value // ViewModel 상태 관찰
    val id = remember { mutableStateOf("") } // 아이디 상태 관리
    val password = remember { mutableStateOf("") } // 비밀번호 상태 관리
    val stayLoggedIn = remember { mutableStateOf(false) } // 로그인 유지 상태 관리

    // 로그인 유지 상태 확인
    LaunchedEffect(Unit) {
        if (loginViewModel.checkIfLoggedIn()) {
            onLoginSuccess() // 사용자가 로그인 상태라면 메인 화면으로 전환
        }
    }

    BackHandler {
        // Ignore back button
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .scale(1.5f)
                .padding(vertical = 32.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "LOGIN", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextField(
            value = id.value,
            onValueChange = { id.value = it },
            label = { Text("아이디") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = primaryLighter,
                focusedContainerColor = primaryLighter,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = hintColor
            ),
            modifier = Modifier
                .border(1.dp, primaryLight, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("비밀번호") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = primaryLighter,
                focusedContainerColor = primaryLighter,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = hintColor
            ),
            modifier = Modifier
                .border(1.dp, primaryLight, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth(0.8f),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // 로그인 유지 체크박스 추가
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = stayLoggedIn.value,
                onCheckedChange = { stayLoggedIn.value = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
            )
            Text(text = "로그인 유지", fontSize = 16.sp, color = Color.Black)
        }

        Text(text = "아이디/비밀번호 찾기", modifier = Modifier.padding(vertical = 16.dp))

        Divider(
            modifier = Modifier.fillMaxWidth(0.8f), color = primaryLight, thickness = 1.dp
        )

        Spacer(modifier = Modifier.size(32.dp))

//         로그인 버튼
        Button(
            onClick = { loginViewModel.login(id.value, password.value, stayLoggedIn.value) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "로그인", color = Color.White)
        }

        // 상태에 따른 처리
        when (loginState) {
            is LoginViewModel.LoginUiState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginViewModel.LoginUiState.Success -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }

            }
            is LoginViewModel.LoginUiState.Error -> {
                Text(
                    text = "로그인 실패: ${loginState.message}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.size(16.dp))

        // 회원가입 버튼
        Button(
            onClick = onSignUpNavigation,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "회원가입", color = Color.White)
        }

//        Spacer(modifier = Modifier.weight(1f))
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
