package com.lodong.poen
import android.Manifest  // 추가
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
import com.lodong.poen.repository.BatteryInfoRepository
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.repository.SignUpRepository
import com.lodong.poen.service.BluetoothForegroundService
import com.lodong.poen.ui.navigation.Navigation
import com.lodong.poen.ui.screens.BluetoothModel
import com.lodong.poen.ui.screens.LoadingScreen


class MainActivity : ComponentActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100  // 여기에 상수 추가
    }

    private lateinit var bluetoothService: BluetoothForegroundService
    private lateinit var binaryBleRepository: BinaryBleRepository
    private lateinit var signUpRepository: SignUpRepository
    private var isServiceBound = mutableStateOf(false)
    private lateinit var bluetoothModel: BluetoothModel




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
        val preferencesHelper = PreferencesHelper.getInstance(this)
        val batteryInfoRepository = BatteryInfoRepository(preferencesHelper)


        bluetoothModel = BluetoothModel(this)
        // 권한 체크 및 요청
        if (!bluetoothModel.checkPermissions()) {
            bluetoothModel.requestPermissions(this)
        }




        binaryBleRepository = BinaryBleRepository(this)
        signUpRepository = SignUpRepository(preferencesHelper) // preferencesHelper 전달

        startAndBindService()





        setContent {
            // Observe isServiceBound to decide what to show
            if (isServiceBound.value) {
                Navigation(
                    bluetoothService = bluetoothService,
                    binaryBleRepository = binaryBleRepository,
                    signUpRepository = signUpRepository ,
                    batteryInfoRepository = batteryInfoRepository // 전달


                )
            } else {
                LoadingScreen() // 서비스 초기화 중일 때 표시
            }
        }



    }


    //권한 허용되어있는지 체크
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 블루투스 관련 권한만 체크
            val bluetoothPermissions = permissions.mapIndexed { index, permission ->
                permission to grantResults[index]
            }.filter { (permission, _) ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    permission == Manifest.permission.BLUETOOTH ||
                            permission == Manifest.permission.BLUETOOTH_ADMIN ||
                            permission == Manifest.permission.BLUETOOTH_SCAN ||
                            permission == Manifest.permission.BLUETOOTH_CONNECT ||
                            permission == Manifest.permission.BLUETOOTH_ADVERTISE
                } else {
                    permission == Manifest.permission.BLUETOOTH ||
                            permission == Manifest.permission.BLUETOOTH_ADMIN
                }
            }

            // 블루투스 권한이 하나라도 있는 경우에만 체크
            if (bluetoothPermissions.isNotEmpty()) {
                val allBluetoothGranted = bluetoothPermissions.all { (_, result) ->
                    result == PackageManager.PERMISSION_GRANTED
                }

                if (allBluetoothGranted) {
                    // 블루투스 권한이 모두 승인된 경우
                    Log.d("MainActivity", "All Bluetooth permissions granted")
                    // 필요한 경우 여기에 추가 초기화 로직
                } else {
                    // 블루투스 권한 중 일부가 거부된 경우
                    Log.w("MainActivity", "Some Bluetooth permissions denied")
                    bluetoothModel.showPermissionDeniedMessage()
                }
            }
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