package fr.isen.gomez.untilfailure.aspect.screenPrincipal

import fr.isen.gomez.untilfailure.model.exercice.ExerciceActivity
import fr.isen.gomez.untilfailure.viewModel.ble.ScanViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.gomez.untilfailure.data.Exercise
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.SeanceViewModel


import android.content.Intent
import androidx.compose.runtime.getValue

import androidx.compose.ui.platform.LocalContext
import fr.isen.gomez.untilfailure.data.nfc.NfcState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SeanceScreen(
    seanceViewModel: SeanceViewModel = viewModel(),
    scanViewModel: ScanViewModel = viewModel(),
    tagName: String = seanceViewModel.tagName.collectAsState().value
) {
    val exercises = seanceViewModel.exercises.collectAsState().value
    val isConnected = scanViewModel.connectionState.collectAsState().value
    val context = LocalContext.current
    val nfcState by seanceViewModel.nfcState.collectAsState()
    val tagName by seanceViewModel.tagName.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SectionTitle(title = "Choisissez votre exercice")
        if (!isConnected) {
            Text(
                "Connexion BLE non établie. Veuillez connecter un appareil.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
          //  Text("Tag Detected: $tagName")
            ExerciseButtons(
                exercises = exercises,
                isConnected = isConnected,
                onExerciseSelected = { exercise ->
                    if (isConnected) {
                        val intent = Intent(context, ExerciceActivity::class.java).apply {
                            putExtra("EXERCISE_ID", exercise.id) // Assurez-vous que 'id' est une propriété de votre classe Exercise
                            putExtra("EXERCISE_NAME", exercise.name) // Exemple d'envoi de plus d'info
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}


@Composable
fun ExerciseButtons(
    exercises: List<Exercise>,
    isConnected: Boolean,
    onExerciseSelected: (Exercise) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        exercises.forEach { exercise ->
            ColorButton(
                onClick = { if (isConnected) onExerciseSelected(exercise) },
                imageId = exercise.imageId,
                text = exercise.name,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}




@Composable
fun ColorButton(
    onClick: () -> Unit,
    imageId: Int?,
    text: String,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.Red // Couleur du fond du bouton en rouge
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            imageId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = text,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White // Texte en blanc pour un bon contraste
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}
