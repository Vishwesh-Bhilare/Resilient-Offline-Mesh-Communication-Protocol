package com.mesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val sender: String,
    val publicKey: String,
    val timestamp: Long,
    val hlcPhysicalMs: Long,
    val hlcCounter: Int,
    val hlcDeviceId: String,
    val ttl: Int,
    val content: String,
    val hops: Int,
    val signatureB64: String,
    val size: Int,
    val priority: Int,
    val channelId: String?,
    val published: Boolean = false,
    val receivedAt: Long
)
