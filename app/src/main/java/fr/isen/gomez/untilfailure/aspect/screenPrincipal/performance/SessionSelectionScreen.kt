import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.data.Workout
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import fr.isen.gomez.untilfailure.data.Series
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSelectionScreen(viewModel: PerformanceViewModel, userId: String, exerciseType: String) {
    LaunchedEffect(exerciseType) {
        Log.d("SessionSelectionScreen", "Loading workouts for type: $exerciseType")
        viewModel.loadWorkoutsByType(userId, exerciseType)
    }

    val workouts by viewModel.workouts.collectAsState()
    val selectedWorkout by viewModel.selectedWorkout.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workouts") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (selectedWorkout != null) {
                Log.d("SessionSelectionScreen", "Displaying selected workout details for ${selectedWorkout!!.workoutId}")
                WorkoutDetail(selectedWorkout)
            } else {
                Log.d("SessionSelectionScreen", "No workout selected, displaying list")
                LazyColumn {
                    items(workouts) { workout ->
                        Text(
                            "Date: ${workout.date}, Type: ${workout.type}",
                            modifier = Modifier
                                .clickable {
                                    if (!workout.workoutId.isNullOrEmpty()) {
                                        Log.d("SessionSelectionScreen", "Workout clicked: ${workout.workoutId}")
                                        viewModel.selectWorkout(workout)
                                        viewModel.loadWorkoutDetails(userId, workout.workoutId)
                                    } else {
                                        Log.e("SessionSelectionScreen", "Error: Workout ID is null or empty for workout ${workout.date}")
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun WorkoutDetail(workout: Workout?) {
    workout?.let {
        Log.d("WorkoutDetail", "Showing details for workout ID: ${workout.workoutId}")
        Column {
            Text("Date: ${it.date}", style = MaterialTheme.typography.titleMedium)
            it.series.forEach { series ->
                // Displaying both valid and invalid reps along with weight for each series
                Text(
                    "Series ${series.seriesNumber}: ${series.validReps} valid reps, ${series.invalidReps} invalid reps at ${series.weight}kg",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            WorkoutSeriesCharts(it.series)
        }
    } ?: Log.d("WorkoutDetail", "Workout detail requested but workout is null")
}




@Composable
fun WorkoutSeriesCharts(series: List<Series>) {
    Column {
        WeightChart(series)
        Spacer(modifier = Modifier.height(16.dp)) // Espacer les graphiques
        RepetitionsChart(series)
    }
}

@Composable
fun WeightChart(series: List<Series>) {
    if (series.isNotEmpty()) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val entries = series.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.weight.toFloat())
                    }
                    val dataSet = LineDataSet(entries, "Weight").apply {
                        color = Color.RED
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.RED)
                    }
                    data = LineData(dataSet)
                    description.text = "Weight per Series"
                    xAxis.labelRotationAngle = 0f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            update = { chart ->
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        )
    }
}

@Composable
fun RepetitionsChart(series: List<Series>) {
    if (series.isNotEmpty()) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val entries = series.mapIndexed { index, s ->
                        Entry(index.toFloat(), s.validReps.toFloat())
                    }
                    val dataSet = LineDataSet(entries, "Valid Reps").apply {
                        color = Color.BLUE
                        valueTextColor = Color.WHITE
                        lineWidth = 2.5f
                        setCircleColor(Color.BLUE)
                    }
                    data = LineData(dataSet)
                    description.text = "Valid Reps per Series"
                    xAxis.labelRotationAngle = 0f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            update = { chart ->
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        )
    }
}
