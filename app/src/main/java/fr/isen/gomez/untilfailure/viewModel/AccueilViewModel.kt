package fr.isen.gomez.untilfailure.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import fr.isen.gomez.untilfailure.model.ConnexionActivity
import fr.isen.gomez.untilfailure.model.InscriptionActivity

class AccueilViewModel : ViewModel() {

    // Fonction pour naviguer vers ConnexionActivity
    fun navigateToConnexion(context: Context) {
        val intent = Intent(context, ConnexionActivity::class.java)
        context.startActivity(intent)
    }

    // Fonction pour naviguer vers InscriptionActivity
    fun navigateToInscription(context: Context) {
        val intent = Intent(context, InscriptionActivity::class.java)
        context.startActivity(intent)
    }
}