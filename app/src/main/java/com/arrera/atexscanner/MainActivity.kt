package com.arrera.atexscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arrera.atexscanner.ui.HomeScreen
import com.arrera.atexscanner.ui.screens.zone.ZoneListScreen
import com.arrera.atexscanner.ui.theme.ATEXScannerTheme
import com.arrera.atexscanner.ui.viewmodel.MainViewModel
import com.arrera.atexscanner.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as AtexScannerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ATEXScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onSiteClick = { site ->
                                    navController.navigate("zones/${site.id}/${site.nom}")
                                },
                                onSiteAdded = { id, name ->
                                    navController.navigate("zones/$id/$name")
                                }
                            )
                        }
                        composable(
                            route = "zones/{siteId}/{siteNom}",
                            arguments = listOf(
                                navArgument("siteId") { type = NavType.LongType },
                                navArgument("siteNom") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val siteId = backStackEntry.arguments?.getLong("siteId") ?: 0L
                            val siteNom = backStackEntry.arguments?.getString("siteNom") ?: ""
                            ZoneListScreen(
                                siteId = siteId,
                                siteNom = siteNom,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
