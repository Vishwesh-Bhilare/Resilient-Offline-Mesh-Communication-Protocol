package com.mesh.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mesh.app.data.local.entity.PeerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(peer: PeerEntity)

    @Query("SELECT * FROM peers WHERE peerId = :id")
    suspend fun getById(id: String): PeerEntity?

    @Query("SELECT * FROM peers")
    fun getAll(): Flow<List<PeerEntity>>
}
