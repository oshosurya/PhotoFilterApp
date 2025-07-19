package com.example.photofilterapplication.presentation.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photofilterapplication.domain.model.FilterType
import com.example.photofilterapplication.presentation.viewmodel.MainViewModel
import com.example.photofilterapplication.utils.saveBitmapToCache

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onPickFromGallery: () -> Unit,
    onCapturePhoto: (Uri, FilterType) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lensFacing by viewModel.lensFacing.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val cameraProvider by viewModel.cameraProviderFlow.collectAsState()
    val filteredBitmap by viewModel.filteredBitmap.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Photo Filter") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            cameraProvider?.let {
                CameraPreview(
                    cameraProvider = it,
                    lensFacing = lensFacing,
                    selectedFilter = selectedFilter,
                    imageAnalyzer = viewModel.getImageAnalyzer(),
                    modifier = Modifier.fillMaxSize()
                )
            }

            filteredBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilterSelector(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { viewModel.selectFilter(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = viewModel::toggleCameraLens) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera")
                    }

                    IconButton(onClick = {
                        viewModel.captureOriginalFrame { bitmap ->
                            bitmap?.let {
                                val uri = saveBitmapToCache(context, it)
                                onCapturePhoto(uri, selectedFilter) // Filter info still passed, but image is raw
                            }
                        }
                    }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo")
                    }

                    IconButton(onClick = onPickFromGallery) {
                        Icon(Icons.Default.Collections, contentDescription = "Gallery")
                    }
                }
            }
        }
    }
}







