package com.mesh.app.core.sync

import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val messageRepository: MessageRepository
) {
    private val diffPhase = DiffPhase()
    private val requestPhase = RequestPhase()
    private val transferPhase = TransferPhase()

    suspend fun buildRequestedIds(peerBloom: BloomFilter): List<String> {
        val local = messageRepository.ids()
        return diffPhase.calculateNeededIds(local, peerBloom)
    }

    fun encodeRequest(ids: List<String>): ByteArray = requestPhase.encode(ids)
    fun decodeRequest(data: ByteArray): List<String> = requestPhase.decode(data)

    suspend fun messagesForRequest(ids: List<String>) = messageRepository.getMessages(ids)

    suspend fun saveTransferred(message: com.mesh.app.core.protocol.Message): Boolean {
        return messageRepository.ingestFromPeer(message)
    }

    fun closeSession() {
        Logger.d("Sync session closed")
    }
}
