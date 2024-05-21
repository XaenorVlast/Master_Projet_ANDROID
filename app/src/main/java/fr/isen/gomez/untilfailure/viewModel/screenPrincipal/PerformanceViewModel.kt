package fr.isen.gomez.untilfailure.viewModel.screenPrincipal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import fr.isen.gomez.untilfailure.data.Series
import fr.isen.gomez.untilfailure.data.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PerformanceViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout

    private val _comparisonWorkouts = MutableStateFlow<Pair<Workout?, Workout?>>(null to null)
    val comparisonWorkouts: StateFlow<Pair<Workout?, Workout?>> = _comparisonWorkouts

    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> = _errorMessages

    fun loadWorkoutsByType(userId: String, exerciseType: String) {
        viewModelScope.launch {
            try {
                val workoutsRef = database.getReference("user/$userId/workouts")
                val query = workoutsRef.orderByChild("type").equalTo(exerciseType)
                val snapshot = query.get().await()
                val workouts = snapshot.children.mapNotNull {
                    val workout = it.getValue<Workout>()
                    workout?.workoutId = it.key.toString()  // Ensure the workout ID is set
                    workout
                }.sortedBy { it.date }
                _workouts.value = workouts
            } catch (e: Exception) {
                _errorMessages.value = "Error loading workouts by type: ${e.message}"
                Log.e("fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel", "Error loading workouts by type: ${e.message}", e)
            }
        }
    }

    fun deselectWorkout() {
        _selectedWorkout.value = null
    }
    fun loadWorkoutDetails(userId: String, workoutId: String) {
        viewModelScope.launch {
            try {
                val workoutRef = database.getReference("user/$userId/workouts/$workoutId")
                val snapshot = workoutRef.get().await()
                Log.d("PerformanceViewModel", "Firebase Data: ${snapshot.value}") // Log the raw Firebase data

                val workout = snapshot.getValue<Workout>()?.apply {
                    this.workoutId = snapshot.key.toString()
                    this.series = snapshot.child("sets").children.mapNotNull { setSnapshot ->
                        Series(
                            validReps = setSnapshot.child("valid_reps").getValue<Int>() ?: 0,
                            invalidReps = setSnapshot.child("invalid_reps").getValue<Int>() ?: 0,
                            weight = setSnapshot.child("weight").getValue<Double>() ?: 0.0,
                            seriesNumber = setSnapshot.key?.removePrefix("set")?.toIntOrNull() ?: 0
                        )
                    }
                }
                if (workout != null) {
                    _selectedWorkout.value = workout
                } else {
                    Log.e("PerformanceViewModel", "No workout found with ID: $workoutId")
                }
            } catch (e: Exception) {
                Log.e("PerformanceViewModel", "Error loading workout details: ${e.message}", e)
            }
        }
    }





    fun selectWorkout(workout: Workout) {
        if (workout.workoutId.isNullOrEmpty()) {
            Log.e("fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel", "Error: Selected workout ID is null or empty")
        } else {
            Log.d("fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel", "Selecting workout with ID: ${workout.workoutId}")
            _selectedWorkout.value = workout

        }
    }

    fun selectComparisonWorkouts(firstWorkoutId: String, secondWorkoutId: String, userId: String) {
        viewModelScope.launch {
            Log.d("PerformanceViewModel", "Initiating comparison between workouts $firstWorkoutId and $secondWorkoutId for user $userId")
            try {
                // Load details of the first workout
                loadWorkoutDetailsAsync(userId, firstWorkoutId).let { firstWorkout ->
                    // Check if the first workout was successfully loaded
                    if (firstWorkout == null) {
                        Log.w("PerformanceViewModel", "Failed to load workout with ID $firstWorkoutId.")
                        _errorMessages.value = "Failed to load workout with ID $firstWorkoutId."
                        return@launch
                    }

                    // Load details of the second workout
                    loadWorkoutDetailsAsync(userId, secondWorkoutId).let { secondWorkout ->
                        // Check if the second workout was successfully loaded
                        if (secondWorkout == null) {
                            Log.w("PerformanceViewModel", "Failed to load workout with ID $secondWorkoutId.")
                            _errorMessages.value = "Failed to load workout with ID $secondWorkoutId."
                            return@launch
                        }

                        // Set the comparison workouts if both are successfully loaded
                        _comparisonWorkouts.value = firstWorkout to secondWorkout
                        Log.d("PerformanceViewModel", "Successfully set comparison workouts.")
                    }
                }
            } catch (e: Exception) {
                Log.e("PerformanceViewModel", "Error selecting comparison workouts: ${e.localizedMessage}", e)
                _errorMessages.value = "Error selecting comparison workouts: ${e.localizedMessage}"
            }
        }
    }

    // A modified version of loadWorkoutDetails to return the workout instead of setting it in a state variable
    private suspend fun loadWorkoutDetailsAsync(userId: String, workoutId: String): Workout? {
        return try {
            val workoutRef = database.getReference("user/$userId/workouts/$workoutId")
            val snapshot = workoutRef.get().await()
            snapshot.getValue<Workout>()?.apply {
                this.workoutId = snapshot.key.toString()
                this.series = snapshot.child("sets").children.mapNotNull { setSnapshot ->
                    Series(
                        validReps = setSnapshot.child("valid_reps").getValue<Int>() ?: 0,
                        invalidReps = setSnapshot.child("invalid_reps").getValue<Int>() ?: 0,
                        weight = setSnapshot.child("weight").getValue<Double>() ?: 0.0,
                        seriesNumber = setSnapshot.key?.removePrefix("set")?.toIntOrNull() ?: 0
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PerformanceViewModel", "Error loading workout details: ${e.message}", e)
            null
        }
    }

}
