package fr.isen.gomez.untilfailure.model


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import fr.isen.gomez.untilfailure.aspect.InscriptionScreen

import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme
import fr.isen.gomez.untilfailure.viewModel.InscriptionViewModel

class InscriptionActivity : ComponentActivity() {
    private val viewModel: InscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                InscriptionScreen(viewModel)
            }
        }
    }
}
