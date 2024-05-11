package fr.isen.gomez.untilfailure.aspect.firstPart

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.model.firstPart.AccueilActivity
import fr.isen.gomez.untilfailure.viewModel.firstPart.InscriptionViewModel


@Composable
fun InscriptionScreen(viewModel: InscriptionViewModel) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var objectif by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Red)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Inscription", style = MaterialTheme.typography.headlineLarge)

                Spacer(Modifier.height(16.dp))
                OutlinedInput(nom, { nom = it }, "Nom")
                OutlinedInput(prenom, { prenom = it }, "Prénom")
                OutlinedInput(email, { email = it }, "Email")
                OutlinedInput(password, { password = it }, "Mot de passe", true)
                OutlinedInput(age, { age = it }, "Âge")
                OutlinedInput(objectif, { objectif = it }, "Objectif")

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.registerUser(nom, prenom, email, password, age, objectif, context) { success ->
                            if (success) {
                                Toast.makeText(context, "Inscription réussie!", Toast.LENGTH_LONG).show()
                                context.startActivity(Intent(context, AccueilActivity::class.java))
                            } else {
                                Toast.makeText(context, "Échec de l'inscription.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text("S'inscrire")
                }
            }
        }
    )
}