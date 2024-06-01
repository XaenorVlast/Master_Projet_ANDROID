import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.gomez.untilfailure.viewModel.ble.ScanViewModel

@SuppressLint("MissingPermission")
@Composable
fun BLEScreen(viewModel: ScanViewModel = viewModel()) {
    val scanResults by viewModel.scanResults.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            "BLE scan...",
            color = Color.Red,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Filled.Bluetooth,
            contentDescription = "Bluetooth Icon",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(300.dp)
                .padding(bottom = 8.dp)
                .size(300.dp),
            tint = Color.Black,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (isScanning) viewModel.stopScan() else viewModel.startScan()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isScanning) "Arrêter le scan" else "Démarrer le scan",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
            )
        }

        Text(
            if (connectionState) "Connecté" else "Non connecté",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(scanResults) { result ->
                Text(
                    "Appareil: ${result.device.name ?: "Inconnu"} - RSSI: ${result.rssi}",
                    modifier = Modifier
                        .clickable {
                            viewModel.connectToDevice(result.device.address)
                            viewModel.stopScan()  // Arrête le scan
                            viewModel.filterResultsForConnectedDevice(result.device.address)  // Filtre les résultats pour ne montrer que l'appareil connecté
                        }
                        .padding(vertical = 8.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )



            }
        }
    }
}
