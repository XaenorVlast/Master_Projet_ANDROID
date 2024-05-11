package fr.isen.gomez.untilfailure.aspect.commun

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.gomez.untilfailure.model.screenPrincipal.ButtonInfo
import fr.isen.gomez.untilfailure.model.screenPrincipal.DataPerformancesActivity
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.model.screenPrincipal.SeanceActivity
import fr.isen.gomez.untilfailure.model.screenPrincipal.SettingsUserActivity
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.ExerciseScreen
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.PerformanceScreen
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.ProfileScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val buttons = listOf(
        ButtonInfo(R.drawable.historiqueperformances, DataPerformancesActivity::class.java, "Historique performances"),
        ButtonInfo(R.drawable.seance, SeanceActivity::class.java, "Séance"),
        ButtonInfo(R.drawable.parametresutilisateur, SettingsUserActivity::class.java, "Paramètres utilisateur")
    )

    Scaffold(
        bottomBar = { FooterMenu(navController, buttons) }
    ) {
        NavHost(navController, startDestination = "exercise") {
            composable("exercise") { ExerciseScreen() }
            composable("performance") { PerformanceScreen() }
            composable("settings") { ProfileScreen() }
        }
    }
}

@Composable
fun FooterMenu(navController: NavController, buttons: List<ButtonInfo>) {
    val currentRoute = navController.currentDestination?.route ?: buttons.first().contentDescription

    NavigationBar {
        buttons.forEach { button ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = button.icon), contentDescription = button.contentDescription) },
                label = { Text(button.contentDescription) },
                selected = currentRoute == button.contentDescription,
                onClick = {
                    val route = when (button.activityClass.simpleName) {
                        "DataPerformancesActivity" -> "performance"
                        "SeanceActivity" -> "exercise"
                        "SettingsUserActivity" -> "settings"
                        else -> navController.graph.startDestinationRoute ?: "exercise"
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}