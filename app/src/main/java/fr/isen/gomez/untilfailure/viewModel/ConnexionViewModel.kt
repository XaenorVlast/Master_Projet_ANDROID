package fr.isen.gomez.untilfailure.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ConnexionViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    init {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        auth = FirebaseAuth.getInstance()
    }

    fun signIn(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onError("Veuillez remplir tous les champs.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let {
                        when (it) {
                            is FirebaseAuthInvalidCredentialsException ->
                                onError("Le mot de passe est incorrect ou l'e-mail est mal formé.")
                            is FirebaseAuthInvalidUserException ->
                                onError("Aucun utilisateur trouvé avec cet e-mail ou compte désactivé.")
                            else ->
                                onError("Échec de la connexion: ${it.localizedMessage}")
                        }
                    }
                }
            }
    }
}
