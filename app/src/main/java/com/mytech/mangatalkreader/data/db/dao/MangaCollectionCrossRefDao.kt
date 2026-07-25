package com.mytech.mangatalkreader.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mytech.mangatalkreader.data.db.entity.MangaCollectionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaCollectionCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: MangaCollectionCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(crossRefs: List<MangaCollectionCrossRef>): List<Long>

    @Delete
    suspend fun delete(crossRef: MangaCollectionCrossRef): Int

    @Query("DELETE FROM manga_collection_cross_ref WHERE manga_id = :mangaId AND collection_id = :collectionId")
    suspend fun deleteByMangaAndCollection(mangaId: Long, collectionId: Long): Int

    @Query("DELETE FROM manga_collection_cross_ref WHERE manga_id = :mangaId")
    suspend fun deleteAllByMangaId(mangaId: Long): Int

    @Query("DELETE FROM manga_collection_cross_ref WHERE collection_id = :collectionId")
    suspend fun deleteAllByCollectionId(collectionId: Long): Int

    @Query("SELECT * FROM manga_collection_cross_ref WHERE manga_id = :mangaId AND collection_id = :collectionId LIMIT 1")
    suspend fun getCrossRef(mangaId: Long, collectionId: Long): MangaCollectionCrossRef?

    @Query(
        """
        SELECT * FROM manga_collection_cross_ref
        WHERE manga_id = :mangaId
        ORDER BY added_date ASC
        """
    )
    fun getCrossRefsByMangaId(mangaId: Long): Flow<List<MangaCollectionCrossRef>>

    @Query(
        """
        SELECT * FROM manga_collection_cross_ref
        WHERE collection_id = :collectionId
        ORDER BY added_date ASC
        """
    )
    fun getCrossRefsByCollectionId(collectionId: Long): Flow<List<MangaCollectionCrossRef>>

    @Query(
        """
        SELECT COUNT(*) FROM manga_collection_cross_ref
        WHERE manga_id = :mangaId
        """
    )
    fun getCollectionCountForManga(mangaId: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM manga_collection_cross_ref
        WHERE collection_id = :collectionId
        """
    )
    fun getMangaCountForCollection(collectionId: Long): Flow<Int>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM manga_collection_cross_ref
            WHERE manga_id = :mangaId AND collection_id = :collectionId
        )
        """
    )
    fun isMangaInCollection(mangaId: Long, collectionId: Long): Flow<Boolean>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM manga_collection_cross_ref
            WHERE manga_id = :mangaId AND collection_id = :collectionId
        )
        """
    )
    suspend fun isMangaInCollectionSync(mangaId: Long, collectionId: Long): Boolean

    @Query(
        """
        SELECT DISTINCT mcr.manga_id FROM manga_collection_cross_ref mcr
        INNER JOIN collection c ON mcr.collection_id = c.id
        WHERE c.is_system = 0
        """
    )
    fun getAllMangaIdsInUserCollections(): Flow<List<Long>>

    @Query(
        """
        SELECT DISTINCT mcr.collection_id FROM manga_collection_cross_ref mcr
        WHERE mcr.manga_id = :mangaId
        """
    )
    suspend fun getCollectionIdsForManga(mangaId: Long): List<Long>
}
