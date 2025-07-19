package com.example.photofilterapplication.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.photofilterapplication.domain.model.FilterType
import com.example.photofilterapplication.presentation.ui.screen.EditorScreen
import com.example.photofilterapplication.presentation.ui.screen.MainScreen

@Composable
fun PhotoFilterNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {

        composable("main") {

            // Gallery launcher
            val galleryLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    val encodedUri = Uri.encode(it.toString())
                    // Navigate without any preselected filter for gallery
                    navController.navigate("editor?imageUri=$encodedUri&filter=NONE")
                }
            }

            MainScreen(
                onPickFromGallery = {
                    galleryLauncher.launch("image/*")
                },
                onCapturePhoto = { imageUri, filterType ->
                    val encodedUri = Uri.encode(imageUri.toString())
                    navController.navigate("editor?imageUri=$encodedUri&filter=${filterType.name}")
                }
            )
        }

        composable(
            route = "editor?imageUri={imageUri}&filter={filter}",
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("filter") {
                    type = NavType.StringType
                    defaultValue = "NONE" // Optional, helps avoid null checks
                }
            )
        ) { backStackEntry ->

            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val imageUri = remember(imageUriString) {
                imageUriString?.let { Uri.parse(it) }
            }

            val filterName = backStackEntry.arguments?.getString("filter") ?: "NONE"
            val selectedFilter = FilterType.valueOf(filterName)

            EditorScreen(
                imageUri = imageUri,
                initialFilter = selectedFilter,
                onBack = { navController.popBackStack() },
            )
        }
    }
}




