package com.lodong.poen.ui.screens

import PreferencesHelper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.lodong.apis.MemberApi
import com.lodong.poen.R
import com.lodong.poen.ui.InfoInputField
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.navigation.Routes
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun DeleteAccountScreen(
    api: MemberApi,
    preferencesHelper: PreferencesHelper,
    navController: NavController,
    onBackButtonPressed: () -> Unit
) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
    val labelSize = 96.dp

    val password = remember { mutableStateOf("") }
    val reason = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader("회원탈퇴") {
            onBackButtonPressed()
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "회원탈퇴 안내",
                    style = labelTextStyle
                )
                Image(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.bye),
                    contentDescription = "bye",
                    contentScale = ContentScale.FillWidth
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "비밀번호 입력",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // 입력 칸 크기 확대
                    hint = "현재 비밀번호를 입력해주세요",
                    value = password.value,
                    onValueChange = { password.value = it },
                    isPassword = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "불편한 점이\n있으셨나요?",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    hint = "무엇이 불편하셨는지 남겨주시면 향후 적극 반영하여 더 나은 서비스를 제공하도록 하겠습니다.",
                    singleLine = false,
                    value = reason.value,
                    onValueChange = { reason.value = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(labelSize))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { onBackButtonPressed() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, primaryColor),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "취소하기", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = {
                            if (password.value.isEmpty()) {
                                errorMessage = "비밀번호를 입력해주세요"
                                showErrorDialog = true
                                return@Button
                            }

                            scope.launch {
                                try {
                                    val token = preferencesHelper.getAccessToken() ?: ""
                                    val bearerToken = if (!token.startsWith("Bearer ")) "Bearer $token" else token

                                    val response = api.deleteAccount(
                                        MemberApi.DeleteAccountRequest(
                                            password = password.value,
                                            reason = reason.value
                                        )

                                    )
                                    if (response.isSuccessful && response.body()?.status == 200) {
                                        preferencesHelper.clear() // 토큰 등 저장된 데이터 삭제
                                        navController.navigate(Routes.LoginScreen.route) {
                                            popUpTo(0) // 스택 전체 제거
                                        }
                                    } else {
                                        Log.e("DeleteAccount", "실패 - 상태 코드: ${response.code()}")
                                        errorMessage = "탈퇴 처리에 실패했습니다"
                                        showErrorDialog = true
                                    }
                                } catch (e: Exception) {
                                    Log.e("DeleteAccount", "에러 발생", e)
                                    errorMessage = "오류가 발생했습니다"
                                    showErrorDialog = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "탈퇴하기", color = Color.White)
                    }
                }
            }
        }

        Image(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.logo_transparant),
            contentDescription = "transparent logo"
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("오류") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("확인")
                }
            }
        )
    }
}
