package com.mesh.app.data.repository

import com.mesh.app.core.transport.MessageChunk
import com.mesh.app.data.local.db.InProgressDao
import com.mesh.app.data.local.entity.InProgressEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InProgressRepository @Inject constructor(
    private val dao: InProgressDao
) {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    private val chunkStore = ConcurrentHashMap<String, ConcurrentHashMap<Int, MessageChunk>>()

    suspend fun addChunk(messageId: String, chunk: MessageChunk, totalChunks: Int) {
        val now = System.currentTimeMillis()
        val existing = dao.getById(messageId)
        val received = existing?.let { json.decodeFromString<List<Int>>(it.receivedChunksJson).toMutableSet() } ?: mutableSetOf()
        received += chunk.chunkIndex

        val byMessage = chunkStore.getOrPut(messageId) { ConcurrentHashMap() }
        byMessage[chunk.chunkIndex] = chunk

        dao.upsert(
            InProgressEntity(
                messageId = messageId,
                receivedChunksJson = json.encodeToString(received.sorted()),
                totalChunks = totalChunks,
                firstChunkAt = existing?.firstChunkAt ?: now,
                lastChunkAt = now
            )
        )
    }

    suspend fun tryAssemble(messageId: String): List<MessageChunk>? {
        val existing = dao.getById(messageId) ?: return null
        val received = json.decodeFromString<List<Int>>(existing.receivedChunksJson)
        if (received.size != existing.totalChunks) return null
        val chunks = chunkStore[messageId]
        if (chunks == null || chunks.size != existing.totalChunks) {
            return null // FIX: 3 — do not delete persisted in-progress record when in-memory chunks are absent
        }
        return (0 until existing.totalChunks).map { idx -> chunks[idx] ?: return null }
    }

    suspend fun cleanup(cutoffMs: Long) {
        dao.deleteOlderThan(cutoffMs)
        chunkStore.keys.toList().forEach { id ->
            if (dao.getById(id) == null) chunkStore.remove(id)
        }
    }

    suspend fun clear(messageId: String) {
        dao.deleteById(messageId)
        chunkStore.remove(messageId)
    }
}
