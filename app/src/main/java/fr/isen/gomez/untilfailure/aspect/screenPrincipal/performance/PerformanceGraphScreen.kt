package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import fr.isen.gomez.untilfailure.data.SessionPerformance
import java.util.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PerformanceGraphScreen(performanceList: List<SessionPerformance>, selectedExerciseId: String) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // Data processing
                val entriesWeight = mutableListOf<Entry>()
                val entriesReps = mutableListOf<Entry>()

                performanceList.sortedBy { it.date }.forEachIndexed { index, session ->
                    session.exercisesPerformed.firstOrNull { it.exerciseId == selectedExerciseId }?.let { detail ->
                        entriesWeight.add(Entry(index.toFloat(), detail.weightLifted.toFloat()))
                        entriesReps.add(Entry(index.toFloat(), detail.repetitions.toFloat()))
                    }
                }

                val dataSetWeight = LineDataSet(entriesWeight, "Max Weight").apply {
                    color = ColorTemplate.COLORFUL_COLORS[0]
                    valueTextColor = ColorTemplate.getHoloBlue()
                    setCircleColor(ColorTemplate.COLORFUL_COLORS[0])
                }

                val dataSetReps = LineDataSet(entriesReps, "Repetitions").apply {
                    color = ColorTemplate.COLORFUL_COLORS[1]
                    valueTextColor = ColorTemplate.getHoloBlue()
                    setCircleColor(ColorTemplate.COLORFUL_COLORS[1])
                }

                this.data = LineData(dataSetWeight, dataSetReps)
                this.description.text = "Performance Over Time"
                this.invalidate() // refresh the chart with new data
            }
        },
        update = { chart ->
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
