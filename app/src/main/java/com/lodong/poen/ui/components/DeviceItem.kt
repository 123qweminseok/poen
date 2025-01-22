package com.lodong.poen.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(
    context: Context,
    deviceWithStatus: BluetoothViewModel.DeviceWithStatus,
    onItemClick: () -> Unit,
    onSendDataClick: () -> Unit
) {
    val deviceName = deviceWithStatus.device.name ?: deviceWithStatus.device.address ?: "Unknown Device"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable {
                if (deviceWithStatus.status != BluetoothViewModel.PairingStatus.Loading) {
                    onItemClick()
                }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = deviceName, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))

        when (deviceWithStatus.status) {
            BluetoothViewModel.PairingStatus.Loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
            BluetoothViewModel.PairingStatus.Success -> {
                Text("연결됨", color = Color.Green)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Send Data",
                    color = Color.Blue,
                    modifier = Modifier.clickable { onSendDataClick() }
                )
            }
            BluetoothViewModel.PairingStatus.Failure -> Text("연결 안 됨", color = Color.Gray)
            else -> Text("연결 안 됨", color = Color.Gray)
        }
    }
}



fun hasBluetoothPermissions(context: Context): Boolean {
    val bluetoothPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
    val connectPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
    return bluetoothPermission == PackageManager.PERMISSION_GRANTED &&
            connectPermission == PackageManager.PERMISSION_GRANTED
}
