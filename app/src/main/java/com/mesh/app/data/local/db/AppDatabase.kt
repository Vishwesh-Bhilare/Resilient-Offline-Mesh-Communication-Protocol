package com.mesh.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mesh.app.data.local.entity.InProgressEntity
import com.mesh.app.data.local.entity.MessageEntity
import com.mesh.app.data.local.entity.PeerEntity

@Database(
    entities = [MessageEntity::class, InProgressEntity::class, PeerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun inProgressDao(): InProgressDao
    abstract fun peerDao(): PeerDao
}
