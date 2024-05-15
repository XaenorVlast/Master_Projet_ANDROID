import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.viewModel.ble.ScanViewModel

@SuppressLint("MissingPermission")
@Composable
fun BLEScreen(viewModel: ScanViewModel = viewModel()) {
    val scanResults by viewModel.scanResults.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    Column {
        Button(
            onClick = {
                if (isScanning) viewModel.stopScan() else viewModel.startScan()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isScanning) "Arrêter le scan" else "Démarrer le scan")
        }

        Text(
            if (connectionState) "Connecté" else "Non connecté",
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(scanResults) { result ->
                Text(
                    "Appareil: ${result.device.name ?: "Inconnu"} - RSSI: ${result.rssi}",
                    modifier = Modifier
                        .clickable {
                            viewModel.connectToDevice(result.device.address)
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
