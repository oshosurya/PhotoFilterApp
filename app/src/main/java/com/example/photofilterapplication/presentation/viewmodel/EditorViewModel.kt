package com.example.photofilterapplication.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photofilterapplication.domain.model.FilterType
import com.example.photofilterapplication.domain.repository.FilterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.photofilterapplication.utils.ImageUtils

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val filterRepository: FilterRepository,
    private val application: Application
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FilterType.NONE)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter

    private val _filteredBitmap = MutableStateFlow<Bitmap?>(null)
    val filteredBitmap: StateFlow<Bitmap?> = _filteredBitmap

    private var originalBitmap: Bitmap? = null

    /** ✅ Load from URI (used for both gallery and camera) */
    fun loadImage(uri: Uri, context: Context = application) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val orientation = ImageUtils.getExifRotation(context, uri)
                val rotatedBitmap = if (orientation != 0) {
                    ImageUtils.rotateBitmap(bitmap, orientation)
                } else {
                    bitmap
                }

                originalBitmap = rotatedBitmap
                applyFilter(_selectedFilter.value) // ✅ Apply whatever the current filter is
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** ✅ Called when user selects a new filter */
    fun selectFilter(filter: FilterType) {
        _selectedFilter.value = filter
        applyFilter(filter)
    }

    /** ✅ Applies current filter on original image */
    private fun applyFilter(filter: FilterType) {
        originalBitmap?.let { bitmap ->
            viewModelScope.launch(Dispatchers.Default) {
                val filtered = filterRepository.applyFilter(bitmap, filter)
                _filteredBitmap.value = filtered
            }
        }
    }

    /** ✅ Expose current filtered image for saving */
    fun getCurrentFilteredBitmap(): Bitmap? = _filteredBitmap.value
}




