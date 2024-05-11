package fr.isen.gomez.untilfailure.model

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme
import fr.isen.gomez.untilfailure.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                viewModel.navigateToAccueilActivity(this)
            }
        }
    }
}