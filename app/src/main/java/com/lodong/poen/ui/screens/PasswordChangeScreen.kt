// PasswordChangeScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.lodong.poen.ui.screens

import PreferencesHelper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lodong.apis.MemberApi
import com.lodong.poen.R
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryLight
import kotlinx.coroutines.launch

@Composable
fun PasswordChangeScreen(
    api: MemberApi,
    preferencesHelper: PreferencesHelper,
    navController: NavController,
    onBackButtonPressed: () -> Unit
) {
    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsHeader("비밀번호 변경") {
            onBackButtonPressed()
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )

        Spacer(modifier = Modifier.size(64.dp))

        Text(
            text = "안녕하세요 POEN입니다.\n새 비밀번호 설정을 완료해 주세요.",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = currentPassword.value,
                onValueChange = { currentPassword.value = it },
                hint = "현재 비밀번호를 입력해 주세요",
                isPassword = true
            )
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = newPassword.value,
                onValueChange = { newPassword.value = it },
                hint = "새로운 비밀번호를 입력해 주세요",
                isPassword = true
            )
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                hint = "새로운 비밀번호를 한번 더 입력해 주세요",
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.size(64.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        if (newPassword.value != confirmPassword.value) {
                            errorMessage = "새 비밀번호가 일치하지 않습니다"
                            showErrorDialog = true
                            return@launch
                        }

                        val response = api.changePassword(
                            MemberApi.PasswordChangeRequest(
                                beforePassword = currentPassword.value,
                                newPassword = newPassword.value
                            )
                        )

                        if (response.isSuccessful) {
                            onBackButtonPressed()
                        } else {
                            errorMessage = "비밀번호 변경에 실패했습니다"
                            showErrorDialog = true
                        }
                    } catch (e: Exception) {
                        errorMessage = "오류가 발생했습니다"
                        showErrorDialog = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            shape = RectangleShape,
            modifier = Modifier
                .height(36.dp)
                .width(160.dp)
        ) {
            Text(text = "확인", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.logo_transparant),
            contentDescription = "transparent logo"
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("알림") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                ) {
                    Text("확인", color = Color.White)
                }
            }
        )
    }
}
@Composable
fun InfoInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp, // 힌트 텍스트 크기 조정
                    color = MaterialTheme.colorScheme.onSurfaceVariant // 힌트 텍스트 색상
                ),
                maxLines = 1 // 힌트 텍스트가 한 줄로 표시되도록 설정
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp), // 입력 칸 높이를 조정 (기본 높이보다 여유를 둠)
        shape = RoundedCornerShape(8.dp), // 테두리 둥글기 조정
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary, // 포커스 시 테두리 색상
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant, // 기본 테두리 색상
            containerColor = Color.Transparent, // 입력 칸 내부 배경 제거
            cursorColor = MaterialTheme.colorScheme.primary // 커서 색상
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp, // 입력 텍스트 크기
            color = MaterialTheme.colorScheme.onSurface // 입력 텍스트 색상
        )
    )
}
