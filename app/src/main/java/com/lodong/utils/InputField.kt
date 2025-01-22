package com.lodong.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
@Composable
fun InputField(
    label: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        label()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp) // SelectorField와 동일한 높이
                .background(lightSelector, RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart // 텍스트 정렬
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(text = placeholder, color = Color.Gray) },
                modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp),
                singleLine = true,
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    errorTextColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    cursorColor = primaryColor,
                    errorCursorColor = Color.Red,
                    selectionColors = TextSelectionColors(
                        handleColor = primaryColor,
                        backgroundColor = primaryColor.copy(alpha = 0.4f)
                    ),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp) // 텍스트 크기 맞춤
            )
        }
    }
}


@Composable
fun SelectorField(
    label: @Composable () -> Unit,
    selections: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    isInputField: Boolean = false // 기본값 false로 설정
) {
    Column {
        label()
        if (isInputField) {
            // 사용자가 직접 입력할 수 있는 텍스트 필드
            TextField(
                value = selected,
                onValueChange = { onSelected(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                placeholder = { Text("입력해주세요") }
            )
        } else {
            // 선택 가능한 드롭다운 메뉴
            DropdownMenuField(
                selections = selections,
                selected = selected,
                onSelected = onSelected
            )
        }
    }
}
@Composable
fun DropdownMenuField(
    selections: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Text(
            text = selected.ifEmpty { "선택해주세요" }, // 기본값 제공
            modifier = Modifier.padding(8.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            selections.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onSelected(item)
                        expanded = false
                    },
                    text = { Text(text = item) } // 텍스트를 명시적으로 설정
                )
            }
        }
    }
}