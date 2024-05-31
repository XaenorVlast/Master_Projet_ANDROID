package fr.isen.gomez.untilfailure.aspect.firstPart

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.model.screenPrincipal.EcranPrincipalActivity
import fr.isen.gomez.untilfailure.viewModel.firstPart.ConnexionViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ConnexionScreen(viewModel: ConnexionViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Black,
        content = { //padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(padding)
                    .background(Color.Black) , // Adaptez la couleur selon votre thème
                    //.padding(32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.connexion_image),
                    contentDescription = "Connexion Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black), startY = 100f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = "Connexion",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedInput(value = email, onValueChange = { email = it }, label = "Email")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedInput(value = password, onValueChange = { password = it }, label = "Mot de passe", isPassword = true)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.signIn(email, password, {
                            context.startActivity(Intent(context, EcranPrincipalActivity::class.java))
                        }, { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
                ) {
                    Text("Se connecter", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedInput(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White, // Arrière-plan blanc
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.LightGray
        )
    )
}