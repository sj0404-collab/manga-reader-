package com.mytech.mangatalkreader.di

import android.content.Context
import androidx.room.Room
import com.mytech.mangatalkreader.data.db.MangaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMangaDatabase(
        @ApplicationContext context: Context
    ): MangaDatabase {
        return Room.databaseBuilder(
            context,
            MangaDatabase::class.java,
            "manga_talk_reader.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMangaDao(database: MangaDatabase) = database.mangaDao()

    @Singleton
    @Provides
    fun provideChapterDao(database: MangaDatabase) = database.chapterDao()

    @Singleton
    @Provides
    fun provideTextBlockDao(database: MangaDatabase) = database.textBlockDao()

    @Singleton
    @Provides
    fun provideCollectionDao(database: MangaDatabase) = database.collectionDao()

    @Singleton
    @Provides
    fun provideSourceDao(database: MangaDatabase) = database.sourceDao()
}
