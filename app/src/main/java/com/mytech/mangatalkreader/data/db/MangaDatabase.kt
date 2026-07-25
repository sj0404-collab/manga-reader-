package com.mytech.mangatalkreader.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.mytech.mangatalkreader.data.db.dao.ChapterDao
import com.mytech.mangatalkreader.data.db.dao.CollectionDao
import com.mytech.mangatalkreader.data.db.dao.MangaCollectionCrossRefDao
import com.mytech.mangatalkreader.data.db.dao.MangaDao
import com.mytech.mangatalkreader.data.db.dao.SourceDao
import com.mytech.mangatalkreader.data.db.dao.TextBlockDao
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import com.mytech.mangatalkreader.data.db.entity.CollectionEntity
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import com.mytech.mangatalkreader.data.db.entity.MangaEntity
import com.mytech.mangatalkreader.data.db.entity.SourceEntity
import com.mytech.mangatalkreader.data.db.entity.TextBlockEntity

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        TextBlockEntity::class,
        SourceEntity::class,
        CollectionEntity::class,
        MangaCollectionCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MangaDatabase : RoomDatabase() {

    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao

    abstract fun textBlockDao(): TextBlockDao

    abstract fun sourceDao(): SourceDao

    abstract fun collectionDao(): CollectionDao

    abstract fun mangaCollectionCrossRefDao(): MangaCollectionCrossRefDao

    companion object {
        private const val DATABASE_NAME = "manga_reader_database"

        @Volatile
        private var INSTANCE: MangaDatabase? = null

        fun getInstance(context: Context): MangaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MangaDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromFloatList(value: String): List<Float> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").mapNotNull { it.trim().toFloatOrNull() }
        }
    }

    @TypeConverter
    fun toFloatList(list: List<Float>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromIntList(value: String): List<Int> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").mapNotNull { it.trim().toIntOrNull() }
        }
    }

    @TypeConverter
    fun toIntList(list: List<Int>): String {
        return list.joinToString(",")
    }
}
