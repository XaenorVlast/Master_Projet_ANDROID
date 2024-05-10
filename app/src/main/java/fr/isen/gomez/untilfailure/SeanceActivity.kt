package fr.isen.gomez.untilfailure

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class SeanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                SeanceScreen()
            }
        }
    }
}

@Composable
fun SeanceScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre "Choisissez une séance : " en rouge gras
        Text(
            text = "Choisissez une séance : ",
            color = Color.Red,
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )

        // Boutons "Squat", "Bench", "Deadlift"
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorButton(
                onClick = { /* Action Squat */ },
                imageId = R.drawable.squat,
                text = "Squat",
                contentColor = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ColorButton(
                onClick = { /* Action Bench */ },
                imageId = R.drawable.bench,
                text = "Bench",
                contentColor = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ColorButton(
                onClick = { /* Action Deadlift */ },
                imageId = R.drawable.deadlift,
                text = "Deadlift",
                contentColor = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

        // Bouton NFC avec une image NFC
        ColorButton(
            onClick = { /* Action Connexion NFC */ },
            imageId = R.drawable.nfc,
            text = "Connexion NFC",
            contentColor = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Ajout d'un espace flexible pour pousser les boutons footer vers le bas
        Spacer(modifier = Modifier.weight(1f))

        // Ajout des boutons footer
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterButton(R.drawable.parametresutilisateur) {
                val intent = Intent(context, SettingsUserActivity::class.java)
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


@Composable
fun ColorButton(
    onClick: () -> Unit,
    imageId: Int? = null,
    text: String,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .width(300.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            imageId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp), // Augmenter la taille de l'image
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 24.sp, // Augmenter la taille du texte
                style = TextStyle(fontSize = 24.sp)
            )
        }
    }
}

