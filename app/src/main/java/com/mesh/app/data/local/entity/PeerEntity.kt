package com.mesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peers")
data class PeerEntity(
    @PrimaryKey val peerId: String,
    val lastSyncTime: Long,
    val syncHash: String,
    val address: String
)
