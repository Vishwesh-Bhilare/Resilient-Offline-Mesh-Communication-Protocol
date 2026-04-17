package com.mesh.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mesh.app.data.local.entity.InProgressEntity

@Dao
interface InProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InProgressEntity)

    @Query("SELECT * FROM in_progress WHERE messageId = :id")
    suspend fun getById(id: String): InProgressEntity?

    @Query("DELETE FROM in_progress WHERE messageId = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM in_progress WHERE lastChunkAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
