package com.lodong.poen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lodong.poen.ui.FieldLabel
import com.lodong.poen.R
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.hintColor
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter
import com.lodong.poen.viewmodel.InquiryViewModel

private enum class Tabs {
    INQUIRY, INQUIRY_HISTORY
}

@Composable
fun InquiryScreen(onBackButtonPressed: () -> Unit) {
    val title = remember { mutableStateOf("") }
    val content = remember { mutableStateOf("") }
    val currentTab = remember { mutableStateOf(Tabs.INQUIRY) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "문의 게시판", onBackButtonPressed = onBackButtonPressed)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "문의하기",
                color = if (currentTab.value == Tabs.INQUIRY) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(if (currentTab.value == Tabs.INQUIRY) primaryColor else primaryLighter)
                    .padding(vertical = 16.dp)
                    .weight(1f)
                    .clickable { currentTab.value = Tabs.INQUIRY },
                textAlign = TextAlign.Center
            )
            Text(
                text = "문의내역",
                color = if (currentTab.value == Tabs.INQUIRY_HISTORY) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(if (currentTab.value == Tabs.INQUIRY_HISTORY) primaryColor else primaryLighter)
                    .padding(vertical = 16.dp)
                    .weight(1f)
                    .clickable { currentTab.value = Tabs.INQUIRY_HISTORY },
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.size(32.dp))

        if (currentTab.value == Tabs.INQUIRY) {
            InquiryForm(
                title = title.value,
                onTitleChange = { title.value = it },
                content = content.value,
                onContentChange = { content.value = it },
                onSave = {
                    title.value = ""
                    content.value = ""
                    onBackButtonPressed()
                }
            )
        } else {
            InquiryHistory()
        }


        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .wrapContentHeight(unbounded = true, align = Alignment.Bottom),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.logo_transparant),
            contentDescription = "transparent logo"
        )
    }
}

@Composable
fun InquiryForm(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    viewModel: InquiryViewModel = viewModel(
        factory = InquiryViewModel.InquiryViewModelFactory(LocalContext.current)
    )
){
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        FieldLabel(text = "제목", required = true)
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("제목을 입력하세요") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = lightSelector,
                focusedContainerColor = lightSelector,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = hintColor
            ),
            modifier = Modifier
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(32.dp))

        FieldLabel(text = "내용", required = true)
        TextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("내용을 입력하세요") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = lightSelector,
                focusedContainerColor = lightSelector,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = hintColor,
                focusedLabelColor = hintColor
            ),
            singleLine = false,
            modifier = Modifier
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(200.dp)
        )

        error?.let { errorMsg ->
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.size(32.dp))

        Button(
            onClick = {
                viewModel.createInquiry(
                    title = title,
                    content = content,
                    onSuccess = onSave
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "완료", color = Color.White)
            }
        }
    }
}
@Composable
fun InquiryHistory(
    viewModel: InquiryViewModel = viewModel(
        factory = InquiryViewModel.InquiryViewModelFactory(LocalContext.current)
    )
) {
    val inquiries by viewModel.inquiries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        inquiries.forEach { inquiry ->
            NoticeItem(
                title = inquiry.title ?: "-",           // null일 경우 "-" 표시
                date = inquiry.regdate ?: "-",          // null일 경우 "-" 표시
                content = inquiry.content ?: "내용 없음"   // null일 경우 "내용 없음" 표시
            )
        }
    }


}
