import android.bluetooth.BluetoothDevice
import android.util.Log

class BondStateHandler(
    private val onPairingSuccess: (BluetoothDevice) -> Unit,
    private val onPairingFailure: (BluetoothDevice) -> Unit
) {
    fun handleBondStateChange(device: BluetoothDevice, bondState: Int) {
        when (bondState) {
            BluetoothDevice.BOND_BONDED -> {
                Log.d("BondStateHandler", "Paired with: ${device.address}")
                onPairingSuccess(device)
            }
            BluetoothDevice.BOND_NONE -> {
                Log.d("BondStateHandler", "Pairing failed or unpaired: ${device.address}")
                onPairingFailure(device)
            }
        }
    }
}
