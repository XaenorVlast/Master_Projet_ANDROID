package fr.isen.gomez.untilfailure

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class DataPerformancesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DataPerformancesScreen()
                }
            }
        }
    }
}

@Composable
fun DataPerformancesScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Black
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            ) {
                Text(
                    text = "Historique des séances",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            ) {
                Text(
                    text = "Graphiques",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterButton(R.drawable.seance) {
                val intent = Intent(context, SeanceActivity::class.java)
                startActivity(context, intent, null)
            }
            FooterButton(R.drawable.home) {
                val intent = Intent(context, ProfileUserActivity::class.java)
                startActivity(context, intent, null)
            }
            FooterButton(R.drawable.parametresutilisateur) {
                val intent = Intent(context, SettingsUserActivity::class.java)
                startActivity(context, intent, null)
            }
        }
    }
}

@Composable
fun FooterButton(imageResId: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}
