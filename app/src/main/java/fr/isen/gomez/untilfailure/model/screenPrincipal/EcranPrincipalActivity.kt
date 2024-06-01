package fr.isen.gomez.untilfailure.model.screenPrincipal

import BLEScreen
import PerformanceScreen
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.PerformanceViewModel
import fr.isen.gomez.untilfailure.viewModel.ble.ScanViewModel
import SettingsScreen
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import fr.isen.gomez.untilfailure.model.firstPart.AccueilActivity
import fr.isen.gomez.untilfailure.viewModel.ble.PermissionsHelper
import fr.isen.gomez.untilfailure.viewModel.screenPrincipal.SettingsViewModel
class EcranPrincipalActivity : ComponentActivity() {
    private lateinit var permissionsHelper: PermissionsHelper
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var scanViewModel: ScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsHelper = PermissionsHelper(this)
        scanViewModel = ViewModelProvider(this)[ScanViewModel::class.java] // Initialisez le ScanViewModel ici

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                setContent { MainContent(scanViewModel) }
            } else {
                // Gérer le cas où les permissions ne sont pas accordées
                val intent = Intent(this@EcranPrincipalActivity, AccueilActivity::class.java)
                startActivity(intent)
                finish()  // Fermer l'activité actuelle pour éviter le retour en arrière
            }

        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (!permissionsHelper.hasAllPermissions()) {
            permissionsHelper.requestNeededPermissions(requestPermissionLauncher)
        } else {
            setContent { MainContent(scanViewModel) }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainContent(scanViewModel: ScanViewModel) {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            NavHost(navController, startDestination = "ble") {  // Modifié pour démarrer sur l'écran BLE
                composable("performance") {
                    PerformanceScreen(viewModel = ViewModelProvider(this@EcranPrincipalActivity)[PerformanceViewModel::class.java])
                }
                composable("seance") {
                    SeanceScreen(
                        seanceViewModel = viewModel(viewModelStoreOwner = this@EcranPrincipalActivity),
                        scanViewModel = scanViewModel
                    )
                }
                composable("settings") {
                    SettingsScreen(viewModel = ViewModelProvider(this@EcranPrincipalActivity)[SettingsViewModel::class.java])
                }
                composable("ble") {
                    BLEScreen(viewModel = scanViewModel)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Style pour la barre de navigation
    val navBarHeight = 48.dp  // Hauteur réduite
    val iconSize = 24.dp      // Taille des icônes
    val textSize = 10.sp      // Taille des textes

    NavigationBar(
        modifier = Modifier.height(navBarHeight)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Comparaison", modifier = Modifier.size(iconSize)) },
            label = { Text("Comparaison", fontSize = textSize) },
            selected = currentDestination?.hierarchy?.any { it.route == "performance" } == true,
            onClick = { navController.navigate("performance") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.profilutilisateur), contentDescription = "Séance", modifier = Modifier.size(iconSize)) },
            label = { Text("Séance", fontSize = textSize) },
            selected = currentDestination?.hierarchy?.any { it.route == "seance" } == true,
            onClick = { navController.navigate("seance") }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Filled.Bluetooth, contentDescription = "BLE", modifier = Modifier.size(iconSize)) },
            label = { Text("BLE", fontSize = textSize) },
            selected = currentDestination?.hierarchy?.any { it.route == "ble" } == true,
            onClick = { navController.navigate("ble") }

        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.parametresutilisateur), contentDescription = "Paramètres", modifier = Modifier.size(iconSize)) },
            label = { Text("Paramètres", fontSize = textSize) },
            selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
            onClick = { navController.navigate("settings") }
        )
    }
}
