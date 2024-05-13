package fr.isen.gomez.untilfailure.data

import com.google.firebase.database.PropertyName
import java.util.Date

// Modèle pour les détails de l'exercice effectué
data class ExercisePerformed(
    val exerciseId: String, // Référence à l'ID de l'exercice dans la classe Exercise
    val weightLifted: Double,
    val repetitions: Int
)

// Modèle pour la performance de session, utilisant des références à Exercise
data class SessionPerformance(
    @PropertyName("date") val date: Date,
    @PropertyName("exercisesPerformed") val exercisesPerformed: List<ExercisePerformed>
)

// Classe pour décrire les exercices disponibles
data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val imageId: Int // Référence à R.drawable.*
)
