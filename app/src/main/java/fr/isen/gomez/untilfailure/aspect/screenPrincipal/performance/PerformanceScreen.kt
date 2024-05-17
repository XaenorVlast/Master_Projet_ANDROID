package fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PerformanceScreen(viewModel: PerformanceViewModel) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Tracker") },
                actions = {
                    // Bouton pour revenir à la liste principale des workouts
                    Button(onClick = { navController.navigate("workoutList") }) {
                        Text("Workouts")
                    }
                    // Bouton pour aller à l'écran de comparaison
                    Button(onClick = { navController.navigate("comparison") }) {
                        Text("Compare")
                    }
                }
            )
        }
    ) {
        NavHost(navController, startDestination = "workoutList") {
            composable("workoutList") {
        //        WorkoutListScreen(viewModel, navController)
            }
            composable("workoutDetail/{workoutId}") { backStackEntry ->
                // Assurez-vous que WorkoutDetailScreen est défini et capable de gérer l'ID
             //   WorkoutDetailScreen(backStackEntry.arguments?.getString("workoutId"), viewModel)
            }
            composable("comparison") {
                // Assurez-vous que ComparisonScreen est défini
            //    ComparisonScreen(viewModel)
            }
        }
    }
}
