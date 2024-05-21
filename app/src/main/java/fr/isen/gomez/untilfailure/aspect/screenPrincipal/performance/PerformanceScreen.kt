import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance.PerformanceGraphScreen
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel

@Composable
fun PerformanceScreen(viewModel: PerformanceViewModel) {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser?.uid

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Red,
            onPrimary = Color.White,
            secondary = Color.White,
            onSecondary = Color.Gray,
            background = Color.White,
            onBackground = Color.Black
        )
    ) {
        NavHost(navController, startDestination = if (user != null) "ExerciseTypeSelectionScreen" else "LoginScreen") {
            composable("ExerciseTypeSelectionScreen") {
                if (user != null) {
                    ExerciseTypeSelectionScreen(navController, viewModel, user)
                } else {
                    Text("Veuillez vous connecter pour accéder à ce contenu", style = MaterialTheme.typography.bodyLarge)
                }
            }
            composable("DataPerformanceScreen/{exerciseType}") { backStackEntry ->
                if (user != null) {
                    val exerciseType = backStackEntry.arguments?.getString("exerciseType") ?: "Unknown"
                    DataPerformancesScreen(viewModel, user, exerciseType, navController)
                }
            }
            composable("LoginScreen") {
                Text("Veuillez vous connecter pour accéder à l'application.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun DataPerformancesScreen(viewModel: PerformanceViewModel, userId: String, exerciseType: String, navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val backStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(backStackEntry.value) {
        selectedTabIndex = 0
        viewModel.loadWorkoutsByType(userId, exerciseType)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                val tabs = listOf("Détails des séances", "Graphique de performance")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> SessionSelectionScreen(viewModel, userId, exerciseType)
                1 -> PerformanceGraphScreen(viewModel, userId, exerciseType)
            }
        }
    }
}

@Composable
fun ExerciseTypeSelectionScreen(navController: NavHostController, viewModel: PerformanceViewModel, userId: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Prêt à battre vos records ? Sélectionnez une discipline.",
            style = TextStyle(color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        listOf("Bench", "Deadlift", "Squat").forEach { exercise ->
            Button(
                onClick = { navController.navigate("DataPerformanceScreen/$exercise") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(exercise, style = TextStyle(fontSize = 18.sp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
