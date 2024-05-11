package fr.isen.gomez.untilfailure.model.screenPrincipal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.ui.theme.UntilFailureTheme

class ProfileUserActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            UntilFailureTheme {
                ProfileScreen(auth)
            }
        }
    }
}

data class ButtonInfo(val icon: Int, val activityClass: Class<*>, val contentDescription: String)

val buttons = listOf(
    ButtonInfo(R.drawable.historiqueperformances, DataPerformancesActivity::class.java, "Historique performances"),
    ButtonInfo(R.drawable.seance, SeanceActivity::class.java, "Séance"),
    ButtonInfo(R.drawable.parametresutilisateur, SettingsUserActivity::class.java, "Paramètres utilisateur")
)

@Composable
fun ProfileScreen(auth: FirebaseAuth) {
    var userFirstName by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userObjective by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchUserData(auth,
            onSuccess = { firstName, age, objective ->
                userFirstName = firstName
                userAge = age
                userObjective = objective
            },
            onError = {
                Toast.makeText(context, "Erreur lors du chargement des données utilisateur", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column {
        ProfileHeader(userFirstName, userAge, userObjective)
        Spacer(modifier = Modifier.weight(1f))
        FooterSection(context)
    }
}

@Composable
fun ProfileHeader(userFirstName: String, userAge: String, userObjective: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profilutilisateur),
            contentDescription = "Photo de profil",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = userFirstName,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Age: $userAge",
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Objectif: $userObjective",
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun FooterSection(context: android.content.Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { button ->
            FooterButton(
                image = painterResource(id = button.icon),
                onClick = { navigateToActivity(context, button.activityClass) },
                contentDescription = button.contentDescription
            )
        }
    }
}

@Composable
fun FooterButton(image: Painter, onClick: () -> Unit, contentDescription: String) {
    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp)) {
        Image(
            painter = image,
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp)
        )
    }
}

fun navigateToActivity(context: android.content.Context, activityClass: Class<*>) {
    val intent = Intent(context, activityClass)
    context.startActivity(intent)
}

fun fetchUserData(auth: FirebaseAuth, onSuccess: (String, String, String) -> Unit, onError: () -> Unit) {
    val currentUser = auth.currentUser
    val database = Firebase.database("https://untilfailure-ca9de-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("user")

    currentUser?.let { user ->
        usersRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val firstName = childSnapshot.child("prenom").getValue(String::class.java)
                        val age = childSnapshot.child("age").getValue(String::class.java)
                        val objective = childSnapshot.child("objectif").getValue(String::class.java)

                        if (firstName != null && age != null && objective != null) {
                            onSuccess(firstName, age, objective)
                        } else {
                            onError()
                        }
                    }
                } else {
                    onError()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError()
            }
        })
    } ?: run {
        onError()
    }
}
