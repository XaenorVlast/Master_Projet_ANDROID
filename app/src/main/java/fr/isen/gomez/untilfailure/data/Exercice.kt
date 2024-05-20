package fr.isen.gomez.untilfailure.data

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.util.Date

// Modèle pour les détails de l'exercice effectué
@IgnoreExtraProperties
data class Workout(
    var workoutId: String = "",
    val date: String = "",
    val type: String = "",
    var series: List<Series> = emptyList()
)

@IgnoreExtraProperties
data class Series(
    var seriesNumber: Int = 0,
    val validReps: Int = 0,
    val invalidReps: Int = 0,
    val weight: Double = 0.0
)

// Classe pour décrire les exercices disponibles
data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val imageId: Int // Référence à R.drawable.*
)
