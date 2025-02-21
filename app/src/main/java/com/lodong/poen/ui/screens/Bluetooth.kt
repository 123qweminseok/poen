package com.lodong.poen.ui.screens

import BluetoothViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.lodong.bluetooth_module.BLEServerManager
import com.lodong.poen.R
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.components.DeviceItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen(
    bluetoothViewModel: BluetoothViewModel,
    onBackButtonPressed: () -> Unit
) {
    val devices by bluetoothViewModel.devices.collectAsState()
    val preferencesHelper = bluetoothViewModel.preferencesHelper
    val context = LocalContext.current

    val bluetoothSwitchState = remember {
        mutableStateOf(preferencesHelper.getBoolean("bluetooth_switch_state", false))
    }
    val bluetoothModel = remember { BluetoothModel(context) }
    val permissionGranted by bluetoothModel.permissionGranted.collectAsState()
    var showEnableBluetoothDialog by remember { mutableStateOf(false) }


// 블루투스 활성화 요청 표시 여부를 추적하는 상태
    var hasShownBluetoothRequest by remember { mutableStateOf(false) }

// 블루투스 활성화 요청에 대한 결과 처리 런처
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        hasShownBluetoothRequest = true  // 요청을 표시했음을 표시
        if (result.resultCode != Activity.RESULT_OK) {
            Toast.makeText(context, "블루투스를 활성화하지 않으면 작동하지 않습니다.", Toast.LENGTH_SHORT).show()
            bluetoothSwitchState.value = false
            preferencesHelper.putBoolean("bluetooth_switch_state", false)
        }
    }



    // 초기 권한 체크
    LaunchedEffect(Unit) {
        if (!bluetoothModel.checkPermissions()) {
            bluetoothModel.requestPermissions(context as Activity)
            bluetoothSwitchState.value = false
            preferencesHelper.putBoolean("bluetooth_switch_state", false)
        }
    }




    // 화면 진입 시 초기화
    LaunchedEffect(Unit) {
        if (!bluetoothSwitchState.value) {
        }
    }



    // 스위치 상태에 따른 스캔 처리
    LaunchedEffect(bluetoothSwitchState.value, permissionGranted) {
        if (bluetoothSwitchState.value) {
            // 권한 확인
            if (!permissionGranted) {
                bluetoothModel.requestPermissions(context as Activity)
                return@LaunchedEffect
            }

            // 블루투스 상태 확인
            if (!bluetoothModel.isBluetoothEnabled()) {
                if (!bluetoothModel.enableBluetooth()) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothEnableLauncher.launch(enableBtIntent)
                    return@LaunchedEffect
                }
            }

            // BLE 스캔 시작
//            bluetoothViewModel.stopBleScan()
            // 즉시 첫 스캔 시작
            bluetoothViewModel.startBleScan()

            // 더 짧은 주기로 스캔 갱신
            while (bluetoothSwitchState.value) {
                delay(3000)  // 이것 짧으면 안됨. 하나의 스캔 길이임 . 패킷 길이를 그래야 다 받음.
                if (!bluetoothSwitchState.value) break

                bluetoothViewModel.stopBleScan()
                delay(300)   // 아주 짧은 대기
                bluetoothViewModel.startBleScan()
            }
        } else {
            bluetoothViewModel.stopBleScan()
            bluetoothViewModel.clearDevices()
        }
    }





    // 블루투스 초기 설정을 위한 단일 LaunchedEffect
    LaunchedEffect(Unit) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // 권한 체크
        if (!bluetoothModel.checkPermissions()) {
            bluetoothModel.requestPermissions(context as Activity)
            bluetoothSwitchState.value = false
            preferencesHelper.putBoolean("bluetooth_switch_state", false)
            return@LaunchedEffect
        }

        // 블루투스가 꺼져있고 스위치가 켜져있거나, 둘 다 꺼져있는 경우
        if (!bluetoothAdapter?.isEnabled!!) {
            showEnableBluetoothDialog = true
        }
    }

    // 스위치 상태에 따른 스캔 처리
    LaunchedEffect(bluetoothSwitchState.value, permissionGranted) {
        if (bluetoothSwitchState.value) {
            // 권한 확인
            if (!permissionGranted) {
                bluetoothModel.requestPermissions(context as Activity)
                return@LaunchedEffect
            }

            // BLE 스캔 시작
            bluetoothViewModel.stopBleScan()
            bluetoothViewModel.startBleScan()

            // 주기적 스캔 갱신
            while (bluetoothSwitchState.value && permissionGranted) {
                delay(20000)
                if (!bluetoothSwitchState.value) break

                bluetoothViewModel.stopBleScan()
                delay(100)

                if (bluetoothModel.checkPermissions() && bluetoothModel.isBluetoothEnabled()) {
                    bluetoothViewModel.startBleScan()
                } else {
                    break
                }
            }
        } else {
            bluetoothViewModel.stopBleScan()
            bluetoothViewModel.clearDevices()
        }
    }

    if (showEnableBluetoothDialog) {
        AlertDialog(
            onDismissRequest = {
                showEnableBluetoothDialog = false
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bluetooth),
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "블루투스 활성화",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "블루투스 기능을 사용하기 위해 블루투스를 켜시겠습니까?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEnableBluetoothDialog = false
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothEnableLauncher.launch(enableBtIntent)
                        bluetoothSwitchState.value = true
                        preferencesHelper.putBoolean("bluetooth_switch_state", true)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        "확인",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEnableBluetoothDialog = false
                        Toast.makeText(
                            context,
                            "블루투스를 활성화하지 않으면 기능이 제한됩니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text("취소")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Header(text = "Bluetooth", onBackButtonPressed = onBackButtonPressed)
        Spacer(modifier = Modifier.size(16.dp))






        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = Color(0xFFF1F8F1),  // 연한 민트색 배경
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),  // 내부 패딩 추가
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bluetooth),  // 블루투스 아이콘 추가
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Bluetooth",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)  // 진한 초록색
                )
            }




            Switch(
                checked = bluetoothSwitchState.value,
                onCheckedChange = { isChecked ->
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (isChecked) {
                        if (bluetoothAdapter?.isEnabled == true) {
                            bluetoothSwitchState.value = true
                            preferencesHelper.putBoolean("bluetooth_switch_state", true)
                        } else {
                            bluetoothSwitchState.value = false
                            bluetoothViewModel.clearDevices()
                        }
                    } else {
                        try {
                            bluetoothSwitchState.value = false
                            preferencesHelper.putBoolean("bluetooth_switch_state", false)

                            // GATT 연결 해제 (버전별 처리)
                            bluetoothViewModel.service.bleManager.currentGatt?.let { gatt ->
                                when {
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                                        if (ActivityCompat.checkSelfPermission(context,
                                                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                            gatt.disconnect()
                                            gatt.close()
                                        }
                                    }
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                                        if (ActivityCompat.checkSelfPermission(context,
                                                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                                            gatt.disconnect()
                                            gatt.close()
                                        }
                                    }
                                    else -> {
                                        // Android 10 이하
                                        gatt.disconnect()
                                        gatt.close()
                                    }
                                }
                            }

                            // 스캔 중지 및 데이터 초기화
                            bluetoothViewModel.stopBleScan()
                            bluetoothViewModel.service.bleManager.resetAllData()
                            bluetoothViewModel.service.bleManager.resetServerData()
                            bluetoothViewModel.service.bleManager.resetOriginalDataList()
                            bluetoothViewModel.clearDevices()

                        } catch (e: Exception) {
                            Log.e("BluetoothScreen", "Error during cleanup: ${e.message}")
                        }
                    }

                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )


        }

