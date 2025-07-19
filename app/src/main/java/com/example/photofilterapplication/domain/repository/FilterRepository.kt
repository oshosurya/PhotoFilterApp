package com.example.photofilterapplication.domain.repository

import android.graphics.Bitmap
import com.example.photofilterapplication.domain.model.FilterType

interface FilterRepository {
    fun applyFilter(bitmap: Bitmap, filter: FilterType): Bitmap
}