import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextFieldDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.SettingsViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
        val objective = viewModel.objective.value
        val newPassword = remember { mutableStateOf("") }
        val userName = viewModel.userName.value  // Utiliser la nouvelle variable d'état

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.the_rock),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(40.dp))  // Apply rounded corners
                    //.border(1.dp, Color.Red, RoundedCornerShape(10.dp))  // Apply red border
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Bienvenue $userName", style = TextStyle(color = Color.Black, fontSize = 24.sp))  // Afficher le message de bienvenue
            OutlinedTextField(
                value = objective,
                onValueChange = { viewModel.objective.value = it },
                label = { Text("Objective") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                    cursorColor = Color.Red
                ),
                textStyle = TextStyle(color = Color.Red, fontSize = 18.sp)
            )
            OutlinedTextField(
                value = newPassword.value,
                onValueChange = { newPassword.value = it },
                label = { Text("New Password") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                    cursorColor = Color.Red
                ),
                textStyle = TextStyle(color = Color.Red, fontSize = 18.sp)
            )
            Button(
                onClick = {
                    viewModel.updateUserObjective(objective)  // Update user objective as before
                    if (newPassword.value.isNotEmpty()) {     // Check if the new password is not empty
                        viewModel.updateUserPassword(newPassword.value)  // Update the password only if it's not empty
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)  // Set button color to red
            )

            {
                Text("Update", color = Color.White)
            }

        }
    }
