package fr.isen.gomez.untilfailure

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UntilFailureTheme {
                launchAccueilActivity()
            }
        }
    }

    private fun launchAccueilActivity() {
        val intent = Intent(this, AccueilActivity::class.java)
        startActivity(intent)
    }
}