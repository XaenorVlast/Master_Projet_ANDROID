package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel

@Composable
fun PerformanceScreen(viewModel: PerformanceViewModel) {
    val performances = viewModel.performances.value
    var selectedScreen by remember { mutableStateOf("list") }
    var selectedExerciseId by remember { mutableStateOf("") }  // Use ID now instead of name

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedScreen = "list" },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f)
            ) {
                Text("Session List", color = Color.White)
            }
            Button(
                onClick = {
                    selectedScreen = "graph"
                    if (performances.isNotEmpty()) {
                        selectedExerciseId = performances.first().exercisesPerformed.firstOrNull()?.exerciseId ?: ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f)
            ) {
                Text("Performance Graph", color = Color.White)
            }
        }
        when (selectedScreen) {
            "list" -> SessionSelectionScreen(performances) { performance ->
                selectedExerciseId = performance.exercisesPerformed.firstOrNull()?.exerciseId ?: ""
            }
            "graph" -> if (selectedExerciseId.isNotEmpty()) {
                PerformanceGraphScreen(performances, selectedExerciseId)
            } else {
                Text("Please select an exercise from a session.", color = Color.Red)
            }
        }
    }
}
