package com.lodong.utils

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lodong.poen.ui.theme.lightSelector
import java.util.Calendar

@Composable
fun InputFieldWithDatePicker(
    label: @Composable () -> Unit,
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String = "YYYY-MM-DD"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // DatePickerDialog 생성
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        label()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(lightSelector, RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { datePickerDialog.show() } // 필드 클릭 시 날짜 선택기 표시
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (value.isNotEmpty()) value else placeholder,
                color = if (value.isNotEmpty()) Color.Black else Color.Gray
            )
        }
    }
}

