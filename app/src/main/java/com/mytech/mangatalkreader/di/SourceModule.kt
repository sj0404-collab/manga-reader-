package com.mytech.mangatalkreader.di

import com.mytech.mangatalkreader.data.source.MangaSource
import com.mytech.mangatalkreader.data.source.SourceRepository
import com.mytech.mangatalkreader.data.source.impl.MangaDexSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceModule {

    @Provides
    @Singleton
    fun provideMangaSources(
        mangaDexSource: MangaDexSource
    ): List<MangaSource> {
        return listOf(mangaDexSource)
    }

    @Provides
    @Singleton
    fun provideMangaDexSource(): MangaDexSource {
        return MangaDexSource()
    }

    @Provides
    @Singleton
    fun provideSourceRepository(
        sources: List<MangaSource>
    ): SourceRepository {
        return SourceRepository(sources)
    }
}
