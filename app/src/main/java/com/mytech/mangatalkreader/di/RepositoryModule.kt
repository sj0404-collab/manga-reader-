package com.mytech.mangatalkreader.di

import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.dao.CollectionDao
import com.mytech.mangatalkreader.data.db.dao.MangaCollectionCrossRefDao
import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.dao.SourceDao
import com.mytech.mangatalkreader.data.db.dao.TextBlockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMangaRepository(
        mangaDao: MangaDao,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): com.mytech.mangatalkreader.domain.repository.MangaRepository {
        return com.mytech.mangatalkreader.data.repository.MangaRepositoryImpl(
            mangaDao = mangaDao,
            applicationScope = applicationScope,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideChapterRepository(
        chapterDao: ChapterDao,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): com.mytech.mangatalkreader.domain.repository.ChapterRepository {
        return com.mytech.mangatalkreader.data.repository.ChapterRepositoryImpl(
            chapterDao = chapterDao,
            applicationScope = applicationScope,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideSourceRepository(
        sourceDao: SourceDao,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): com.mytech.mangatalkreader.domain.repository.SourceRepository {
        return com.mytech.mangatalkreader.data.repository.SourceRepositoryImpl(
            sourceDao = sourceDao,
            applicationScope = applicationScope,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionDao: CollectionDao,
        crossRefDao: MangaCollectionCrossRefDao,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): com.mytech.mangatalkreader.domain.repository.CollectionRepository {
        return com.mytech.mangatalkreader.data.repository.CollectionRepositoryImpl(
            collectionDao = collectionDao,
            crossRefDao = crossRefDao,
            applicationScope = applicationScope,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideTextBlockRepository(
        textBlockDao: TextBlockDao,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): com.mytech.mangatalkreader.domain.repository.TextBlockRepository {
        return com.mytech.mangatalkreader.data.repository.TextBlockRepositoryImpl(
            textBlockDao = textBlockDao,
            applicationScope = applicationScope,
            ioDispatcher = ioDispatcher
        )
    }
}
