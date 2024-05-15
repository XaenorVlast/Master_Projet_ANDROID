package fr.isen.gomez.untilfailure.model.exercice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fr.isen.gomez.untilfailure.BLEManager
import java.util.UUID

class ExerciceActivity : ComponentActivity() {
    private val SERVICE_UUID = UUID.fromString("00000000-cc7a-482a-984a-7f2ed5b3e58f")
    private val CHARACTERISTIC_UUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceControlScreen()
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
    private fun sendCommandToBLEDevice(context: android.content.Context) {
        val bluetoothGatt = BLEManager.getGatt()
        val service: BluetoothGattService? = bluetoothGatt?.getService(SERVICE_UUID)
        val characteristic: BluetoothGattCharacteristic? = service?.getCharacteristic(CHARACTERISTIC_UUID)

        characteristic?.let {
            it.value = byteArrayOf(0x01)  // Command to send, example here is 0x01
            val success = bluetoothGatt.writeCharacteristic(it)
            if (success) {
                Toast.makeText(context, "Command sent successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to send command", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(context, "Characteristic not found", Toast.LENGTH_SHORT).show()
    }
}
