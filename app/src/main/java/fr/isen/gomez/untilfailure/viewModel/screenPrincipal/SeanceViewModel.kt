package fr.isen.gomez.untilfailure.viewModel.screenPrincipal


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.data.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeanceViewModel : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises = _exercises.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            // Ici, vous pourriez charger les données depuis Firebase
            _exercises.value = listOf(
                Exercise("1", "Squat", "Description for Squat", R.drawable.squat),
                Exercise("2", "Bench", "Description for Bench", R.drawable.bench),
                Exercise("3", "Deadlift", "Description for Deadlift", R.drawable.deadlift)
            )
        }
    }

}

