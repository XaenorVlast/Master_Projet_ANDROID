package fr.isen.gomez.untilfailure



import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log

object BLEManager {
    private var bluetoothGatt: BluetoothGatt? = null
    var notificationListener: NotificationListener? = null

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

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic?.let { char ->
                char.value?.let { value ->
                    val receivedString = value.toString(Charsets.UTF_8)
                    Log.d("BLE Notification", "Received data: $receivedString")
                    notificationListener?.onNotificationReceived(char)
                }
            }
        }

    }
    // In BLEManager
    interface NotificationListener {
        fun onNotificationReceived(characteristic: BluetoothGattCharacteristic)
    }

    fun getGatt(): BluetoothGatt? {
        return bluetoothGatt
    }
}
