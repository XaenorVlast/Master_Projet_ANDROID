package fr.isen.gomez.untilfailure



import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context

object BLEManager {
    private var bluetoothGatt: BluetoothGatt? = null

    @SuppressLint("MissingPermission")
    fun connect(context: Context, address: String, autoConnect: Boolean = false): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val device = bluetoothManager.adapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(context, autoConnect, gattCallback)
        return bluetoothGatt != null
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                BluetoothProfile.STATE_DISCONNECTED -> disconnect()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            // Callback pour la découverte de services
        }
    }
    fun getGatt(): BluetoothGatt? {
        return bluetoothGatt
    }
}
