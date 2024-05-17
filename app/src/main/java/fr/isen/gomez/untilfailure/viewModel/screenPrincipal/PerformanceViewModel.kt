package fr.isen.gomez.untilfailure.viewModel.screenPrincipal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.gomez.untilfailure.data.Series
import fr.isen.gomez.untilfailure.data.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

class PerformanceViewModel : ViewModel() {
    private val _workoutDetails = MutableStateFlow<Workout?>(null)
    val workoutDetails: StateFlow<Workout?> = _workoutDetails

    fun loadWorkoutDetails(userId: String, workoutId: String) {
        viewModelScope.launch {
            try {
                val database = FirebaseDatabase.getInstance("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
                val workoutRef = database.getReference("user/$userId/workouts/$workoutId")

                val snapshot = workoutRef.get().await()
                val workout = snapshot.getValue<Workout>()
                _workoutDetails.value = workout
            } catch (e: Exception) {
                // Gérer l'exception, par exemple en affichant un message d'erreur
                e.printStackTrace()
            }
        }
    }
}
