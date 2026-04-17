package com.mesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "in_progress")
data class InProgressEntity(
    @PrimaryKey val messageId: String,
    val receivedChunksJson: String,
    val totalChunks: Int,
    val firstChunkAt: Long,
    val lastChunkAt: Long
)
