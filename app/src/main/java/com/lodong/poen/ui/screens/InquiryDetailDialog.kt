package com.lodong.poen.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.lodong.apis.MemberApi.InquiryDetail
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.viewmodel.InquiryViewModel
import androidx.compose.material3.AlertDialogDefaults



@Composable
fun InquiryDetailDialog(
    questionId: String,
    onDismiss: () -> Unit,
    viewModel: InquiryViewModel
) {
    val dialogState = remember { mutableStateOf<InquiryDetail?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }


    val isEditMode = remember { mutableStateOf(false) }

    // 수정을 위한 상태 추가
    val editTitle = remember { mutableStateOf("") }
    val editContent = remember { mutableStateOf("") }

    // 초기 데이터 로드 시 수정용 상태 초기화
    LaunchedEffect(dialogState.value) {
        dialogState.value?.let { detail ->
            editTitle.value = detail.title
            editContent.value = detail.content
        }
    }


    fun formatDate(dateString: String?): String {
        return try {
            if (dateString == null) return "-"
            // 2025-02-07T03:20:00.253Z 형식의 자열을 파싱
            val pattern = if (dateString.contains("T")) {
                dateString.split("T")[0] // T 이전의 날짜 부분만 사용
            } else {
                dateString
            }
            pattern
        } catch (e: Exception) {
            Log.e("InquiryDetailDialog", "날짜 변환 실패", e)
            dateString ?: "-"
        }
    }
    fun getDisplayDate(detail: InquiryDetail?): String {
        return when {
            detail?.regDate != null -> formatDate(detail.regDate)
            detail?.updateDate != null -> formatDate(detail.updateDate)
            else -> "날짜 정보 없음"
        }
    }


    LaunchedEffect(questionId) {
        try {
            val detail = viewModel.getInquiryDetail(questionId)
            dialogState.value = detail
            isLoading.value = false
        } catch (e: Exception) {
            error.value = e.message ?: "상세 내용을 불러오는데 실패했습니다."
            isLoading.value = false
        }
    }



    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),       // 모서리 둥글게
        containerColor = Color.White,            // 다이얼로그 배경을 흰색으로 지정
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White), // 제목 영역도 흰색
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isEditMode.value) {
                    Text(
                        text = dialogState.value?.title ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { isEditMode.value = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정",
                            tint = primaryColor
                        )
                    }
                } else {
                    TextField(
                        value = editTitle.value,
                        onValueChange = { editTitle.value = it },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        },
        text = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White  // 텍스트 영역도 흰색
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    when {
                        isLoading.value -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        error.value != null -> {
                            Text(
                                text = error.value ?: "",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            Column {
                                Text(
                                    text = "문의 내용",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (!isEditMode.value) {
                                    Text(
                                        text = dialogState.value?.content ?: "",
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                } else {
                                    TextField(
                                        value = editContent.value,
                                        onValueChange = { editContent.value = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .padding(bottom = 16.dp),
                                        colors = TextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.White,
                                            focusedContainerColor = Color.White
                                        )
                                    )
                                }

                                Text(
                                    text = "작성일: ${getDisplayDate(dialogState.value)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                dialogState.value?.answers?.firstOrNull()?.let { answer ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "답변",
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = answer.content,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "${formatDate(answer.regdate)}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isEditMode.value) {
                    // 수정 모드일 때의 버튼들
                    TextButton(
                        onClick = { isEditMode.value = false }
                    ) {
                        Text("취소")
                    }
                    TextButton(
                        onClick = {
                            viewModel.updateInquiry(
                                questionId = questionId,
                                title = editTitle.value,
                                content = editContent.value
                            ) {
                                isEditMode.value = false
                                onDismiss()  // 저장 후 다이얼로그 닫기
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = primaryColor)
                    ) {
                        Text("저장")
                    }
                } else {
                    // 조회 모드일 때의 버튼들
                    TextButton(
                        onClick = {
                            viewModel.deleteInquiry(questionId) {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("삭제")
                    }
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = primaryColor)
                    ) {
                        Text("닫기")
                    }
                }
            }
        }
    )
}
