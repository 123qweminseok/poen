package com.lodong.poen.ui.screens

import BluetoothViewModel
import PreferencesHelper
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.lodong.poen.R
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.components.StartDiagnosis
import com.lodong.poen.ui.theme.primaryLight
@Composable
fun DiagnoseScreen(
    context: Context,
    onBackButtonPressed: () -> Unit,
    bluetoothViewModel: BluetoothViewModel,
    preferencesHelper: PreferencesHelper,
    navController: NavController  // NavController 추가

) {
    val batteryId = preferencesHelper.getBatteryInfo()["battery_id"]
    val devices = bluetoothViewModel.devices.collectAsState().value
    val bondedDevices = bluetoothViewModel.bondedDevices.collectAsState().value
    val pairedDevice = devices.find { it.status is BluetoothViewModel.PairingStatus.Success }
    val progress = bluetoothViewModel.diagnosisProgress.collectAsState().value

    // 기존 로깅은 유지하되 비동기로 처리
    LaunchedEffect(devices, bondedDevices) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            Log.d("DiagnoseScreen", "Devices: ${devices.map { it.device.address to it.status }}")
            Log.d("DiagnoseScreen", "Paired Device: $pairedDevice")
            Log.d("DiagnoseScreen", "Battery ID: $batteryId")
            Log.d(
                "DiagnoseScreen",
                "Bonded Devices: ${bondedDevices.joinToString { "${it.name} (${it.address})" }}"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Header(text = "진단", onBackButtonPressed = onBackButtonPressed)

        when {
            batteryId.isNullOrEmpty() -> {
                Text(
                    text = "배터리 정보를 먼저 입력해주세요.",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            pairedDevice == null -> {
                Text(
                    text = "페어링된 Bluetooth 기기가 없습니다. 먼저 페어링해주세요.",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 진행률 표시
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = primaryLight
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // StartDiagnosis를 LaunchedEffect로 감싸서 즉시 진행
                    LaunchedEffect(pairedDevice) {
                        bluetoothViewModel.startDiagnosis()
                    }

                    StartDiagnosis(
                        context = context,
                        bluetoothViewModel = bluetoothViewModel,
                        pairedDevice = pairedDevice,
                        navController = navController  // NavController 전달

                    )
                }
            }
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