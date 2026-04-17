package com.mesh.app.core.sync

import com.mesh.app.core.protocol.Message
import com.mesh.app.core.transport.ChunkManager
import com.mesh.app.core.transport.MessageChunk

class TransferPhase(
    private val chunkManager: ChunkManager = ChunkManager()
) {
    fun toChunks(messages: List<Message>): List<MessageChunk> = messages.flatMap { chunkManager.chunk(it) }
    fun fromChunks(chunks: List<MessageChunk>): Message? = chunkManager.reassemble(chunks)
}
