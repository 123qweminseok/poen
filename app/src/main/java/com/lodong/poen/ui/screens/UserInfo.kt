package com.lodong.poen.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.ui.CustomBrownButton
import com.lodong.poen.R
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.InfoInputField
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter

@Composable
fun UserInfoEditScreen(
    isSeller: Boolean = true,
    onBackButtonPressed: () -> Unit
) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
    val labelSize = 96.dp
    val inputSize = 168.dp

    val inputHeight = 40.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsHeader(text = "회원정보 수정", onBackButtonPressed = onBackButtonPressed)
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "아이디", style = labelTextStyle)
                Text(text = "leeddoong", color = Color(0x80262626))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "비밀번호 변경", style = labelTextStyle)
                CustomBrownButton(onClick = {}, text = "비밀번호 변경하기")
            }

            val name = remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "이름", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = name.value,
                    hint = "홍길동",
                    onValueChange = { name.value = it }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "이메일 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "jsh010322@gmail.com",
                    value = "",
                    onValueChange = {})
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(onClick = {}, text = "계정확인")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "전화번호", style = labelTextStyle)
                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(onClick = {}, text = "인증하기")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(labelSize))
                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(onClick = {}, text = "확인")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "주소", style = labelTextStyle)
                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(onClick = {}, text = "우편번호검색")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(labelSize))
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = "",
                    onValueChange = {},
                    hint = "나머지 주소를 입력해 주세요"
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(onClick = {}, text = "확인")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "약관동의", style = labelTextStyle)
                Checkbox(
                    checked = false, onCheckedChange = {},
                    colors = CheckboxDefaults.colors(
                        checkedColor = lightSelector,
                        uncheckedColor = lightSelector
                    ),
                )
                Text(text = "개인정보 수집.이용 동의(선택)", fontSize = 12.sp)
            }

            if (isSeller) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업자 등록번호",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "대표자 성명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "개업일자",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "사업자 확인")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "우편번호검색")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {},
                        hint = "나머지 주소를 입력해 주세요"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "확인")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "계좌번호",
                        style = labelTextStyle
                    )
                    SelectorField(
                        label = {},
                        selections = listOf("국민은행"),
                        selected = "국민은행",
                        borderColor = primaryLighter,
                        backgroundColor = lightSelector,
                        modifier = Modifier.width(inputSize)
                    ) { }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = "",
                        onValueChange = {}
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomBrownButton(onClick = {}, text = "계좌확인")
                }
            }

            Spacer(modifier = Modifier.size(32.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryColor),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "회원탈퇴하기", color = Color.Black)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryLight),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "회원정보수정", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.size(32.dp))

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
}
//
//@Composable
//fun SignUpScreen() {
//    val labelTextStyle = TextStyle(
//        color = Color.Black,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 12.sp
//    )
//    val labelSize = 96.dp
//    val inputSize = 168.dp
//
//    val inputHeight = 40.dp
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        Header(text = "회원가입", onBackButtonPressed = {})
//        Divider(
//            modifier = Modifier
//                .fillMaxWidth(),
//            color = lightSelector,
//            thickness = 2.dp
//        )
//        Column(
//            modifier = Modifier
//                .padding(start = 16.dp)
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(modifier = Modifier.width(labelSize), text = "아이디", style = labelTextStyle)
//                Text(text = "leeddoong", color = Color(0x80262626))
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(modifier = Modifier.width(labelSize), text = "비밀번호 변경", style = labelTextStyle)
//                CustomBrownButton(onClick = {}, text = "비밀번호 변경하기")
//            }
//
//            val name = remember { mutableStateOf("") }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(modifier = Modifier.width(labelSize), text = "이름", style = labelTextStyle)
//                InfoInputField(
//                    modifier = Modifier.width(inputSize),
//                    value = name.value,
//                    hint = "홍길동",
//                    onValueChange = { name.value = it }
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    modifier = Modifier.width(labelSize),
//                    text = "이메일 주소",
//                    style = labelTextStyle
//                )
//                InfoInputField(
//                    modifier = Modifier.width(inputSize),
//                    hint = "jsh010322@gmail.com",
//                    value = "",
//                    onValueChange = {})
//                Spacer(modifier = Modifier.width(8.dp))
//                CustomBrownButton(onClick = {}, text = "계정확인")
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(modifier = Modifier.width(labelSize), text = "전화번호", style = labelTextStyle)
//                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
//                Spacer(modifier = Modifier.width(8.dp))
//                CustomBrownButton(onClick = {}, text = "인증하기")
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Spacer(modifier = Modifier.width(labelSize))
//                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
//                Spacer(modifier = Modifier.width(8.dp))
//                CustomBrownButton(onClick = {}, text = "확인")
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(modifier = Modifier.width(labelSize), text = "주소", style = labelTextStyle)
//                InfoInputField(modifier = Modifier.width(inputSize), value = "", onValueChange = {})
//                Spacer(modifier = Modifier.width(8.dp))
//                CustomBrownButton(onClick = {}, text = "우편번호검색")
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Spacer(modifier = Modifier.width(labelSize))
//                InfoInputField(
//                    modifier = Modifier.width(inputSize),
//                    value = "",
//                    onValueChange = {},
//                    hint = "나머지 주소를 입력해 주세요"
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                CustomBrownButton(onClick = {}, text = "확인")
//            }
//
//            val isSeller = remember { mutableStateOf(false) }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(inputHeight),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    modifier = Modifier.width(labelSize),
//                    text = "구매자/판매자\n관리 선택",
//                    style = labelTextStyle
//                )
//                val radioButtonColor = Color(0xFFBAC784)
//                RadioButton(
//                    modifier = Modifier.size(24.dp),
//                    selected = !isSeller.value,
//                    onClick = { isSeller.value = false },
//                    colors = RadioButtonDefaults.colors(
//                        selectedColor = radioButtonColor,
//                        unselectedColor = radioButtonColor
//                    )
//                )
//                Text(text = "매물정보(구매자)")
//                Spacer(modifier = Modifier.width(16.dp))
//                RadioButton(
//                    modifier = Modifier.size(24.dp),
//                    selected = isSeller.value,
//                    onClick = { isSeller.value = true },
//                    colors = RadioButtonDefaults.colors(
//                        selectedColor = radioButtonColor,
//                        unselectedColor = radioButtonColor
//                    )
//                )
//                Text(text = "진단정보(판매자)")
//            }
//
//            if (isSeller.value) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "사업자 등록번호",
//                        style = labelTextStyle
//                    )
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {})
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "대표자 성명",
//                        style = labelTextStyle
//                    )
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {})
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "사업장명",
//                        style = labelTextStyle
//                    )
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {})
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "개업일자",
//                        style = labelTextStyle
//                    )
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {})
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CustomBrownButton(onClick = {}, text = "사업자 확인")
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "사업장 주소",
//                        style = labelTextStyle
//                    )
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {})
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CustomBrownButton(onClick = {}, text = "우편번호검색")
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(modifier = Modifier.width(labelSize))
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {},
//                        hint = "나머지 주소를 입력해 주세요"
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CustomBrownButton(onClick = {}, text = "확인")
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier.width(labelSize),
//                        text = "계좌번호",
//                        style = labelTextStyle
//                    )
//                    SelectorField(
//                        label = {},
//                        selections = listOf("국민은행"),
//                        selected = "국민은행",
//                        borderColor = primaryLighter,
//                        backgroundColor = lightSelector,
//                        modifier = Modifier.width(inputSize)
//                    ) { }
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(inputHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(modifier = Modifier.width(labelSize))
//                    InfoInputField(
//                        modifier = Modifier.width(inputSize),
//                        value = "",
//                        onValueChange = {}
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CustomBrownButton(onClick = {}, text = "계좌확인")
//                }
//            }
//
//            Spacer(modifier = Modifier.size(32.dp))
//
//            Button(
//                onClick = {},
//                colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
//                shape = RoundedCornerShape(8.dp),
//                border = BorderStroke(1.dp, primaryLight),
//                modifier = Modifier
//                    .height(36.dp)
//                    .align(Alignment.CenterHorizontally)
//            ) {
//                Text(text = "회원가입하기", color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.size(32.dp))
//
//            Image(
//                modifier = Modifier
//                    .padding(bottom = 32.dp)
//                    .fillMaxWidth(),
//                contentScale = ContentScale.FillWidth,
//                painter = painterResource(id = R.drawable.logo_transparant),
//                contentDescription = "transparent logo"
//            )
//        }
//    }
//}

@Composable
@Preview
fun PasswordChangeScreen(
    onBackButtonPressed: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsHeader("비밀번호 변경") {
            onBackButtonPressed()
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )
        Spacer(modifier = Modifier.size(64.dp))
        Text(text = "안녕하세요 홍길동님 POEN입니다.\n새 비밀번호 설정을 완료해 주세요.")
        Spacer(modifier = Modifier.size(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = "",
                onValueChange = {},
                hint = "현재 비밀번호를 입력해 주세요"
            )
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = "",
                onValueChange = {},
                hint = "새로운 비밀번호를 입력해 주세요"
            )
            InfoInputField(
                modifier = Modifier.height(48.dp),
                value = "",
                onValueChange = {},
                hint = "새로운 비밀번호를 한번 더 입력해 주세요"
            )
        }

        Spacer(modifier = Modifier.size(64.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            shape = RectangleShape,
            modifier = Modifier
                .height(36.dp)
                .width(160.dp)
        ) {
            Text(text = "확인", color = Color.White)
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