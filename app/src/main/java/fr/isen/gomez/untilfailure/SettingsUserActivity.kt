package fr.isen.gomez.untilfailure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class SettingsUserActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            UntilFailureTheme {
                SettingsScreen(auth)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(auth: FirebaseAuth) {
    val context = LocalContext.current
    val currentUser = auth.currentUser

    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var objectif by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("user")

    // Charger l'objectif actuel de l'utilisateur
    LaunchedEffect(currentUser) {
        currentUser?.email?.let { email ->
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val currentObjectif = childSnapshot.child("objectif").getValue(String::class.java)
                            objectif = currentObjectif ?: ""
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Fonction pour les champs de texte
    @Composable
    fun OutlinedInput(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        isPassword: Boolean = false
    ) {
        val textColor = MaterialTheme.colorScheme.onSurface
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 200.dp), // Ajuste la largeur ici selon tes préférences
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            )
        )
    }

    // Interface des paramètres utilisateur
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.background(Color.Gray)
        ) {
            Text(
                text = "Paramètres Utilisateur",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            OutlinedInput(email, { email = it }, "Email")
            OutlinedInput(objectif, { objectif = it }, "Objectif")
            OutlinedInput(currentPassword, { currentPassword = it }, "Mot de Passe Actuel", isPassword = true)
            OutlinedInput(newPassword, { newPassword = it }, "Nouveau Mot de Passe", isPassword = true)
            OutlinedInput(confirmNewPassword, { confirmNewPassword = it }, "Confirmer Nouveau Mot de Passe", isPassword = true)

            Button(
                onClick = {
                    updateUserSettings(auth, email, currentPassword, newPassword, confirmNewPassword, objectif, usersRef, context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Enregistrer", color = Color.White)
            }
        }

        // Ajout des boutons footer en bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterButton(R.drawable.historiqueperformances) {
                val intent = Intent(context, DataPerformancesActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }
            FooterButton(R.drawable.home) {
                val intent = Intent(context, ProfileUserActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }
            FooterButton(R.drawable.seance) {
                val intent = Intent(context, SeanceActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }
}


private fun updateUserSettings(
    auth: FirebaseAuth,
    email: String,
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String,
    objectif: String,
    usersRef: DatabaseReference,
    context: Context
) {
    val currentUser = auth.currentUser
    val credentials = EmailAuthProvider.getCredential(currentUser?.email ?: "", currentPassword)

    if (newPassword != confirmNewPassword) {
        Toast.makeText(context, "Les nouveaux mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show()
        return
    }

    currentUser?.reauthenticate(credentials)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            currentUser.updateEmail(email).addOnCompleteListener { emailTask ->
                if (emailTask.isSuccessful) {
                    currentUser.updatePassword(newPassword).addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            // Mise à jour des données dans Firebase Realtime Database
                            val query = usersRef.orderByChild("email").equalTo(currentUser.email)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (childSnapshot in snapshot.children) {
                                            val key = childSnapshot.key
                                            key?.let {
                                                usersRef.child(it).child("email").setValue(email)
                                                usersRef.child(it).child("objectif").setValue(objectif)
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                                }
                            })
                            Toast.makeText(context, "Mise à jour réussie!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Échec de la mise à jour du mot de passe.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Échec de la mise à jour de l'e-mail.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Authentification échouée.", Toast.LENGTH_SHORT).show()
        }
    }
}
