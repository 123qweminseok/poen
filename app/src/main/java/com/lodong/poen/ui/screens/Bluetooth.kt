package com.lodong.poen.ui.screens

import BluetoothViewModel
import android.Manifest
import android.annotation.SuppressLint
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
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != android.app.Activity.RESULT_OK) {
            Toast.makeText(context, "블루투스를 활성화하지 않으면 작동하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    // 블루투스 권한 요청 (API 31 이상)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
            ActivityCompat.requestPermissions(context as android.app.Activity, permissions, 1)
        }
    }

    LaunchedEffect(bluetoothSwitchState.value) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothSwitchState.value) {
            if (bluetoothAdapter?.isEnabled == false) {
                // 블루투스 활성화 요청
//                Toast.makeText(context, "블루투스를 켜야 작동합니다.", Toast.LENGTH_SHORT).show()
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothEnableLauncher.launch(enableBtIntent)
            }
        }
    }

    // 화면 진입 시 초기화
    LaunchedEffect(Unit) {
        if (!bluetoothSwitchState.value) {
        }
    }

    // 스위치 상태에 따른 스캔 처리
    LaunchedEffect(bluetoothSwitchState.value) {
        if (bluetoothSwitchState.value) {
            // 권한 확인
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("BluetoothScreen", "BLUETOOTH permissions are missing")
                // 권한 요청
                ActivityCompat.requestPermissions(
                    context as android.app.Activity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    1
                )
                return@LaunchedEffect // 권한 없으면 BLE 스캔 중단
            }



            // BLE 스캔 시작
            bluetoothViewModel.stopBleScan() // 기존 스캔 중단
            bluetoothViewModel.startBleScan() // 새 스캔 시작

            // 30초마다 스캔 갱신
            while (bluetoothSwitchState.value) {
                delay(30000) // 30초 대기
                bluetoothViewModel.stopBleScan()
                delay(500) // 잠시 대기 후 다시 시작
                bluetoothViewModel.startBleScan()
            }
        } else {
            bluetoothViewModel.stopBleScan()
            bluetoothViewModel.clearDevices()
        }
    }


    LaunchedEffect(Unit) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothSwitchState.value && bluetoothAdapter?.isEnabled == false) {
            // 스위치는 켜져있는데 블루투스가 꺼져있는 경우
            Toast.makeText(context, "휴대전화 블루투스를 켜주고 실행해주세요", Toast.LENGTH_SHORT).show()
            bluetoothAdapter.enable() // 블루투스 자동 활성화
        }
    }

    // 블루투스 상태 변경 감지
    LaunchedEffect(bluetoothSwitchState.value) {
        while (bluetoothSwitchState.value) {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (!bluetoothAdapter?.isEnabled!!) {
                Toast.makeText(context, "휴대전화 블루투스를 켜주고 실행해주세요", Toast.LENGTH_SHORT).show()
                bluetoothAdapter.enable()
            }
            delay(1000) // 1초마다 체크
        }
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
//                            Toast.makeText(context, "블루투스가 꺼져있습니다. 블루투스를 켜주세요", Toast.LENGTH_SHORT).show()
                            bluetoothSwitchState.value = false
                            bluetoothViewModel.clearDevices()
                        }
                    } else {
                        bluetoothSwitchState.value = false
                        preferencesHelper.putBoolean("bluetooth_switch_state", false)
                        bluetoothViewModel.stopBleScan()
                        bluetoothViewModel.clearDevices()
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
                                        if (deviceWithStatus.status != BluetoothViewModel.PairingStatus.Loading) {
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