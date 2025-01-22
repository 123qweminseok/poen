package com.lodong.poen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.R
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryLight

@Composable
@Preview
fun LogoutScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader("로그아웃") {
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "안녕하세요 홍길동님 POEN입니다.\n계정을 로그아웃합니다.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                shape = RectangleShape,
                modifier = Modifier
                    .height(36.dp)
                    .width(160.dp)
            ) {
                Text(text = "로그아웃", color = Color.White)
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
}