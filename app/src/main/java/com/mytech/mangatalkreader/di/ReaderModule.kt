package com.mytech.mangatalkreader.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReaderModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providePagePreloadCount(): Int = 5

    @Provides
    @Singleton
    fun provideMaxImageCacheSize(): Long = 50 * 1024 * 1024L // 50 MB

    @Provides
    @Singleton
    fun provideImageCacheMemoryPercent(): Int = 25 // 25% of available memory
}
