package com.lodong.poen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lodong.poen.R
import com.lodong.poen.ui.SettingsCategory
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.viewmodel.NoticesViewModel
import kotlinx.coroutines.launch

@Composable
fun NoticeScreen(
    onBackButtonPressed: () -> Unit,
    viewModel: NoticesViewModel = viewModel(
        factory = NoticesViewModel.NoticesViewModelFactory(LocalContext.current)
    )
) {
    val notices by viewModel.notices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        SettingsHeader(text = "공지사항", onBackButtonPressed = onBackButtonPressed)
        SettingsCategory(text = "")

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
        ) {
            notices.forEach { notice ->
                NoticeItem(
                    noticeId = notice.noticeId,
                    title = notice.title,
                    date = formatDate(notice.regDate),
                    viewModel = viewModel
                )
            }        }


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

private fun formatDate(dateString: String): String {
    return try {
        if (dateString.contains("T")) {
            dateString.split("T")[0]
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}


@Composable
fun NoticeItem(
    noticeId: String,
    title: String,
    date: String,
    viewModel: NoticesViewModel,
    content: String? = null
) {
    val scope = rememberCoroutineScope()
    val isExpanded = remember { mutableStateOf(false) }
    val selectedContent by viewModel.selectedNoticeContent.collectAsState()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded.value = !isExpanded.value
                    if (isExpanded.value) {
                        scope.launch {
                            viewModel.getNoticeDetail(noticeId)
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    maxLines = if (isExpanded.value) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = date, fontSize = 12.sp)
            }
            Image(
                painter = painterResource(id = R.drawable.chevron_down),
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (isExpanded.value) 0f else -90f
                },
                contentDescription = "expand"
            )
        }
        if (isExpanded.value) {
            if (viewModel.isLoading.collectAsState().value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = selectedContent ?: "내용을 불러오는 중입니다...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )
    }
}