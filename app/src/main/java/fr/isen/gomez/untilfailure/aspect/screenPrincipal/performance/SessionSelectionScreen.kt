package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance

import androidx.compose.runtime.Composable
import fr.isen.gomez.untilfailure.data.SessionPerformance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionSelectionScreen(
    performances: List<SessionPerformance>,
    onSessionClick: (SessionPerformance) -> Unit
) {
    // Tri par date
    val sortedPerformances = performances.sortedByDescending { it.date }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 colonnes pour les cartes
        modifier = Modifier.padding(8.dp)
    ) {
        items(sortedPerformances.size) { index ->
            SessionCard(performance = sortedPerformances[index], onClick = { onSessionClick(sortedPerformances[index]) })
        }
    }
}

@Composable
fun SessionCard(performance: SessionPerformance, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Utilisation d'un bloc try pour gérer les cas où la conversion échoue ou les données sont incorrectes
            val dateString = try {
                val date = performance.date// S'assure que performance.date est un Long
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            } catch (e: Exception) {
                "Invalid date"  // Un message ou un traitement d'erreur si la date est invalide
            }

            Text(
                text = "Session Date: $dateString",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Exercises: ${performance.exercisesPerformed.size}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
