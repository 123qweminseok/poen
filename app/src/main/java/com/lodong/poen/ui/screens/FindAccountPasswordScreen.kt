@file:OptIn(ExperimentalMaterial3Api::class)

package com.lodong.poen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Handle UI state changes
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

    LaunchedEffect(findIdState) {
        when (findIdState) {
            is FindIdViewModel.UiState.Success -> {
                val result = (findIdState as FindIdViewModel.UiState.Success)
                val encodedId = java.net.URLEncoder.encode(result.identifier, "UTF-8")
                val encodedDate = java.net.URLEncoder.encode(result.regDate, "UTF-8")
                navController.navigate("find_id_result/$encodedId/$encodedDate") {
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "아이디 찾기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            var idName by remember { mutableStateOf("") }
            var idEmail by remember { mutableStateOf("") }

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
                    containerColor = Color(0xFF00C15B)
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

            Spacer(modifier = Modifier.height(60.dp))

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
                    containerColor = Color(0xFF00C15B)
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