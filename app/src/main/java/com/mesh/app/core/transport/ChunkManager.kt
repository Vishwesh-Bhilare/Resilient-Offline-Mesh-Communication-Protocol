package com.mesh.app.core.transport

import com.mesh.app.core.protocol.Message
import com.mesh.app.util.Constants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class MessageChunk(
    val messageId: String,
    val chunkIndex: Int,
    val totalChunks: Int,
    val data: ByteArray
) {
    fun toByteArray(): ByteArray {
        val idBytes = messageId.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val out = ByteBuffer.allocate(36 + data.size).order(ByteOrder.BIG_ENDIAN)
        out.put(idBytes)
        out.putShort(chunkIndex.toShort())
        out.putShort(totalChunks.toShort())
        out.put(data)
        return out.array()
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): MessageChunk {
            val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
            val id = ByteArray(32)
            bb.get(id)
            val idx = bb.short.toInt() and 0xffff
            val total = bb.short.toInt() and 0xffff
            val data = ByteArray(bb.remaining())
            bb.get(data)
            return MessageChunk(id.joinToString("") { "%02x".format(it) }, idx, total, data)
        }
    }
}

class ChunkManager {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    fun chunk(message: Message): List<MessageChunk> {
        val payload = json.encodeToString(message).toByteArray()
        val total = ((payload.size + Constants.CHUNK_DATA_SIZE - 1) / Constants.CHUNK_DATA_SIZE)
        return (0 until total).map { idx ->
            val from = idx * Constants.CHUNK_DATA_SIZE
            val to = minOf(payload.size, from + Constants.CHUNK_DATA_SIZE)
            MessageChunk(message.id, idx, total, payload.copyOfRange(from, to))
        }
    }

    fun reassemble(chunks: List<MessageChunk>): Message? {
        if (chunks.isEmpty()) return null
        val sorted = chunks.sortedBy { it.chunkIndex }
        val total = sorted.first().totalChunks
        if (sorted.size != total) return null
        if (sorted.indices.any { sorted[it].chunkIndex != it }) return null
        val bytes = sorted.flatMap { it.data.toList() }.toByteArray()
        return json.decodeFromString(bytes.decodeToString())
    }
}
