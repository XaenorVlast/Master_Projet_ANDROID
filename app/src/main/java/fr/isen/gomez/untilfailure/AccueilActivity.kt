package fr.isen.gomez.untilfailure

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class AccueilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                AccueilScreen()
            }
        }
    }
}

@Composable
fun AccueilScreen() {
    val context = LocalContext.current

    // Red background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the transparent logo at the top of the screen
        Image(
            painter = painterResource(id = R.drawable.untilfailure), // Ensure this resource is transparent
            contentDescription = "Until Failure Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Button for Connexion
        ActionButton("Connexion") {
            val intent = Intent(context, ConnexionActivity::class.java)
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button for Inscription
        ActionButton("Inscription") {
            val intent = Intent(context, InscriptionActivity::class.java)
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Display the dumbbell image at the bottom
        Image(
            painter = painterResource(id = R.drawable.dumbbell),
            contentDescription = "Dumbbell",
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
