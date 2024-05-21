package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance


import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fr.isen.gomez.untilfailure.data.Series
import fr.isen.gomez.untilfailure.data.Workout
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel
import com.github.mikephil.charting.components.YAxis




@Composable
fun PerformanceGraphScreen(viewModel: PerformanceViewModel, userId: String, workoutType: String) {
    val comparisonWorkouts by viewModel.comparisonWorkouts.collectAsState()

    // Log when Composable is recomposed and what the current state of comparisonWorkouts is
    Log.d("PerformanceGraphScreen", "Recomposing with workoutType: $workoutType, comparisonWorkouts: $comparisonWorkouts")

    // UI for workout selection
    WorkoutSelectionUI(viewModel, userId, workoutType)

    // Display comparison charts when both workouts are selected
    comparisonWorkouts.first?.let { firstWorkout ->
        comparisonWorkouts.second?.let { secondWorkout ->
            Log.d("PerformanceGraphScreen", "Displaying comparison charts for workouts.")
            Column {
                Text("Comparison of Workouts", style = MaterialTheme.typography.headlineMedium)
                WorkoutComparisonCharts(firstWorkout.series, secondWorkout.series)
            }
        }
    }
}
@Composable
fun WorkoutSelectionUI(viewModel: PerformanceViewModel, userId: String, workoutType: String) {
    val workouts by viewModel.workouts.collectAsState()
    var selectedWorkout1 by remember { mutableStateOf<Workout?>(null) }
    var selectedWorkout2 by remember { mutableStateOf<Workout?>(null) }
    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }

    Column {
        Log.d("WorkoutSelectionUI", "Rendering Workout Selection UI")
        Text("Select two workouts for comparison:", style = MaterialTheme.typography.titleMedium)

        // Dropdown Menu for the first workout
        Text("Workout 1", style = MaterialTheme.typography.bodyMedium)
        Button(onClick = { expanded1 = true }) {
            Text(text = selectedWorkout1?.date ?: "Select Workout 1")
        }
        DropdownMenu(
            expanded = expanded1,
            onDismissRequest = { expanded1 = false }
        ) {
            workouts.filter { it.type == workoutType }.forEach { workout ->
                DropdownMenuItem(
                    onClick = {
                        selectedWorkout1 = workout
                        expanded1 = false  // Corrected from expanded1.value = false
                        viewModel.loadWorkoutDetails(userId, workout.workoutId)
                    },
                    text = { Text(workout.date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Menu for the second workout
        Text("Workout 2", style = MaterialTheme.typography.bodyMedium)
        Button(onClick = { expanded2 = true }) {
            Text(text = selectedWorkout2?.date ?: "Select Workout 2")
        }
        DropdownMenu(
            expanded = expanded2,
            onDismissRequest = { expanded2 = false }
        ) {
            workouts.filter { it.type == workoutType && it != selectedWorkout1 }.forEach { workout ->
                DropdownMenuItem(
                    onClick = {
                        selectedWorkout2 = workout
                        expanded2 = false  // Corrected from expanded2.value = false
                        viewModel.loadWorkoutDetails(userId, workout.workoutId)
                    },
                    text = { Text(workout.date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Compare Button
        Button(
            onClick = {
                selectedWorkout1?.let { w1 ->
                    selectedWorkout2?.let { w2 ->
                        if (w1 != w2) {
                            Log.d("WorkoutSelectionUI", "Comparing workouts: ${w1.workoutId} and ${w2.workoutId}")
                            viewModel.selectComparisonWorkouts(w1.workoutId, w2.workoutId, userId)
                        }
                    }
                }
            },
            enabled = selectedWorkout1 != null && selectedWorkout2 != null && selectedWorkout1 != selectedWorkout2
        ) {
            Text("Compare")
        }
    }
}


@Composable
fun WorkoutComparisonCharts(workout1: List<Series>, workout2: List<Series>) {
    Column {
        Text("Combined Workout Comparison", style = MaterialTheme.typography.bodyLarge)
        CombinedChart(workout1, workout2)
    }
}

@Composable
fun CombinedChart(workout1: List<Series>, workout2: List<Series>) {
    if (workout1.isNotEmpty() || workout2.isNotEmpty()) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val weightEntries1 = workout1.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.weight.toFloat())
                    }
                    val weightEntries2 = workout2.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.weight.toFloat())
                    }
                    val repsEntries1 = workout1.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.validReps.toFloat())
                    }
                    val repsEntries2 = workout2.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.validReps.toFloat())
                    }

                    val weightDataSet1 = LineDataSet(weightEntries1, "Workout 1 Weight").apply {
                        axisDependency = YAxis.AxisDependency.LEFT
                        color = Color.RED
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.RED)
                        enableDashedLine(10f, 5f, 0f)
                    }
                    val weightDataSet2 = LineDataSet(weightEntries2, "Workout 2 Weight").apply {
                        axisDependency = YAxis.AxisDependency.LEFT
                        color = Color.GREEN
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.GREEN)
                        enableDashedLine(20f, 10f, 0f)
                    }
                    val repsDataSet1 = LineDataSet(repsEntries1, "Workout 1 Reps").apply {
                        axisDependency = YAxis.AxisDependency.RIGHT
                        color = Color.BLUE
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.BLUE)
                    }
                    val repsDataSet2 = LineDataSet(repsEntries2, "Workout 2 Reps").apply {
                        axisDependency = YAxis.AxisDependency.RIGHT
                        color = Color.YELLOW
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.YELLOW)
                    }

                    data = LineData(listOf(weightDataSet1, weightDataSet2, repsDataSet1, repsDataSet2))
                    description.text = "Weight and Reps Comparison"
                    xAxis.labelRotationAngle = 0f
                    legend.isEnabled = true

                    axisRight.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)
                    xAxis.setDrawGridLines(false)

                    axisRight.textColor = Color.BLUE
                    axisLeft.textColor = Color.RED
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            update = { chart ->
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        )
    } else {
        Text("No data available for the selected workouts", style = MaterialTheme.typography.bodyMedium)
    }
}
