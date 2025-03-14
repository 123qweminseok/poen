package com.lodong.poen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.lodong.poen.R
import com.lodong.poen.ui.theme.buttonColor
import com.lodong.poen.ui.theme.hintColor
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter

@Composable
fun Header(text: String, onBackButtonPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(16.dp))
            Image(painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "back",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onBackButtonPressed() })
            Spacer(modifier = Modifier.weight(1f))
        }
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LeafImageButton(text: String, onClick: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    val buttonColor = if (isPressed.value) primaryColor else primaryLight


    Box(
        modifier = Modifier
            .border(2.dp, primaryLight, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp)
    ) {
        Button(
            onClick = onClick,
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.leaf),
                    contentDescription = "leaf"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = text, color = Color.White)
            }
        }
    }
}

@Composable
fun FieldLabel(text: String, required: Boolean = false) {
    Row {
        Text(modifier = Modifier.padding(start = 8.dp), text = text)
        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "*", color = Color.Red)
        }
    }
}

@Composable
fun SelectorField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = lightSelector,
    label: @Composable () -> Unit,
    selections: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        label()
        val expanded = remember {
            mutableStateOf(false)
        }
        val textFieldSize = remember { mutableStateOf(Size.Zero) }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .clickable { expanded.value = true }
            .border(1.dp, borderColor, shape = RoundedCornerShape(4.dp))
            .onGloballyPositioned { coordinates ->
                textFieldSize.value = coordinates.size.toSize()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = selected)
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.chevron_down),
                contentDescription = "chevron down",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
        }
        DropdownMenu(modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.value.width.toDp() }),
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }) {
            selections.forEach { selection ->
                DropdownMenuItem(onClick = { onSelected(selection);expanded.value = false },
                    text = { Text(text = selection) })
            }
        }
    }
}

@Composable
fun SettingsHeader(text: String, onBackButtonPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackButtonPressed() },
            painter = painterResource(id = R.drawable.chevron_left),
            contentDescription = "back",
            colorFilter = ColorFilter.tint(Color.Black)
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(text = text, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SettingsCategory(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryLighter)
            .padding(start = 36.dp, top = 20.dp, bottom = 20.dp),
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black
    )
}

@Composable
fun SettingsEntry(text: String, textColor: Color = Color.Black, onClick: () -> Unit = {}) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 12.dp)
                .clickable { onClick() },
        ) {
            Text(
                text = text, fontSize = 16.sp, color = textColor
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { scaleX = -1f },
                colorFilter = ColorFilter.tint(Color.Black),
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "go"
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth(), color = lightSelector, thickness = 2.dp
        )
    }
}

@Composable
fun Popup(text: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .background(primaryColor, shape = RoundedCornerShape(8.dp))
            .size(320.dp, 200.dp), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.padding(8.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.x),
                    contentDescription = "close",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                        .clickable { onDismiss() }
                )
            }
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.leaf),
                    contentDescription = "leaf",
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = 12.dp.toPx()
                        }
                        .size(24.dp))
                Text(text = text, color = Color.White)
            }
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff594f48)),
                shape = RectangleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "확인", color = Color.White)
            }
        }
    }
}

@Composable
fun CustomBrownButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun CustomBrownButton2(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    val buttonColor = Color(0xFF795548)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .width(120.dp)
            .heightIn(min = 48.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            maxLines = 2,  // 최대 2줄까지 표시
            softWrap = true,  // 자동 줄바꿈 활성화
            overflow = TextOverflow.Visible,  // 텍스트가 잘리지 않도록 설정
            modifier = Modifier.fillMaxWidth()
        )
    }
}















@Composable
fun InfoInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    singleLine : Boolean = true,
    borderColor: Color = primaryLighter,
    backgroundColor: Color = lightSelector,
    enabled: Boolean = true // 추가

) {
    val isFocused = remember { mutableStateOf(false) }
    val finalBackgroundColor = if (enabled) backgroundColor else Color.LightGray.copy(alpha = 0.4f)
    val finalBorderColor = if (enabled) borderColor else Color.Gray
    val textColor = if (enabled) Color.Black else Color.Gray

    Surface(
        modifier = modifier
            .border(1.dp, finalBorderColor, shape = RoundedCornerShape(4.dp))
            .onFocusChanged { isFocused.value = it.isFocused },
        shape = RoundedCornerShape(4.dp),
        color = finalBackgroundColor
    )
    {
        BasicTextField(
            value = value,
            // enabled가 false라면 onValueChange를 막아서 수정 불가능하게 처리
            onValueChange = {
                // enabled == false일 때는 입력값 변경 차단
                if (enabled) {
                    onValueChange(it)
                }
            },
            // enabled가 false이면 readOnly를 true로 설정
            readOnly = !enabled,
            singleLine = singleLine,
            cursorBrush = SolidColor(Color.Black),
        ) { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .let { if (!singleLine) it.padding(top = 16.dp) else it },
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
            ) {
                if (value.isEmpty() && !isFocused.value) {
                    Text(text = hint, color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                } else {
                    innerTextField()
                }
            }
        }
    }
}