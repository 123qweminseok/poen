package com.lodong.poen.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lodong.poen.ui.navigation.Routes

@Preview(showBackground = true)
@Composable
fun PermissionRequestScreenPreview() {
    val navController = rememberNavController()
    PermissionRequestScreen(navController = navController)
}

@Composable
fun PermissionRequestScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        // 권한 체크 없이 바로 다음 화면으로 이동
        navController.navigate(Routes.LoginScreen.route) {
            popUpTo(Routes.PermissionRequestScreen.route) { inclusive = true }
        }
    }
}
