package fr.isen.gomez.untilfailure.model.exercice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ExerciceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exerciseId = intent.getStringExtra("EXERCISE_ID") ?: "ID inconnu"
        val exerciseName = intent.getStringExtra("EXERCISE_NAME") ?: "Nom inconnu"

        setContent {
            ExerciceScreen(exerciseId, exerciseName)
        }
    }
}

@Composable
fun ExerciceScreen(exerciseId: String, exerciseName: String) {
    Surface(
        modifier = Modifier.padding(all = 16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(PaddingValues(all = 8.dp))) {
            Text(
                text = "Nom de l'exercice: $exerciseName",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "ID de l'exercice: $exerciseId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}