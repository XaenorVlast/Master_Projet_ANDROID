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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class InscriptionActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        setContent {
            UntilFailureTheme {
                InscriptionScreen(auth)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscriptionScreen(auth: FirebaseAuth) {
    val context = LocalContext.current
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var objectif by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("user")

    val isFormValid = remember(nom, prenom, mail, password) {
        nom.isNotEmpty() && prenom.isNotEmpty() && mail.contains("@") && password.length >= 8
    }

    // Créez un écran d'inscription
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Inscription",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Réutilisation de la fonction OutlinedInput
            @Composable
            fun OutlinedInput(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
                val textColor = MaterialTheme.colorScheme.onSurface
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = textColor) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White, // Arrière-plan blanc
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            // Formulaires pour les différents champs
            OutlinedInput(nom, { nom = it }, "Nom")
            OutlinedInput(prenom, { prenom = it }, "Prénom")
            OutlinedInput(age, { age = it }, "Âge")
            OutlinedInput(objectif, { objectif = it }, "Objectif")
            OutlinedInput(mail, { mail = it }, "Email")
            OutlinedInput(password, { password = it }, "Mot de passe", isPassword = true)

            // Bouton "S'inscrire"
            Button(
                onClick = {
                    createUserWithEmailAndPassword(auth, mail, password, nom, prenom, age, objectif, context) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Utilisateur enregistré avec succès!", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("S'inscrire", color = Color.White)
            }

            // Bouton "Se connecter"
            Button(
                onClick = {
                    context.startActivity(Intent(context, ConnexionActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Se connecter", color = Color.White)
            }
        }
    }
}



// Met à jour la fonction pour créer un utilisateur
private fun createUserWithEmailAndPassword(
    auth: FirebaseAuth,
    email: String,
    password: String,
    nom: String,
    prenom: String,
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
            // Utilise l'UID généré par Firebase Auth pour identifier les utilisateurs dans Realtime Database
            val userId = task.result?.user?.uid

            if (userId != null) {
                // Référence à la base de données Firebase, en utilisant l'URL personnalisée
                val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
                val usersRef = database.getReference("user").child(userId)

                // Créer un objet utilisateur sans le mot de passe
                val user = User(
                    nom = nom,
                    prenom = prenom,
                    age = age,
                    objectif = objectif,
                    email = email,
                    password = password
                )

                // Stocke les données utilisateur dans Firebase Database sous l'UID
                usersRef.setValue(user).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(context, "Utilisateur enregistré avec succès!", Toast.LENGTH_SHORT).show()
                        onComplete(true)

                        // Rediriger vers ProfileUserActivity
                        val intent = Intent(context, ProfileUserActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Échec de l'enregistrement des données: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }
                }
            }
        } else {
            // Gestion des exceptions Firebase
            when (task.exception) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(context, "L'adresse e-mail est mal formatée ou le mot de passe est trop court.", Toast.LENGTH_SHORT).show()
                }
                is FirebaseAuthUserCollisionException -> {
                    Toast.makeText(context, "L'adresse e-mail est déjà utilisée par un autre compte.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Échec de l'inscription: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
            onComplete(false)
        }
    }
}

data class User(
    val nom: String,
    val prenom: String,
    val age: String,
    val objectif: String,
    val email: String,
    val password: String
)