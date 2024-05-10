package fr.isen.gomez.untilfailure

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class ConnexionActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setContent {
            UntilFailureTheme {
                ConnexionScreen(auth)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnexionScreen(auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Red background for the whole screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red), // Red background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Title "Connexion" centered
            Text(
                text = "Connexion",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Custom outlined text field function
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
                        containerColor = Color.White, // White background for the field
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            // Apply reusable input function to various fields
            OutlinedInput(email, { email = it }, "Email")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedInput(password, { password = it }, "Mot de passe", isPassword = true)

            Spacer(modifier = Modifier.height(24.dp))

            // Sign-in button
            Button(
                onClick = {
                    signInWithEmailAndPassword(auth, email, password, context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Set to green color
            ) {
                Text("Se connecter", color = Color.White)
            }
        }
    }
}

private fun signInWithEmailAndPassword(auth: FirebaseAuth, email: String, password: String, context: Context) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(context, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Connexion réussie, rediriger vers ProfileUserActivity
                val intent = Intent(context, ProfileUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            } else {
                when {
                    task.exception is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(context, "Le mot de passe est incorrect ou l'e-mail est mal formé.", Toast.LENGTH_SHORT).show()
                    }
                    task.exception is FirebaseAuthInvalidUserException -> {
                        Toast.makeText(context, "Aucun utilisateur trouvé avec cet e-mail ou compte désactivé.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Échec de la connexion: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}

