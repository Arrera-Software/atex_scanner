package com.arrera.atexscanner

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.arrera.atexscanner.ui.screens.camera.CameraScreen
import com.arrera.atexscanner.ui.screens.equipment.EquipmentListScreen
import com.arrera.atexscanner.ui.screens.ocr.OcrResultScreen
import com.arrera.atexscanner.ui.screens.zone.ZoneListScreen
import com.arrera.atexscanner.ui.theme.ATEXScannerTheme
import com.arrera.atexscanner.ui.viewmodel.MainViewModel
import com.arrera.atexscanner.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels {
        val app = application as AtexScannerApplication
        MainViewModelFactory(app.repository, app.ocrProcessor)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Les permissions ont été demandées
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestPermissions()

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
                                onBack = { navController.popBackStack() },
                                onZoneClick = { zone ->
                                    navController.navigate("equipments/${zone.id}/${zone.nom}")
                                }
                            )
                        }
                        composable(
                            route = "equipments/{zoneId}/{zoneNom}",
                            arguments = listOf(
                                navArgument("zoneId") { type = NavType.LongType },
                                navArgument("zoneNom") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val zoneId = backStackEntry.arguments?.getLong("zoneId") ?: 0L
                            val zoneNom = backStackEntry.arguments?.getString("zoneNom") ?: ""
                            EquipmentListScreen(
                                zoneId = zoneId,
                                zoneNom = zoneNom,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onLaunchCamera = { tag ->
                                    viewModel.setPendingTagAndZone(tag, zoneId)
                                    navController.navigate("camera")
                                },
                                onImageSelected = { tag, uri ->
                                    viewModel.setPendingTagAndZone(tag, zoneId)
                                    viewModel.processImage(uri) {
                                        navController.navigate("ocr_result")
                                    }
                                }
                            )
                        }
                        composable("camera") {
                            CameraScreen(
                                onImageCaptured = { uri ->
                                    viewModel.processImage(uri) {
                                        navController.navigate("ocr_result") {
                                            popUpTo("equipments/{zoneId}/{zoneNom}")
                                        }
                                    }
                                },
                                onError = { /* Gérer l'erreur */ }
                            )
                        }
                        composable("ocr_result") {
                            OcrResultScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onSave = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