//해당 부분이 이제 스캔후 띄우는 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ걍 배열에 있는거 가져오는거임 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        if (bluetoothSwitchState.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (devices.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "기기 감지중...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        itemsIndexed(devices) { _, deviceWithStatus ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable {
                                        // 이미 연결된 경우 disconnectDevice를 호출합니다.
                                        if (deviceWithStatus.status is BluetoothViewModel.PairingStatus.Success) {
                                            bluetoothViewModel.disconnectDevice(context, deviceWithStatus.device)
                                        }
                                        // 로딩 중이 아닌 경우에만 연결 시도합니다.
                                        else if (deviceWithStatus.status !is BluetoothViewModel.PairingStatus.Loading) {
                                            val serviceUUID = BLEServerManager.DEFAULT_SERVICE_UUID
                                            val characteristicUUID = BLEServerManager.DEFAULT_CHARACTERISTIC_UUID
                                            val dataToSend = "Data for ${deviceWithStatus.device.name}".toByteArray(Charsets.UTF_8)
                                            bluetoothViewModel.connectToDevice(
                                                context = context,
                                                device = deviceWithStatus.device,
                                                serviceUUID = serviceUUID,
                                                characteristicUUID = characteristicUUID,
                                                dataToSend = dataToSend
                                            )
                                        }
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (deviceWithStatus.status) {
                                        BluetoothViewModel.PairingStatus.Success -> Color(0xFFF1F8F1)
                                        BluetoothViewModel.PairingStatus.Loading -> Color(0xFFFFF8E1)
                                        else -> Color.White
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deviceWithStatus.device.name ?: deviceWithStatus.device.address,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (deviceWithStatus.device.name != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = deviceWithStatus.device.address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when (deviceWithStatus.status) {
                                                    BluetoothViewModel.PairingStatus.Success -> Color(0xFF4CAF50)
                                                    BluetoothViewModel.PairingStatus.Loading -> Color(0xFFFFA000)
                                                    else -> Color(0xFF9E9E9E)
                                                },
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        when (deviceWithStatus.status) {
                                            BluetoothViewModel.PairingStatus.Loading -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                    Text(
                                                        "연결중",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                            BluetoothViewModel.PairingStatus.Success -> {
                                                Text(
                                                    "연결됨",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    "연결 안됨",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }




    }
}