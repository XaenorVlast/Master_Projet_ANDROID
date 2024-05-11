package fr.isen.gomez.untilfailure.model.firstPart


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import fr.isen.gomez.untilfailure.aspect.firstPart.InscriptionScreen

import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme
import fr.isen.gomez.untilfailure.viewModel.firstPart.InscriptionViewModel

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
