package com.example.photofilterapplication.presentation.viewmodel

import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.ViewModel
import com.example.photofilterapplication.domain.model.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.viewModelScope
import com.example.photofilterapplication.domain.repository.FilterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import com.example.photofilterapplication.utils.CameraUtils.getCameraProvider
import com.example.photofilterapplication.utils.toBitmapCompat

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val filterRepository: FilterRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FilterType.NONE)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter

    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
    val lensFacing: StateFlow<Int> = _lensFacing

    private val _cameraProviderFlow = MutableStateFlow<ProcessCameraProvider?>(null)
    val cameraProviderFlow: StateFlow<ProcessCameraProvider?> = _cameraProviderFlow

    private val _filteredBitmap = MutableStateFlow<Bitmap?>(null)
    val filteredBitmap: StateFlow<Bitmap?> = _filteredBitmap

    private val _lastOriginalFrame = MutableStateFlow<Bitmap?>(null)

        init {
            viewModelScope.launch {
                try {
                    _cameraProviderFlow.value = getCameraProvider(context)
                    Log.d("MainViewModel", "Camera provider initialized")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Camera provider failed", e)
                }
            }
        }

    fun toggleCameraLens() {
        _lensFacing.value =
            if (_lensFacing.value == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK
    }

    fun selectFilter(filter: FilterType) {
        _selectedFilter.value = filter
    }

    fun getImageAnalyzer(): ImageAnalysis.Analyzer {
        return ImageAnalysis.Analyzer { imageProxy ->
            try {
                val bitmap = imageProxy.toBitmapCompat()
                _lastOriginalFrame.value = bitmap // ✅ Save raw frame

                val filtered = filterRepository.applyFilter(bitmap, _selectedFilter.value)
                _filteredBitmap.value = filtered
            } catch (e: Exception) {
                Log.e("MainViewModel", "Filter error", e)
            } finally {
                imageProxy.close()
            }
        }
    }

    fun captureOriginalFrame(onCaptured: (Bitmap?) -> Unit) {
        val lastOriginalFrame = _lastOriginalFrame.value
        onCaptured(lastOriginalFrame)
    }
}



