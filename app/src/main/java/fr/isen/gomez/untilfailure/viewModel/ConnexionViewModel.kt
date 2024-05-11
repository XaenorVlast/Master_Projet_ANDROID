package fr.isen.gomez.untilfailure.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ConnexionViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
