package fr.isen.gomez.untilfailure.model.firstPart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import fr.isen.gomez.untilfailure.aspect.firstPart.ConnexionScreen
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme
import fr.isen.gomez.untilfailure.viewModel.firstPart.ConnexionViewModel

class ConnexionActivity : ComponentActivity() {
    private val viewModel: ConnexionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                ConnexionScreen(viewModel)
            }
        }
    }
}
