package fr.isen.gomez.untilfailure.model.exercice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fr.isen.gomez.untilfailure.BLEManager
import java.util.*

class ExerciceActivity : ComponentActivity(), BLEManager.NotificationListener {
    private val SERVICE_UUID = UUID.fromString("00000000-cc7a-482a-984a-7f2ed5b3e58f")
    private val CHARACTERISTIC_NOTIFY_UUID1 =
        UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19") // UUID for notifications
    private val CHARACTERISTIC_NOTIFY_UUID2 =
        UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19") // UUID for sending data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceControlScreen()
        }

        setupNotificationListener()
        BLEManager.notificationListener = this
    }

    override fun onNotificationReceived(characteristic: BluetoothGattCharacteristic) {
        runOnUiThread {
            val receivedData = characteristic.value.toString(Charsets.UTF_8)
            Toast.makeText(this, "Received notification: $receivedData", Toast.LENGTH_SHORT).show()
        }
    }
    @Composable
    fun DeviceControlScreen() {
        val context = LocalContext.current
        Button(onClick = { sendCommandToBLEDevice(context) }) {
            Text("Send Command")
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupNotificationListener() {
        val bluetoothGatt = BLEManager.getGatt()
        bluetoothGatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_NOTIFY_UUID1)
            ?.let { characteristic ->
                val notificationDescriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                bluetoothGatt.setCharacteristicNotification(characteristic, true)
                notificationDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt.writeDescriptor(notificationDescriptor)
            }
    }

    @SuppressLint("MissingPermission")
    private fun sendCommandToBLEDevice(context: android.content.Context) {
        val bluetoothGatt = BLEManager.getGatt()
        val service: BluetoothGattService? = bluetoothGatt?.getService(SERVICE_UUID)
        val characteristic: BluetoothGattCharacteristic? =
            service?.getCharacteristic(CHARACTERISTIC_NOTIFY_UUID2)

        characteristic?.let {
            it.value = byteArrayOf(0x01)  // Command to send
            val success = bluetoothGatt.writeCharacteristic(it)
            if (success) {
                Toast.makeText(context, "Command sent successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to send command", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
