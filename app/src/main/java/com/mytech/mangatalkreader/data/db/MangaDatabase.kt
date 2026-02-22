package com.mytech.mangatalkreader.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.dao.TextBlockDao
import com.mytech.mangatalkreader.data.db.dao.CollectionDao
import com.mytech.mangatalkreader.data.db.dao.SourceDao
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.SourceEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import com.mytech.mangatalkreader.data.db.converter.DateConverter

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        TextBlockEntity::class,
        CollectionEntity::class,
        SourceEntity::class,
        MangaCollectionCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun textBlockDao(): TextBlockDao
    abstract fun collectionDao(): CollectionDao
    abstract fun sourceDao(): SourceDao
}
