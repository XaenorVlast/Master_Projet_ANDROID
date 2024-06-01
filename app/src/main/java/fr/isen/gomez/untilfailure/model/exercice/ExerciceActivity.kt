package fr.isen.gomez.untilfailure.model.exercice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import fr.isen.gomez.untilfailure.BLEManager
import fr.isen.gomez.untilfailure.data.ExerciseState
import fr.isen.gomez.untilfailure.data.SessionMode
import fr.isen.gomez.untilfailure.model.screenPrincipal.EcranPrincipalActivity
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ExerciceActivity : ComponentActivity(), BLEManager.NotificationListener {
    private val SERVICE_UUID = UUID.fromString("00000000-cc7a-482a-984a-7f2ed5b3e58f")
    private val CHARACTERISTIC_NOTIFY_UUID1 =
        UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19") // UUID for notifications
    private val CHARACTERISTIC_NOTIFY_UUID2 =
        UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19") // UUID for sending data
    private var currentState by mutableStateOf(ExerciseState.AWAITING_START)
    private var validReps = 0
    private var invalidReps = 0
    private var barWeight by mutableStateOf(0f)  // stocke le poids de la barre en kg
    private var showWeightDialog by mutableStateOf(false)
    private var seriesCount = 0  // Compteur pour les séries
    private var userId: String? = null
    private var workoutId: String? = null
    private var exerciseName: String? = null
    private var timerValue by mutableStateOf(0)  // Temps restant sur le minuteur
    private var showTimerSetup by mutableStateOf(false)
    private var showTimerDialog by mutableStateOf(false)
    private var countdownTimer: CountDownTimer? = null
    private var lastReceivedCommand: String? = null
    private var currentSessionMode by mutableStateOf(SessionMode.NON_GUIDED)
    private var currentObjective: String = "Default Objective"
    private var feedbackMessage by mutableStateOf("")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer les informations nécessaires de l'intent
        exerciseName = intent.getStringExtra("EXERCISE_NAME")
        userId = FirebaseAuth.getInstance().currentUser?.uid

        // Démarrer une nouvelle séance d'entraînement si le nom de l'exercice est fourni
        if (exerciseName != null && userId != null) {
            startNewWorkout(userId!!, exerciseName!!)
        }

        // Configurer l'écoute des notifications Bluetooth
        setupNotificationListener()
        BLEManager.notificationListener = this

        fetchUserObjective { objective ->
            currentObjective = objective
            // Vous pouvez ici mettre à jour l'interface utilisateur ou initialiser des fragments avec cet objectif.
        }
        // Définir le contenu de l'UI avec une sélection initiale du mode de session
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Choisir le mode de session", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = {
                        currentSessionMode = SessionMode.GUIDED
                        updateUI() // Appel pour rafraîchir l'UI après la sélection
                    }) {
                        Text("Session Guidée")
                    }
                    Button(onClick = {
                        currentSessionMode = SessionMode.NON_GUIDED
                        updateUI() // Appel pour rafraîchir l'UI après la sélection
                    }) {
                        Text("Session Non Guidée")
                    }
                }
            }
        }
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
                when (currentSessionMode) {
                    SessionMode.GUIDED -> GuidedSessionUI(exerciseName)
                    SessionMode.NON_GUIDED -> NonGuidedSessionUI()
                }
            }
        }
    }

    @Composable
    fun ExerciseVideo(exerciseName: String?) {
        when (exerciseName) {
            "squat" -> VideoPlayer(videoUrl = "url_to_squat_video")
            "bench" -> VideoPlayer(videoUrl = "url_to_bench_video")
            "deadlift" -> VideoPlayer(videoUrl = "url_to_deadlift_video")
            else -> Text("Pas de vidéo disponible pour cet exercice.")
        }
    }

    @Composable
    fun VideoPlayer(videoUrl: String) {
        AndroidView(factory = { context ->
            // Utilisez votre lecteur vidéo ici; par exemple, intégrez un VideoView ou un lecteur externe
            VideoView(context).apply {
                setVideoPath(videoUrl)
                start()
            }
        })
    }

    // Fonction pour récupérer l'objectif de l'utilisateur
    private fun fetchUserObjective(onResult: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
        val usersRef = database.getReference("user")

        val currentUser = auth.currentUser
        if (currentUser != null) {
            usersRef.child(currentUser.uid).child("objectif").get().addOnSuccessListener { snapshot ->
                val objective = snapshot.value?.toString() ?: "Default Objective"
                onResult(objective)
            }.addOnFailureListener {
                // Gérer l'erreur si nécessaire, par exemple en retournant une valeur par défaut ou en loggant l'erreur
                onResult("Default Objective")
            }
        } else {
            // Gérer le cas où l'utilisateur n'est pas connecté ou l'UID n'est pas disponible
            onResult("Default Objective")
        }
    }

    @Composable
    fun GuidedSessionUI(exerciseName: String?){
        var recommendedReps by remember { mutableStateOf(0) } // Nombre de répétitions recommandées

        // Déterminer le nombre de répétitions en fonction de l'objectif et du numéro de la série
        recommendedReps = when (currentObjective) {
            "Force" -> {
                if (seriesCount == 0 || seriesCount == 1) 5 else 3
            }
            "Haltérophilie" -> {
                if (seriesCount == 0 || seriesCount == 1) 10 else 8
            }
            else -> 0 // Cas par défaut si aucun objectif n'est défini
        }
        Surface(modifier = Modifier.padding(all = 16.dp), color = MaterialTheme.colorScheme.background) {
            Column {
                // Afficher la description de l'état actuel
                Text(
                    text = "État actuel : ${getStateDescription(currentState)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                if (showWeightDialog) {
                    ShowWeightInputDialog(
                        onWeightEntered = { weight ->
                            barWeight = weight
                            showWeightDialog = false  // Fermer le dialogue après la saisie

                            // Activer la configuration du minuteur uniquement si ce n'est pas la première série
                            if (seriesCount > 0) {
                                showTimerSetup = true  // Préparer à afficher le dialogue du minuteur
                            }
                        },
                        sessionMode = currentSessionMode,
                        objective = currentObjective,
                        seriesCount = seriesCount
                    )
                }

                // Afficher le dialogue pour le minuteur si nécessaire et si ce n'est pas la première série
                if (showTimerSetup) {
                    ShowTimerInputDialog { duration ->
                        startTimer(duration)
                        showTimerSetup = false  // Fermer le dialogue après la saisie
                    }
                }

                // Affichage conditionnel des informations selon l'état
                when (currentState) {
                    ExerciseState.AWAITING_START -> {
                        Text("Prêt à commencer la séance.", style = MaterialTheme.typography.bodyLarge)
                    }
                    ExerciseState.AWAITING_REFERENCE_CONFIRMATION -> {
                        Text("En attente de la confirmation de la répétition de référence.", style = MaterialTheme.typography.bodyLarge)
                        ExerciseVideo(exerciseName)
                        Text("Suivez les conseils dans la vidéo pour réaliser votre répétition de référence.", style = MaterialTheme.typography.bodyLarge)
                    }
                    ExerciseState.AWAITING_REFERENCE_VALIDATION -> {
                        Text("Validation de la répétition de référence en cours.", style = MaterialTheme.typography.bodyLarge)
                        ExerciseVideo(exerciseName)
                        Text("Suivez les conseils dans la vidéo pour réaliser la validation de la répétition de référence.", style = MaterialTheme.typography.bodyLarge)
                    }

                    ExerciseState.RECORDING_REPETITIONS -> {
                        Text("Enregistrement des répétitions.", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Poids de la barre : $barWeight kg",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Blue
                        )
                        Text("Répétitions valides : $validReps", style = MaterialTheme.typography.bodyLarge, color = Color.Green)
                        Text("Répétitions non valides : $invalidReps", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                        Text("Répétitions recommandées : $recommendedReps", style = MaterialTheme.typography.bodyLarge, color = Color.Magenta)
                        if (feedbackMessage.isNotEmpty() && exerciseName=="Bench") {
                            Text(feedbackMessage, style = MaterialTheme.typography.bodyLarge, color = Color.Magenta)
                        }
                    }

                    ExerciseState.SESSION_ENDED -> {
                        Text("La séance est terminée.", style = MaterialTheme.typography.bodyLarge)
                    }
                }


                // Logique pour afficher ou masquer le dialogue basé sur l'état de l'application
                if (currentState == ExerciseState.RECORDING_REPETITIONS && barWeight == 0f) {
                    if (showTimerDialog) {
                        ShowTimerInputDialog { duration ->
                            startTimer(duration)
                            showTimerDialog = false  // Fermer le dialogue après la saisie

                        }
                    }
                    showWeightDialog = true
                }
                if (currentState == ExerciseState.RECORDING_REPETITIONS && timerValue > 0) {
                    Text("Temps restant : $timerValue secondes", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
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
                            endSession()
                        },
                        modifier = Modifier.padding(PaddingValues(top = 8.dp))
                    ) {
                        Text("Terminer séance")
                    }
                }
                if (currentState == ExerciseState.SESSION_ENDED) {
                    lastReceivedCommand = null  // Réinitialiser lorsque la séance est terminée
                }

            }
        }
    }
    @Composable
    fun NonGuidedSessionUI() {


        Surface(modifier = Modifier.padding(all = 16.dp), color = MaterialTheme.colorScheme.background) {
            Column {
                // Afficher la description de l'état actuel
                Text(
                    text = "État actuel : ${getStateDescription(currentState)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                if (showWeightDialog) {
                    ShowWeightInputDialog(
                        onWeightEntered = { weight ->
                            barWeight = weight
                            showWeightDialog = false  // Fermer le dialogue après la saisie

                            // Activer la configuration du minuteur uniquement si ce n'est pas la première série
                            if (seriesCount > 0) {
                                showTimerSetup = true  // Préparer à afficher le dialogue du minuteur
                            }
                        },
                        sessionMode = currentSessionMode,
                        objective = currentObjective,
                        seriesCount = seriesCount
                    )
                }

                // Afficher le dialogue pour le minuteur si nécessaire et si ce n'est pas la première série
                if (showTimerSetup) {
                    ShowTimerInputDialog { duration ->
                        startTimer(duration)
                        showTimerSetup = false  // Fermer le dialogue après la saisie
                    }
                }

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
                        Text(
                            text = "Poids de la barre : $barWeight kg",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Blue
                        )
                        Text("Répétitions valides : $validReps", style = MaterialTheme.typography.bodyLarge, color = Color.Green)
                        Text("Répétitions non valides : $invalidReps", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                        if (feedbackMessage.isNotEmpty() && exerciseName=="Bench") {
                            Text(feedbackMessage, style = MaterialTheme.typography.bodyLarge, color = Color.Magenta)
                        }
                    }
                    ExerciseState.SESSION_ENDED -> {
                        Text("La séance est terminée.", style = MaterialTheme.typography.bodyLarge)
                    }
                }


                // Logique pour afficher ou masquer le dialogue basé sur l'état de l'application
                if (currentState == ExerciseState.RECORDING_REPETITIONS && barWeight == 0f) {
                    if (showTimerDialog) {
                        ShowTimerInputDialog { duration ->
                            startTimer(duration)
                            showTimerDialog = false  // Fermer le dialogue après la saisie

                        }
                    }
                    showWeightDialog = true
                }
                if (currentState == ExerciseState.RECORDING_REPETITIONS && timerValue > 0) {
                    Text("Temps restant : $timerValue secondes", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
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
                            endSession()
                        },
                        modifier = Modifier.padding(PaddingValues(top = 8.dp))
                    ) {
                        Text("Terminer séance")
                    }
                }
                if (currentState == ExerciseState.SESSION_ENDED) {
                    lastReceivedCommand = null  // Réinitialiser lorsque la séance est terminée
                }

            }
        }
    }


    private fun endSession() {
        currentState = ExerciseState.SESSION_ENDED
        showTimerSetup = false // Assurez-vous que le setup du timer est désactivé
        showWeightDialog = false // Assurez-vous que le dialogue du poids est également fermé
        showTimerDialog = false // Assurez-vous que tout dialogue de timer est fermé

        saveSeriesToFirebase(userId!!, workoutId!!, seriesCount)
        sendCommandToBLEDevice(0x02)  // Envoyer commande pour terminer la séance
        navigateToMainActivity()  // Naviguer vers l'écran principal après la fin de la séance
        updateUI()  // Mettre à jour l'UI après toutes les modifications d'état
    }


    @Composable
    fun ShowTimerInputDialog(onTimerDurationEntered: (Int) -> Unit) {
        val context = LocalContext.current
        if (currentSessionMode == SessionMode.GUIDED) {
            // Définir la durée prédéfinie en fonction de l'objectif
            val presetDuration = when (currentObjective) {
                "Force" -> 300  // 5 minutes
                "Haltérophilie" -> 120  // 2 minutes
                else -> 180  // Autre cas, par défaut à 3 minutes
            }

            // Appel immédiat du callback avec la durée prédéfinie
            onTimerDurationEntered(presetDuration)
        } else {
            var timerInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    // Optionnel : vous pouvez aussi appeler endSession() ici si vous voulez que la séance se termine lorsque le dialogue est fermé de manière externe.
                },
                title = { Text("Définir la durée du minuteur") },
                text = {
                    TextField(
                        value = timerInput,
                        onValueChange = { timerInput = it },
                        label = { Text("Durée (secondes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            timerInput.toIntOrNull()?.let {
                                onTimerDurationEntered(it)
                                // Fermer le dialogue après la confirmation
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Veuillez entrer un nombre valide.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text("Démarrer le minuteur")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Appeler endSession() lorsque le bouton "Annuler" est cliqué
                            showTimerDialog = false
                        }
                    ) {
                        Text("Annuler")
                    }
                }
            )
        }
    }


    private fun startTimer(duration: Int) {
        countdownTimer?.cancel() // Annuler le minuteur précédent s'il est en cours
        countdownTimer = object : CountDownTimer((duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerValue = (millisUntilFinished / 1000).toInt()
                updateUI()  // Met à jour l'interface utilisateur avec le temps restant
            }

            override fun onFinish() {
                timerValue = 0
                updateUI()
            }
        }.start()
    }


    @Composable
    fun ShowWeightInputDialog(onWeightEntered: (Float) -> Unit, sessionMode: SessionMode, objective: String, seriesCount: Int) {
        val context = LocalContext.current
        var weightInput by remember { mutableStateOf("") }
        var guidanceText = remember { mutableStateOf("") }

        // Calculer le texte de guidage en fonction du mode de session, de l'objectif et du nombre de séries
        if (sessionMode == SessionMode.GUIDED) {
            guidanceText.value = when (objective) {
                "Haltérophilie" -> when (seriesCount) {
                    0 -> "Sélectionner un poids à 50% de votre charge max pour la première série."
                    1 -> "Sélectionner un poids à 60% de votre charge max pour la deuxième série."
                    else -> "Sélectionner un poids à 65% de votre charge max pour les autres séries."
                }
                "Force" -> when (seriesCount) {
                    0 -> "Commencez avec 65% de votre charge max pour la première série."
                    in 1..4 -> "Augmentez progressivement jusqu'à 85% de votre charge max pour les séries suivantes."
                    else -> "Maintenez 85% de votre charge max."
                }
                else -> "Entrez le poids désiré (kg)."
            }
        }

        AlertDialog(
            onDismissRequest = {
                // Fermer le dialogue lorsqu'on clique à l'extérieur ou sur le bouton annuler
                showWeightDialog = false
            },
            title = { Text("Entrer le Poids de la Barre") },
            text = {
                Column {
                    if (sessionMode == SessionMode.GUIDED) {
                        Text(guidanceText.value, style = MaterialTheme.typography.bodyMedium)
                    }
                    TextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Poids (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        weightInput.toFloatOrNull()?.let {
                            onWeightEntered(it)
                            showWeightDialog = false // Fermer le dialogue après la confirmation
                        } ?: run {
                            Toast.makeText(context, "Veuillez entrer un nombre valide.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                Button(
                    onClick = { endSession() }
                ) {
                    Text("Annuler")
                }
            }
        )
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
        // Si la commande reçue est 'repv' et identique à la dernière commande, ignorer cette réception
        // Vérifier si la commande est une répétition de 'repv' et doit être ignorée
        if (receivedData == "repv" && lastReceivedCommand == "repv" || receivedData == "repv" && validReps ==0 && invalidReps == 0) {
            Log.d("ExerciseActivity", "Ignoring repeated 'repv' command.")
            return  // Ignorer cette commande
        } else {
            // Mettre à jour la dernière commande reçue si ce n'est pas une répétition à ignorer
            lastReceivedCommand = receivedData
        }

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
                    "Gval", "Dval", "Cval" -> {
                        validReps++  // Incrémenter le compteur de répétitions valides
                        handleArmDetails(receivedData)  // Gérer les détails spécifiques des bras
                    }
                    "Gnva", "Dnva", "Cnva" -> {
                        invalidReps++  // Incrémenter le compteur de répétitions invalides
                        handleArmDetails(receivedData)  // Gérer les détails spécifiques des bras
                    }
                    "repv" -> {
                        saveSeriesToFirebase(userId!!, workoutId!!, seriesCount)
                        seriesCount++
                        resetSeriesData()
                        updateUI()
                    }
                }

            }
            // Gérer les autres états au besoin.
            else -> {
                // Ajouter des cas pour d'autres états si nécessaire.
            }
        }
    }
    private fun handleArmDetails(command: String) {
        feedbackMessage =when (command) {
            "Gval", "Gnva" -> "Bras gauche incorrect."
            "Dval", "Dnva" -> "Bras droit incorrect."
            "Cval", "Cnva" -> "Tous les bras corrects."
            else -> ""
        }
        if (timerValue > 0) {
            countdownTimer?.cancel()
            timerValue = 0
        }
        updateUI()
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
    private fun resetSeriesData() {
        validReps = 0
        invalidReps = 0
        barWeight = 0f  // Supposer que l'utilisateur doit entrer le poids à nouveau si nécessaire
    }

    private fun saveSeriesToFirebase(userId: String, workoutId: String, seriesNumber: Int) {

        val workoutData = hashMapOf(
            "valid_reps" to validReps,
            "invalid_reps" to invalidReps,
            "weight" to barWeight
        )

        // Spécifiez ici l'URL de votre Firebase Realtime Database
        val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
        val databaseReference = database.reference
            .child("user")
            .child(userId)
            .child("workouts")
            .child(workoutId)
            .child("sets")
            .child("set$seriesNumber")

        databaseReference.setValue(workoutData)
            .addOnSuccessListener {
                Log.d("Firebase", "Data saved successfully!")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Failed to save data!")
            }
    }

    private fun generateWorkoutId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return "workout" + dateFormat.format(Date())
    }
    private fun startNewWorkout(userId: String, exerciseType: String) {
        workoutId = generateWorkoutId()  // Générer un ID unique pour la nouvelle séance
        seriesCount = 0  // Réinitialiser le compteur de séries pour la nouvelle séance

        if (userId == null) {
            Log.d("Firebase", "No user is logged in.")
            return
        }

        val workoutInitData = hashMapOf(
            "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            "type" to exerciseType  // Utilisation du type d'exercice passé en paramètre
        )

        // Initialisation de la base de données Firebase avec une URL spécifique
        val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
        val databaseReference = database.getReference("user")
            .child(userId)
            .child("workouts")
            .child(workoutId!!)

        // Sauvegarde des données dans la base de données
        databaseReference.setValue(workoutInitData)
            .addOnSuccessListener {
                Log.d("Firebase", "Workout session initialized successfully!")
            }
            .addOnFailureListener {
                Log.d("Firebase", "Failed to initialize workout session")
            }
    }






}
