package com.mesh.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mesh.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity)

    @Query("SELECT * FROM messages ORDER BY hlcPhysicalMs ASC, hlcCounter ASC")
    fun getAll(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE published = 0 ORDER BY timestamp ASC")
    suspend fun getUnpublished(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY hlcPhysicalMs ASC, hlcCounter ASC")
    fun getByChannel(channelId: String): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE (receivedAt + ttl * 1000) < :now")
    suspend fun deleteExpired(now: Long)

    @Query("UPDATE messages SET published = 1 WHERE id = :id")
    suspend fun markPublished(id: String)

    @Query("SELECT id FROM messages")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM messages WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<MessageEntity>

    @Query("DELETE FROM messages WHERE id IN (SELECT id FROM messages WHERE published = 1 ORDER BY timestamp ASC LIMIT :count)")
    suspend fun deletePublishedOldest(count: Int)

    @Query("DELETE FROM messages WHERE id IN (SELECT id FROM messages ORDER BY timestamp ASC LIMIT :count)")
    suspend fun deleteOldest(count: Int)
}
