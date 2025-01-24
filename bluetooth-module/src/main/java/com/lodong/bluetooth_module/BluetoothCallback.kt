package com.lodong.bluetooth_module

interface BluetoothCallback {
    fun onDataReceived(data: ByteArray)
    fun onDataReadyForTransfer()
    fun onDataTransferComplete()
}