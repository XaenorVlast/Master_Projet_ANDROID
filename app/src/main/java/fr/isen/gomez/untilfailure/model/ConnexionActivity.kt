package fr.isen.gomez.untilfailure.model

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import fr.isen.gomez.untilfailure.aspect.ConnexionScreen
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme
import fr.isen.gomez.untilfailure.viewModel.ConnexionViewModel

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
