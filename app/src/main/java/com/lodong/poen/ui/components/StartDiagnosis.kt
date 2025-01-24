package com.lodong.poen.ui.components

import BluetoothViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StartDiagnosis(
    context: Context,
    bluetoothViewModel: BluetoothViewModel,
    pairedDevice: BluetoothViewModel.DeviceWithStatus
) {
    val progress = bluetoothViewModel.diagnosisProgress.collectAsState().value //BluetoothViewModel의 diagnosisProgress StateFlow를 가져온거임. 이 클래스에서 해주고 있기 때문에
    val diagnosisMessage = remember { mutableStateOf("진단 중입니다") }
    val dots = remember { mutableStateOf("") }
    val shouldStartDiagnosis = remember { mutableStateOf(false) }
    val logs = remember { mutableStateOf<List<String>>(emptyList()) }



    // BLE 데이터 리스너 설정 이 부분이 이제 화면에 나오는거지 ㅇㅇ
    LaunchedEffect(Unit) {
        bluetoothViewModel.setBLEDataListener { data ->
            val hexString = data.joinToString(" ") { "%02X".format(it) }
            val dataCount = bluetoothViewModel.getOriginalDataListSize()
            logs.value = logs.value + listOf(
                "[${System.currentTimeMillis()}] 원본 데이터(${dataCount}번째): $hexString",
                "[${System.currentTimeMillis()}] 누적된 원본 데이터 개수: $dataCount"
            )
        }
    }

    // 기존 로그 추가 함수
    fun addLog(message: String) {
        logs.value = logs.value + "[${System.currentTimeMillis()}] $message"
    }


    // 애니메이션 효과: 점(...)을 순환적으로 추가
    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0..3) {
                dots.value = ".".repeat(i)
                delay(500)
            }
        }
    }

    // 진단 준비 완료 후 진단 시작 플래그 설정
    LaunchedEffect(Unit) {
        delay(500)
        shouldStartDiagnosis.value = true
        addLog("진단 시작 준비 완료")
    }

    //진단 상태에 따른 동작     // GATT 연결 및 데이터 전송 처리
    LaunchedEffect(shouldStartDiagnosis.value) {
        if (shouldStartDiagnosis.value) {
            val gatt = pairedDevice.gatt
            if (gatt != null) {
                try {
                    addLog("GATT 연결 확인됨")
                    val serviceUUID = bluetoothViewModel.serviceUUID
                    val characteristicUUID = bluetoothViewModel.characteristicUUID

                    addLog("데이터 전송 시작")
                    bluetoothViewModel.sendDataToDevice(
                        gatt = gatt,
                        serviceUUID = serviceUUID,
                        characteristicUUID = characteristicUUID,
                        data = "StartDiagnosis".toByteArray(Charsets.UTF_8)
                    )
//                    addLog("데이터 전송 완료")

                    when {
                        progress < 1.0f -> {
                            diagnosisMessage.value = "진단 중입니다... (${(progress * 100).toInt()}%)"
//                            addLog("진행률: ${(progress * 100).toInt()}%")
                        }
                        progress >= 1.0f -> {
                            diagnosisMessage.value = "진단이 완료되었습니다."
                        }
                    }
                } catch (e: Exception) {
                    diagnosisMessage.value = "진단 중 오류가 발생했습니다: ${e.message}"
                    addLog("오류 발생: ${e.message}")
                }
            } else {
                diagnosisMessage.value = "GATT 연결이 없습니다."
                addLog("GATT 연결 없음")
            }
        }
    }


    // 진단 진행률에 따라 메시지와 로그 업데이트
    LaunchedEffect(progress) {
        if (progress < 1.0f) {
            diagnosisMessage.value = "진단 중입니다"
//            addLog("진행 중: ${(progress * 100).toInt()}%")
        } else if (progress >= 1.0f) {
            diagnosisMessage.value = "진단이 완료되었습니다"
            dots.value = ""
        }
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ화면 나누는 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 상단 절반: 진단 상태 표시
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (progress < 1.0f) {
                    "${diagnosisMessage.value}${dots.value}"
                } else {
                    diagnosisMessage.value
                },
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            if (progress < 1.0f) {
                Text(
                    text = "(${(progress * 100).toInt()}%)",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .padding(vertical = 16.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE8F5E9)
            )
        }

        // 하단 절반: 로그 표시
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "로그",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(8.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(logs.value) { log ->
                    Text(
                        text = log,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }

//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ여기까지 이제 로그 띄우는부분임ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    }
}