package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mytech.mangatalkreader.data.db.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: ChapterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapters: List<ChapterEntity>): List<Long>

    @Update
    suspend fun update(chapter: ChapterEntity): Int

    @Delete
    suspend fun delete(chapter: ChapterEntity): Int

    @Query("DELETE FROM chapter WHERE id = :chapterId")
    suspend fun deleteById(chapterId: Long): Int

    @Query("DELETE FROM chapter WHERE manga_id = :mangaId")
    suspend fun deleteAllByMangaId(mangaId: Long): Int

    @Query("SELECT * FROM chapter WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: Long): ChapterEntity?

    @Query("SELECT * FROM chapter WHERE id = :chapterId")
    fun getChapterByIdAsFlow(chapterId: Long): Flow<ChapterEntity?>

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY chapter_number DESC")
    fun getChaptersByMangaId(mangaId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId AND chapter_number = :chapterNumber LIMIT 1")
    suspend fun getChapterByNumber(mangaId: Long, chapterNumber: Float): ChapterEntity?

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY chapter_number ASC")
    fun getChaptersByMangaIdAsc(mangaId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter WHERE is_downloaded = 1 ORDER BY fetched_date DESC")
    fun getDownloadedChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter WHERE is_read = 0 AND manga_id = :mangaId ORDER BY chapter_number ASC LIMIT 1")
    suspend fun getNextUnreadChapter(mangaId: Long): ChapterEntity?

    @Query("SELECT COUNT(*) FROM chapter WHERE manga_id = :mangaId")
    fun getChapterCountByMangaId(mangaId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapter WHERE manga_id = :mangaId AND is_downloaded = 1")
    fun getDownloadedChapterCountByMangaId(mangaId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapter WHERE manga_id = :mangaId AND is_read = 1")
    fun getReadChapterCountByMangaId(mangaId: Long): Flow<Int>

    @Query("UPDATE chapter SET is_read = :isRead WHERE id = :chapterId")
    suspend fun setReadStatus(chapterId: Long, isRead: Boolean): Int

    @Query("UPDATE chapter SET last_read_page = :page WHERE id = :chapterId")
    suspend fun updateReadProgress(chapterId: Long, page: Int): Int

    @Query("UPDATE chapter SET is_downloaded = :isDownloaded, download_path = :downloadPath WHERE id = :chapterId")
    suspend fun updateDownloadStatus(chapterId: Long, isDownloaded: Boolean, downloadPath: String?): Int

    @Query("SELECT MAX(chapter_number) FROM chapter WHERE manga_id = :mangaId")
    suspend fun getLatestChapterNumber(mangaId: Long): Float?

    @Query("SELECT * FROM chapter WHERE source_chapter_id = :sourceChapterId AND manga_id = :mangaId LIMIT 1")
    suspend fun getChapterBySourceId(sourceChapterId: String, mangaId: Long): ChapterEntity?

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId AND chapter_number >= :fromChapter AND chapter_number <= :toChapter ORDER BY chapter_number ASC")
    suspend fun getChaptersInRange(mangaId: Long, fromChapter: Float, toChapter: Float): List<ChapterEntity>
}
