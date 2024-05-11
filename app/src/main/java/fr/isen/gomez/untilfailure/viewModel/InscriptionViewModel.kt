package fr.isen.gomez.untilfailure.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import fr.isen.gomez.untilfailure.data.User

class InscriptionViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    fun registerUser(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        age: String,
        objectif: String,
        context: Context,
        onComplete: (Boolean) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || nom.isBlank() || prenom.isBlank() || age.isBlank() || objectif.isBlank()) {
            Toast.makeText(context, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            onComplete(false)
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                userId?.let {
                    val user = User(nom, prenom, age, objectif, email, "") // Ne pas inclure le mot de passe ici
                    val database = Firebase.database("https://your-firebase-database-url.com")
                    database.getReference("users").child(it).setValue(user).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onComplete(true)
                        } else {
                            Toast.makeText(context, "Échec de l'enregistrement des données: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            onComplete(false)
                        }
                    }
                } ?: onComplete(false)
            } else {
                Toast.makeText(context, "Échec de l'inscription: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        }
    }
    }
