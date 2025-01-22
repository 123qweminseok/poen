package com.lodong.poen.viewmodel

import BluetoothViewModel
import PreferencesHelper
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.service.BluetoothForegroundService

object BluetoothViewModelSingleton {
    private var instance: BluetoothViewModel? = null

    fun getInstance(
        service: BluetoothForegroundService,
        repository: BinaryBleRepository,
        preferencesHelper: PreferencesHelper
    ): BluetoothViewModel {
        if (instance == null) {
            instance = BluetoothViewModel(service, repository, preferencesHelper)
        }
        return instance!!
    }
}
