package com.mytech.mangatalkreader.di

import android.content.Context
import com.mytech.mangatalkreader.data.db.MangaDatabase
import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.dao.CollectionDao
import com.mytech.mangatalkreader.data.db.dao.MangaCollectionCrossRefDao
import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.dao.SourceDao
import com.mytech.mangatalkreader.data.db.dao.TextBlockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMangaDatabase(
        @ApplicationContext context: Context
    ): MangaDatabase {
        return MangaDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideMangaDao(database: MangaDatabase): MangaDao {
        return database.mangaDao()
    }

    @Provides
    @Singleton
    fun provideChapterDao(database: MangaDatabase): ChapterDao {
        return database.chapterDao()
    }

    @Provides
    @Singleton
    fun provideTextBlockDao(database: MangaDatabase): TextBlockDao {
        return database.textBlockDao()
    }

    @Provides
    @Singleton
    fun provideSourceDao(database: MangaDatabase): SourceDao {
        return database.sourceDao()
    }

    @Provides
    @Singleton
    fun provideCollectionDao(database: MangaDatabase): CollectionDao {
        return database.collectionDao()
    }

    @Provides
    @Singleton
    fun provideMangaCollectionCrossRefDao(database: MangaDatabase): MangaCollectionCrossRefDao {
        return database.mangaCollectionCrossRefDao()
    }
}
