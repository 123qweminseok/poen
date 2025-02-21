package com.lodong.poen.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothModel(private val context: Context) {
    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted
    companion object {
        const val PERMISSION_REQUEST_CODE = 100

        // 모든 권한이 허용되었는지 확인하는 편의 메서드
    }


    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }
    /**
     * 위치 서비스가 비활성화되어 있다면 설정 화면을 열어 사용자에게 활성화를 유도합니다.
     */
    fun requestEnableLocationService(activity: Activity) {
        if (!isLocationServiceEnabled()) {
            Toast.makeText(context, "위치 서비스가 꺼져 있습니다. 설정에서 위치 서비스를 켜주세요.", Toast.LENGTH_LONG).show()
            // 위치 설정 화면으로 이동
            activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }


    // 필요한 권한 목록
    private val permissions = mutableListOf<String>().apply {
        // 기본 블루투스 권한 (모든 버전)
        add(Manifest.permission.BLUETOOTH)
        add(Manifest.permission.BLUETOOTH_ADMIN)

        // Android 6.0 이상의 위치 권한 (블루투스 스캔에 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Android 12 이상 블루투스 권한
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
    }

    // 권한 체크
    fun checkPermissions(): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }.also { _permissionGranted.value = it }
    }

    // 권한 요청
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }


    // 권한 결과 처리

    // 블루투스 활성화 체크
    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }

    // 블루투스 활성화 요청
// 블루투스 활성화 요청
    fun enableBluetooth(): Boolean {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false

            // 권한 체크 먼저 수행
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }

            return if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
                true
            } else true

        } catch (e: SecurityException) {
            // 권한이 없는 경우 처리
            showPermissionDeniedMessage()
            return false
        } catch (e: Exception) {
            // 기타 예외 처리
            return false
        }
    }
    // 설정 화면으로 이동
    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(this)
        }
    }

    // 권한 거부 메시지
    public fun showPermissionDeniedMessage() {
        Toast.makeText(
            context,
            "설정-애플리케이션 정보-권한-모두 허용",
            Toast.LENGTH_LONG
        ).show()
    }

}