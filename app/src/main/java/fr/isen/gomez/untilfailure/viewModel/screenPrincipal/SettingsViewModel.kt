package fr.isen.gomez.untilfailure.viewModel.screenPrincipal

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("user")

    var objective = mutableStateOf("")
    var userName = mutableStateOf("")

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        usersRef.child(currentUser?.uid ?: "").get().addOnSuccessListener { snapshot ->
            objective.value = snapshot.child("objectif").value?.toString() ?: "Default Value"
            userName.value = snapshot.child("prenom").value?.toString() ?: "Utilisateur"  // Assurez-vous que "nom" est la clé correcte dans Firebase.
        }
    }

    fun updateUserObjective(newObjective: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            usersRef.child(currentUser.uid).child("objectif").setValue(newObjective)
                .addOnSuccessListener {
                    Log.d("UpdateObjective", "Objective successfully updated.")
                }
                .addOnFailureListener {
                    Log.e("UpdateObjective", "Failed to update objective.", it)
                }
        }
    }

    fun updateUserPassword(newPassword: String) {
        val currentUser = auth.currentUser
        currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("UpdatePassword", "User password updated.")
            } else {
                Log.e("UpdatePassword", "Failed to update password.")
            }
        }
    }
}
