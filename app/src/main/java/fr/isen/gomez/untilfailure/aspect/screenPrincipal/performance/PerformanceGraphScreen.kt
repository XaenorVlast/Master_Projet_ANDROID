package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance

import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fr.isen.gomez.untilfailure.data.Workout

@Composable
fun PerformanceGraphScreen(viewModel: PerformanceViewModel, userId: String, workoutType: String) {
    val comparisonWorkouts by viewModel.comparisonWorkouts.collectAsState()

    // Composable pour la sélection des workouts
    WorkoutSelectionUI(viewModel, userId, workoutType)

    // Graphiques de comparaison
    comparisonWorkouts.first?.let { firstWorkout ->
        comparisonWorkouts.second?.let { secondWorkout ->
            Column {
                Text(
                    "Comparison of Workouts",
                    style = MaterialTheme.typography.headlineMedium // Ajusté pour Material3
                )
                WorkoutSeriesComparisonChart(listOf(firstWorkout, secondWorkout), "Weight")
                WorkoutSeriesComparisonChart(listOf(firstWorkout, secondWorkout), "Reps")
            }
        }
    }
}

// Utilisez WorkoutSeriesChart de la réponse précédente pour créer un graphique pour comparer les deux workouts
@Composable
fun WorkoutSeriesComparisonChart(workouts: List<Workout>, dataType: String) {
    val lineDataSets = workouts.mapIndexed { index, workout ->
        val entries = workout.series.mapIndexed { seriesIndex, series ->
            Entry(seriesIndex.toFloat(), if (dataType == "Weight") series.weight.toFloat() else series.validReps.toFloat())
        }
        LineDataSet(entries, "Workout ${index + 1} ($dataType)")
    }
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = LineData(lineDataSets)
                description.text = "Series $dataType Comparison"
            }
        },
        update = { chart ->
            chart.invalidate()
        }
    )
}

@Composable
fun WorkoutSelectionUI(viewModel: PerformanceViewModel, userId: String, workoutType: String) {
    // Charger les workouts si nécessaire
    val workouts by viewModel.workouts.collectAsState()
    var selectedWorkout1 by remember { mutableStateOf<Workout?>(null) }
    var selectedWorkout2 by remember { mutableStateOf<Workout?>(null) }

    Column {
        Text("Select two workouts for comparison:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(workouts.filter { it.type == workoutType }) { workout ->
                Button(
                    onClick = {
                        if (selectedWorkout1 == null) {
                            selectedWorkout1 = workout
                        } else if (selectedWorkout2 == null) {
                            selectedWorkout2 = workout
                        } else {
                            // Reset or manage selections
                            selectedWorkout1 = workout
                            selectedWorkout2 = null
                        }
                    }
                ) {
                    Text("Select Workout: ${workout.date}")
                }
            }
        }

        // Display selected workouts
        Text("Selected Workouts:")
        if (selectedWorkout1 != null) Text("1: ${selectedWorkout1!!.date}")
        if (selectedWorkout2 != null) Text("2: ${selectedWorkout2!!.date}")

        // Button to trigger comparison
        Button(
            onClick = {
                selectedWorkout1?.let { w1 ->
                    selectedWorkout2?.let { w2 ->
                        viewModel.selectComparisonWorkouts(w1.workoutId, w2.workoutId, userId)
                    }
                }
            },
            enabled = selectedWorkout1 != null && selectedWorkout2 != null
        ) {
            Text("Compare")
        }
    }
}