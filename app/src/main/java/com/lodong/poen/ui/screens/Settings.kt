package com.lodong.poen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.R
import com.lodong.poen.ui.SettingsCategory
import com.lodong.poen.ui.SettingsEntry
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.primaryColor

@Composable
fun SettingsScreen(
    onAccountSettingNavigation: () -> Unit,
    onNoticeNavigation: () -> Unit = {},
    onSupportNavigation: () -> Unit = {},
    onVersionInfoNavigation: () -> Unit = {},
    onBackButtonPressed: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "설정", onBackButtonPressed = onBackButtonPressed)
        SettingsCategory(text = "계정")
        SettingsEntry(text = "계정설정") {
            onAccountSettingNavigation()
        }
        SettingsCategory(text = "고객지원")
        SettingsEntry(text = "공지사항") {
            onNoticeNavigation()
        }
        SettingsEntry(text = "문의 게시판") {
            onSupportNavigation()
        }
        SettingsEntry(text = "버전 정보") {
            onVersionInfoNavigation()
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
}

@Composable
fun AccountSettingScreen(
    onBackButtonPressed: () -> Unit,
    onInfoEditNavigation: () -> Unit,
    onPasswordChangeNavigation: () -> Unit,
    onLogoutNavigation: () -> Unit,
    onAccountDeletionNavigation: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "계정 설정", onBackButtonPressed = onBackButtonPressed)
        SettingsCategory(text = "")
        SettingsEntry(text = "회원정보 수정") {
            onInfoEditNavigation()
        }
        SettingsEntry(text = "비밀번호 변경") {
            onPasswordChangeNavigation()
        }
        SettingsEntry(text = "로그아웃") {
            onLogoutNavigation()
        }
        SettingsEntry(text = "회원탈퇴", textColor = primaryColor) {
            onAccountDeletionNavigation()
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
}

@Composable
fun VersionInfoScreen(onBackButtonPressed: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "버전 정보", onBackButtonPressed = onBackButtonPressed)
        SettingsCategory(text = "")
        Spacer(modifier = Modifier.size(96.dp))
        Text(text = "v 3.5", fontWeight = FontWeight.Bold, fontSize = 36.sp)
        Text(text = "새로운 업데이트가 있습니다.", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 16.dp),
            color = primaryColor,
            thickness = 2.dp
        )
        Text(text = "현재버전 v 3.3", color = Color.LightGray)

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
}