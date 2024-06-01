package fr.isen.gomez.untilfailure.aspect.firstPart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.viewModel.firstPart.AccueilViewModel

@Composable
fun AccueilScreen(viewModel: AccueilViewModel) {
    val context = LocalContext.current

    // UI redéfinie avec Material3
    Scaffold(
        containerColor = Color.Black,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(padding)
                    .background(Color.Black),
                    //.padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.acceuil_image),
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)  // Adjust this height to manage the space for the image
                )

                Text(
                    "LIMITBREAKER",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.limitbreaker_logo),
                    contentDescription = "LimitBreaker Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                ActionButton("Connexion", Color(0xFFFF0000)) {
                    viewModel.navigateToConnexion(context)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ActionButton("Inscription", Color(0xFFFF0000)) {
                    viewModel.navigateToInscription(context)
                }

                Spacer(modifier = Modifier.weight(1f))

                /*Image(
                    painter = painterResource(id = R.drawable.dumbbell),
                    contentDescription = "Dumbbell",
                    modifier = Modifier.padding(vertical = 16.dp)
                )*/
            }
        }
    )
}

@Composable
fun ActionButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
    }
}