package com.lodong.poen.factory

import BluetoothViewModel
import PreferencesHelper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.service.BluetoothForegroundService


class BluetoothViewModelFactory(
    private val service: BluetoothForegroundService,
    private val repository: BinaryBleRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BluetoothViewModel(service, repository, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}