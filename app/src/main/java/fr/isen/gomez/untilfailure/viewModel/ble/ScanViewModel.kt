package fr.isen.gomez.untilfailure.viewModel.ble

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.lifecycle.AndroidViewModel
import fr.isen.gomez.untilfailure.BLEManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults = _scanResults.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()


    init {
        checkBluetoothGattConnection()
    }

    private fun checkBluetoothGattConnection() {
        val bluetoothGatt = BLEManager.getGatt() // Supposons que BLEManager a une méthode bleGatt qui retourne un BluetoothGatt?
        _connectionState.value = bluetoothGatt != null
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                val updatedResults = _scanResults.value.toMutableList()
                if (!updatedResults.any { it.device.address == result.device.address }) {
                    updatedResults.add(result)
                    _scanResults.value = updatedResults
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.let { _scanResults.value = it.toList() }
        }

        override fun onScanFailed(errorCode: Int) {
            _errorMessage.value = "Scan failed with error: $errorCode"
        }

    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        bluetoothLeScanner?.let {
            _isScanning.value = true
            it.startScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.let {
            it.stopScan(scanCallback)
            _isScanning.value = false
        }
    }

    fun connectToDevice(address: String) {
        if (BLEManager.connect(context, address)) {
            _connectionState.value = true
        } else {
            _errorMessage.value = "Failed to connect."
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCleared() {
        super.onCleared()
        BLEManager.disconnect()
        stopScan()
    }

    fun filterResultsForConnectedDevice(address: String) {
        val connectedDeviceResult = _scanResults.value.find { it.device.address == address }
        connectedDeviceResult?.let {
            _scanResults.value = listOf(it)
        }
    }

}
