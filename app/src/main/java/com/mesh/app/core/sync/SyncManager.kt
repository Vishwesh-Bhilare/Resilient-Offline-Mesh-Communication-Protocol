package com.mesh.app.core.sync

import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.core.transport.MessageChunk
import com.mesh.app.data.repository.InProgressRepository
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.data.repository.PeerRepository
import com.mesh.app.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val messageRepository: MessageRepository,
    private val inProgressRepository: InProgressRepository
) {
    private val diffPhase = DiffPhase()
    private val requestPhase = RequestPhase()
    private val transferPhase = TransferPhase()


    suspend fun shouldSync(peerId: String, peerRepo: PeerRepository): Boolean {
        val peer = peerRepo.getPeer(peerId) ?: return true
        val elapsed = System.currentTimeMillis() - peer.lastSyncTime
        return elapsed > 60_000L
    }

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


    suspend fun ingestTransferredChunk(chunk: MessageChunk): Boolean {
        inProgressRepository.addChunk(chunk.messageId, chunk, chunk.totalChunks)
        val assembled = inProgressRepository.tryAssemble(chunk.messageId) ?: return false
        val message = transferPhase.fromChunks(assembled) ?: return false
        val saved = saveTransferred(message)
        if (saved) {
            inProgressRepository.clear(chunk.messageId)
        }
        return saved
    }

    fun closeSession() {
        Logger.d("Sync session closed")
    }
}
