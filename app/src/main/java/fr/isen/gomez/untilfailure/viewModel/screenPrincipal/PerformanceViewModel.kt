package fr.isen.gomez.untilfailure.viewModel.screenPrincipal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.isen.gomez.untilfailure.data.SessionPerformance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class PerformanceViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().getReference("performances")

    private val _performances = MutableStateFlow<List<SessionPerformance>>(emptyList())
    val performances: StateFlow<List<SessionPerformance>> = _performances.asStateFlow()

    init {
        viewModelScope.launch {
            fetchPerformances()
        }
    }

    private fun fetchPerformances() = viewModelScope.launch {
        getPerformanceUpdates().collect { updatedPerformances ->
            _performances.value = updatedPerformances
        }
    }

    private fun getPerformanceUpdates() = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPerformances = snapshot.children.mapNotNull { it.getValue<SessionPerformance>() }
                trySend(newPerformances).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        dbRef.addValueEventListener(listener)
        awaitClose { dbRef.removeEventListener(listener) }
    }
}
