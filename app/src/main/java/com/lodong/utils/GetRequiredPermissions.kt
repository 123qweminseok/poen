// PermissionUtils.kt
package com.lodong.poen.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Build

fun getRequiredPermissions(): Array<String> {
    val permissions = mutableListOf<String>()

    // BLE 광고 지원 여부 확인 함수
    fun isBluetoothAdvertiseSupported(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.bluetoothLeAdvertiser != null
    }

    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14+
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            if (isBluetoothAdvertiseSupported()) {
                permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { // Android 12+
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            if (isBluetoothAdvertiseSupported()) {
                permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> { // Android 6+
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION) // 위치 권한 필요
        }
        else -> {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Android 9+
        permissions.add(Manifest.permission.FOREGROUND_SERVICE)
    }

    permissions.add(Manifest.permission.CAMERA) // 카메라 권한 추가

    return permissions.toTypedArray()
}
