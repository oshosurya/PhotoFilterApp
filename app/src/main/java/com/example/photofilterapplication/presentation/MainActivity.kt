package com.example.photofilterapplication.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.example.photofilterapplication.presentation.ui.theme.PhotoFilterApplicationTheme
import com.example.photofilterapplication.utils.PhotoFilterNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission on app start
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            PhotoFilterApplicationTheme {
                val navController = rememberNavController()
                PhotoFilterNavHost(navController = navController)
            }
        }
    }
}




