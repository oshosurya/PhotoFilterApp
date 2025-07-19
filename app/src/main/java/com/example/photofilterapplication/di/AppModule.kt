package com.example.photofilterapplication.di

import android.content.Context
    import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.photofilterapplication.data.repository.FilterRepositoryImpl
import com.example.photofilterapplication.domain.repository.FilterRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFilterRepository(
        @ApplicationContext context: Context
    ): FilterRepository = FilterRepositoryImpl(context)
}
