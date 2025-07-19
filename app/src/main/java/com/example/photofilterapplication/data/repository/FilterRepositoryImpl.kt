package com.example.photofilterapplication.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.photofilterapplication.domain.model.FilterType
import com.example.photofilterapplication.domain.repository.FilterRepository
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*

class FilterRepositoryImpl(
    private val context: Context
) : FilterRepository {

    override fun applyFilter(bitmap: Bitmap, filter: FilterType): Bitmap {
        val gpuImage = GPUImage(context)
        gpuImage.setImage(bitmap)

        val gpuFilter = when (filter) {
            FilterType.NONE -> GPUImageFilter()
            FilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
            FilterType.CYBERPUNK -> {
                GPUImageFilterGroup(
                    listOf(
                        GPUImageContrastFilter(1.0f),        // Less harsh contrast
                        GPUImageSaturationFilter(1.8f),      // Slight color pop
                        GPUImageHueFilter(80.0f),            // Cool hue shift (magenta/cyan tones)
                        GPUImageBrightnessFilter(0.1f)       // Slight brightness boost
                    )
                )
            }
        }

        gpuImage.setFilter(gpuFilter)
        return gpuImage.bitmapWithFilterApplied
    }
}