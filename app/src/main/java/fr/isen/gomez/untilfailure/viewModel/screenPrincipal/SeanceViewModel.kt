package fr.isen.gomez.untilfailure.viewModel.screenPrincipal


import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.data.Exercise
import fr.isen.gomez.untilfailure.data.nfc.NfcState
import fr.isen.gomez.untilfailure.data.nfc.OnTagDiscoveryCompletedListener
import fr.isen.gomez.untilfailure.data.nfc.TagHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeanceViewModel : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises = _exercises.asStateFlow()
    private val _nfcState = MutableStateFlow<NfcState>(NfcState.Available)
    val nfcState = _nfcState.asStateFlow()

    private val _tagName = MutableStateFlow<String>("")
    val tagName = _tagName.asStateFlow()
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

    fun checkNfcAvailability(nfcAdapter: NfcAdapter?) {
        if (nfcAdapter == null) {
            _nfcState.value = NfcState.NotAvailable
        } else {
            _nfcState.value = NfcState.Available
        }
    }

    fun discoverTag(tag: Tag, listener: OnTagDiscoveryCompletedListener) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                // Logique pour lire les données du tag ici
                tag
            }
            listener.onTagDiscoveryCompleted(result, TagHelper.ProductID())  // Assurez-vous que TagHelper.ProductID() est correctement implémenté
        }
    }


    fun updateTagInfo(tagName: String) {
        _tagName.value = tagName  // Met à jour l'état du tag name dans le ViewModel
    }

}



