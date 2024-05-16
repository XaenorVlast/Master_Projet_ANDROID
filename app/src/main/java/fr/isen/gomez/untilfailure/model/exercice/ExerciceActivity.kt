package fr.isen.gomez.untilfailure.model.exercice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.BLEManager
import fr.isen.gomez.untilfailure.data.ExerciseState
import fr.isen.gomez.untilfailure.model.screenPrincipal.EcranPrincipalActivity
import java.util.*

class ExerciceActivity : ComponentActivity(), BLEManager.NotificationListener {
    private val SERVICE_UUID = UUID.fromString("00000000-cc7a-482a-984a-7f2ed5b3e58f")
    private val CHARACTERISTIC_NOTIFY_UUID1 =
        UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19") // UUID for notifications
    private val CHARACTERISTIC_NOTIFY_UUID2 =
        UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19") // UUID for sending data
    private var currentState by mutableStateOf(ExerciseState.AWAITING_START)
    private var validReps = 0
    private var invalidReps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceControlScreen()
        }

        setupNotificationListener()
        BLEManager.notificationListener = this
    }

    override fun onNotificationReceived(characteristic: BluetoothGattCharacteristic) {
        val receivedData = characteristic.value.toString(Charsets.UTF_8)
        runOnUiThread {
            handleStateTransition(receivedData)
        }
    }


    @Composable
    fun DeviceControlScreen() {
        Surface(modifier = Modifier.padding(all = 16.dp), color = MaterialTheme.colorScheme.background) {
            Column {
                // Afficher la description de l'état actuel
                Text(
                    text = "État actuel : ${getStateDescription(currentState)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                // Affichage conditionnel des informations selon l'état
                when (currentState) {
                    ExerciseState.AWAITING_START -> {
                        Text("Prêt à commencer la séance.", style = MaterialTheme.typography.bodyLarge)
                    }
                    ExerciseState.AWAITING_REFERENCE_CONFIRMATION -> {
                        Text("En attente de la confirmation de la répétition de référence.", style = MaterialTheme.typography.bodyLarge)
                    }
                    ExerciseState.AWAITING_REFERENCE_VALIDATION -> {
                        Text("Validation de la répétition de référence en cours.", style = MaterialTheme.typography.bodyLarge)
                    }
                    ExerciseState.RECORDING_REPETITIONS -> {
                        Text("Enregistrement des répétitions.", style = MaterialTheme.typography.bodyLarge)
                        Text("Répétitions valides : $validReps", style = MaterialTheme.typography.bodyLarge, color = Color.Green)
                        Text("Répétitions non valides : $invalidReps", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                    }
                    ExerciseState.SESSION_ENDED -> {
                        Text("La séance est terminée.", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Boutons pour contrôler la séance
                if (currentState == ExerciseState.AWAITING_START) {
                    Button(
                        onClick = {
                            sendCommandToBLEDevice(0x01)  // Commande pour démarrer la séance
                            currentState = ExerciseState.AWAITING_REFERENCE_CONFIRMATION
                        },
                        modifier = Modifier.padding(PaddingValues(top = 16.dp))
                    ) {
                        Text("Commencer séance")
                    }
                }

                if (currentState != ExerciseState.SESSION_ENDED) {
                    Button(
                        onClick = {
                            currentState = ExerciseState.SESSION_ENDED
                            sendCommandToBLEDevice(0x02)  // Envoyer commande pour terminer la séance
                            navigateToMainActivity()  // Naviguer vers l'écran principal après la fin de la séance
                        },
                        modifier = Modifier.padding(PaddingValues(top = 8.dp))
                    ) {
                        Text("Terminer séance")
                    }
                }
            }
        }
    }

    private fun getStateDescription(state: ExerciseState): String {
        return when (state) {
            ExerciseState.AWAITING_START -> "En attente du début de la séance"
            ExerciseState.AWAITING_REFERENCE_CONFIRMATION -> "En attente de confirmation de la répétition de référence"
            ExerciseState.AWAITING_REFERENCE_VALIDATION -> "Validation de la répétition de référence en cours"
            ExerciseState.RECORDING_REPETITIONS -> "Enregistrement des répétitions"
            ExerciseState.SESSION_ENDED -> "Séance terminée"
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
    private fun sendCommandToBLEDevice(commandByte: Byte) {
        val bluetoothGatt = BLEManager.getGatt()
        val characteristic = bluetoothGatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_NOTIFY_UUID2)
        characteristic?.let {
            it.value = byteArrayOf(commandByte)  // Utiliser la commande fournie
            if (bluetoothGatt.writeCharacteristic(it)) {
                Toast.makeText(this, "Command sent successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Gère les transitions d'état de l'application en fonction des données reçues du périphérique BLE.
     * @param receivedData les données reçues du périphérique BLE, sous forme de chaîne de caractères.
     */
    private fun handleStateTransition(receivedData: String) {
        Log.d("BLE handleStateTransition", "Received data: $receivedData")
        Log.d("ExerciseActivity", "Current state updated to: $currentState")

        when (currentState) {

            // Si l'état actuel est en attente du début, vérifie les commandes reçues pour passer à la validation.
            ExerciseState.AWAITING_REFERENCE_CONFIRMATION -> {
                when (receivedData) {
                    "finr" -> {
                        // Si la donnée reçue est "finr", passer à l'état de validation de la référence.
                        currentState = ExerciseState.AWAITING_REFERENCE_VALIDATION
                        updateUI()  // Mise à jour de l'interface utilisateur après changement d'état
                    }
                    "refv" -> {
                        // Si la donnée reçue est "refv", revenir à l'état initial en attente de début.
                        currentState = ExerciseState.AWAITING_START
                        updateUI()  // Mise à jour de l'interface utilisateur après changement d'état
                    }
                }
            }
            // Si l'état actuel est en attente de validation de la référence, vérifie les commandes reçues pour commencer l'enregistrement ou recommencer.
            ExerciseState.AWAITING_REFERENCE_VALIDATION -> {
                when (receivedData) {
                    "vals" -> {
                        // Si la donnée reçue est "vals", passer à l'enregistrement des répétitions.
                        currentState = ExerciseState.RECORDING_REPETITIONS
                        updateUI()  // Mise à jour de l'interface utilisateur après changement d'état
                    }
                    "valf", "valv" -> {
                        // Si la donnée reçue indique une validation échouée, revenir à l'état initial.
                        currentState = ExerciseState.AWAITING_REFERENCE_CONFIRMATION

                        updateUI()  // Mise à jour de l'interface utilisateur après changement d'état
                    }
                }
            }
            // Si l'état actuel est enregistrement des répétitions, traiter les données reçues pour compter les répétitions valides ou non valides.
            ExerciseState.RECORDING_REPETITIONS -> {
                when (receivedData) {
                    "ahah" -> {
                        // Incrémenter le compteur de répétitions valides.
                        validReps++
                        updateUI()  // Mise à jour de l'interface utilisateur pour afficher le nouveau compteur
                    }
                    "ihih" -> {
                        // Incrémenter le compteur de répétitions non valides.
                        invalidReps++
                        updateUI()  // Mise à jour de l'interface utilisateur pour afficher le nouveau compteur
                    }
                    "repv" -> {
                        // Si la donnée reçue est "repv", maintenir l'état d'enregistrement des répétitions.
                        currentState = ExerciseState.RECORDING_REPETITIONS
                        updateUI()  // Mise à jour de l'interface utilisateur après changement d'état
                    }
                }
            }
            // Gérer les autres états au besoin.
            else -> {
                // Ajouter des cas pour d'autres états si nécessaire.
            }
        }
    }

    /**
     * Met à jour l'interface utilisateur en fonction de l'état actuel de l'application.
     */
    private fun updateUI() {
        runOnUiThread {
            setContent {
                DeviceControlScreen()
            }
        }
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, EcranPrincipalActivity::class.java)
        startActivity(intent)
        finish()  // Terminez cette activité pour éviter le retour en arrière
    }

}
