package com.lodong.poen.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
class PermissionManager(private val context: Context) {
    companion object {
        @Volatile
        private var instance: PermissionManager? = null

        fun getInstance(context: Context): PermissionManager {
            return instance ?: synchronized(this) {
                instance ?: PermissionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

