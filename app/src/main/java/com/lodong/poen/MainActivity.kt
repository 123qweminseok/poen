package com.lodong.poen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.repository.SignUpRepository
import com.lodong.poen.service.BluetoothForegroundService
import com.lodong.poen.ui.navigation.Navigation
import com.lodong.poen.ui.screens.LoadingScreen


class MainActivity : ComponentActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100  // 여기에 상수 추가
    }

    private lateinit var bluetoothService: BluetoothForegroundService
    private lateinit var binaryBleRepository: BinaryBleRepository
    private lateinit var signUpRepository: SignUpRepository
    private var isServiceBound = mutableStateOf(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothForegroundService.LocalBinder
            bluetoothService = binder.getService()
            isServiceBound.value = true
            Log.d("MainActivity", "BluetoothForegroundService connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound.value = false
            Log.w("MainActivity", "BluetoothForegroundService disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binaryBleRepository = BinaryBleRepository(this)
        signUpRepository = SignUpRepository()
        startAndBindService()

        checkAndRequestPermissions()



        setContent {
            // Observe isServiceBound to decide what to show
            if (isServiceBound.value) {
                Navigation(
                    bluetoothService = bluetoothService,
                    binaryBleRepository = binaryBleRepository,
                    signUpRepository = signUpRepository
                )
            } else {
                LoadingScreen() // 서비스 초기화 중일 때 표시
            }
        }



    }




    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 안드로이드 12 이상
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).also { // 권한 목록 로깅 추가
                Log.d("MainActivity", "Requesting permissions for Android 12+: ${it.joinToString()}")
            }
        } else {
            // 안드로이드 11 이하
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).also {
                Log.d("MainActivity", "Requesting permissions for Android 11-: ${it.joinToString()}")
            }
        }

        val missingPermissions = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissions(
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startAndBindService()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound.value) {
            unbindService(serviceConnection)
            isServiceBound.value = false
            Log.d("MainActivity", "Service unbound on destroy")
        }
    }

    private fun startAndBindService() {
        val serviceIntent = Intent(this, BluetoothForegroundService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }




}