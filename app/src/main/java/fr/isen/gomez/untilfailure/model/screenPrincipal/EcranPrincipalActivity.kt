package fr.isen.gomez.untilfailure.model.screenPrincipal

import BLEScreen
import ScanViewModel
import SettingsScreen
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.gomez.untilfailure.R
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.SeanceScreen
import fr.isen.gomez.untilfailure.aspect.screenPrincipal.performance.PerformanceScreen
import fr.isen.gomez.untilfailure.viewModel.ble.PermissionsHelper
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.SeanceViewModel
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.SettingsViewModel


class EcranPrincipalActivity : ComponentActivity() {
    private lateinit var permissionsHelper: PermissionsHelper
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsHelper = PermissionsHelper(this)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                setContent { MainContent() }
            } else {
                // Handle the case where permissions are not granted
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (!permissionsHelper.hasAllPermissions()) {
            permissionsHelper.requestNeededPermissions(requestPermissionLauncher)
        } else {
            setContent { MainContent() }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            NavHost(navController, startDestination = "performance") {
                composable("performance") { PerformanceScreen(viewModel = ViewModelProvider(this@EcranPrincipalActivity)[PerformanceViewModel::class.java]) }
                composable("seance") { SeanceScreen(viewModel = ViewModelProvider(this@EcranPrincipalActivity)[SeanceViewModel::class.java]) }
                composable("settings") { SettingsScreen(viewModel = ViewModelProvider(this@EcranPrincipalActivity)[SettingsViewModel::class.java]) }
                composable("ble") {
                    BLEScreen(viewModel = viewModel<ScanViewModel>())
                }
            }
        }
    }


    @Composable
    fun BottomNavigationBar(navController: NavController) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBarItem(
                icon = { Image(painter = painterResource(id = R.drawable.home), contentDescription = "Accueil") },
                label = { Text("Accueil") },
                selected = currentDestination?.hierarchy?.any { it.route == "performance" } == true,
                onClick = { navController.navigate("performance") }
            )
            NavigationBarItem(
                icon = { Image(painter = painterResource(id = R.drawable.profilutilisateur), contentDescription = "Profil") },
                label = { Text("Profil") },
                selected = currentDestination?.hierarchy?.any { it.route == "seance" } == true,
                onClick = { navController.navigate("seance") }
            )
            NavigationBarItem(
                icon = { Image(painter = painterResource(id = R.drawable.bluetooth3), contentDescription = "ble") },
                label = { Text("ble") },
                selected = currentDestination?.hierarchy?.any { it.route == "ble" } == true,
                onClick = { navController.navigate("ble") }
            )
            NavigationBarItem(
                icon = { Image(painter = painterResource(id = R.drawable.parametresutilisateur), contentDescription = "Paramètres") },
                label = { Text("Paramètres") },
                selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
                onClick = { navController.navigate("settings") }
            )

        }
    }
}
