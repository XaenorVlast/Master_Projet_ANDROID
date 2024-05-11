package fr.isen.gomez.untilfailure.aspect

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(onNavigate: () -> Unit) {
    // Votre UI ici
    Button(onClick = onNavigate) {
        Text("Accéder à l'accueil")
    }
}