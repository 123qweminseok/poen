package com.lodong.poen.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lodong.poen.R
import com.lodong.poen.ui.LeafImageButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight // ChevronRight 대신 사용 가능
@Composable
fun MainScreen(
    onSettingsNavigation: () -> Unit,
    onBatteryInfoNavigation: () -> Unit,
    onBluetoothNavigation: () -> Unit,
    onDiagnoseNavigation: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFE8F5E9)
                    )
                )
            )
            .fillMaxSize()
    ) {
        // 상단 헤더 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Image(
                modifier = Modifier
                    .scale(1.5f)
                    .padding(vertical = 32.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo"
            )
            IconButton(
                onClick = { onSettingsNavigation() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "settings",
                    modifier = Modifier
                        .size(24.dp)
                        .shadow(4.dp, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 메인 버튼 영역
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainButton(
                text = "배터리 정보 입력",
                icon = R.drawable.battery_full,
                onClick = onBatteryInfoNavigation
            )
            MainButton(
                text = "장치 연결",
                icon = R.drawable.bluetooth,
                onClick = onBluetoothNavigation
            )
            MainButton(
                text = "진단 시작",
                icon = R.drawable.diagnosis,
                onClick = onDiagnoseNavigation
            )
            MainButton(
                text = "BERI-Link",
                icon = R.drawable.link,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 하단 로고
        Image(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(0.7f),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.logo_transparant),
            contentDescription = "transparent logo",
            alpha = 0.8f
        )
    }
}

@Composable
private fun MainButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(  // 테두리 추가
            width = 1.5.dp,
            color = Color(0xFF4CAF50).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color(0xFF4CAF50).copy(alpha = 0.5f)
            )
        }
    }
}