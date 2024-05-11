package fr.isen.gomez.untilfailure.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import fr.isen.gomez.untilfailure.model.AccueilActivity

class MainViewModel : ViewModel() {
    fun navigateToAccueilActivity(context: Context) {
        val intent = Intent(context, AccueilActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}