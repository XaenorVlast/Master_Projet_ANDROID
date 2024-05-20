import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance.PerformanceGraphScreen
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel

@Composable
fun PerformanceScreen(viewModel: PerformanceViewModel) {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser?.uid

    NavHost(navController, startDestination = if (user != null) "ExerciseTypeSelectionScreen" else "LoginScreen") {
        composable("ExerciseTypeSelectionScreen") {
            if (user != null) {
                ExerciseTypeSelectionScreen(navController, viewModel, user)
            } else {
                Text("Please log in to view this content")
            }
        }
        composable("DataPerformanceScreen/{exerciseType}") { backStackEntry ->
            if (user != null) {
                val exerciseType = backStackEntry.arguments?.getString("exerciseType") ?: "Unknown"
                DataPerformancesScreen(viewModel, user, exerciseType, navController)
            }
        }
        composable("LoginScreen") {
            Text("Please log in to access the application.")
        }
    }
}

@Composable
fun DataPerformancesScreen(viewModel: PerformanceViewModel, userId: String, exerciseType: String, navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            val tabs = listOf("Session Details", "Performance Graph")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = TextStyle(fontSize = 20.sp)) }
                )
            }
        }

        // Dynamically display the appropriate screen based on the selected tab index
        when (selectedTabIndex) {
            0 -> SessionSelectionScreen(viewModel, userId, exerciseType) // Display session selection
            1 -> PerformanceGraphScreen(viewModel, userId, exerciseType) // Display performance graph
        }
    }
}

@Composable
fun ExerciseTypeSelectionScreen(navController: NavHostController, viewModel: PerformanceViewModel, userId: String) {
    Column {
        listOf("Bench", "Deadlift", "Squat").forEach { exercise ->
            Button(onClick = {
                navController.navigate("DataPerformanceScreen/$exercise")
            }) {
                Text("Select $exercise")
            }
        }
    }
}
