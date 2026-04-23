package com.mesh.app.data.repository

import android.util.Base64
import com.mesh.app.core.protocol.HLC
import com.mesh.app.core.protocol.Message
import com.mesh.app.core.security.SignatureUtil
import com.mesh.app.data.local.db.MessageDao
import com.mesh.app.data.local.entity.MessageEntity
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun observeAll(): Flow<List<Message>> = messageDao.getAll().map { list ->
        list.map { it.toDomain() }
    }

    fun observeChatMessages(): Flow<List<Message>> = observeAll().map { list ->
        list.filterNot { it.channel_id == Constants.CHANNEL_PRESENCE || it.content.startsWith(Constants.PING_PREFIX) }
    }

    fun observePresenceLogs(): Flow<List<Message>> = observeAll().map { list ->
        list.filter { it.channel_id == Constants.CHANNEL_PRESENCE || it.content.startsWith(Constants.PING_PREFIX) }
    }

    fun observeChannel(channelId: String): Flow<List<Message>> = messageDao.getByChannel(channelId).map { list ->
        list.map { it.toDomain() }
    }

    suspend fun save(message: Message) {
        messageDao.insert(message.toEntity(receivedAt = System.currentTimeMillis()))
    }

    suspend fun ingestFromPeer(message: Message): Boolean {
        if (message.hops > Constants.MAX_HOPS) return false
        if (!verifySignature(message)) return false
        if (messageDao.countById(message.id) > 0) return false

        messageDao.insert(
            message.copy(hops = message.hops + 1)
                .toEntity(receivedAt = System.currentTimeMillis())
        )
        return true
    }

    suspend fun unpublished(): List<Message> = messageDao.getUnpublished().map { it.toDomain() }

    suspend fun markPublished(id: String) = messageDao.markPublished(id)

    suspend fun ids(): List<String> = messageDao.getAllIds()

    suspend fun getMessages(ids: List<String>): List<Message> {
        if (ids.isEmpty()) return emptyList()
        return messageDao.getByIds(ids).map { it.toDomain() }
    }

    suspend fun cleanupAndEvict() {
        messageDao.deleteExpired(System.currentTimeMillis())
    }

    private fun verifySignature(message: Message): Boolean {
        return runCatching {
            val publicKeyBytes = Base64.decode(message.public_key, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val publicKey = KeyFactory.getInstance("1.3.101.112", "BC").generatePublic(keySpec)
            SignatureUtil.verify(message.id + message.content, message.signature, publicKey)
        }.onFailure {
            Logger.w("Failed to verify signature for message ${message.id}", it)
        }.getOrElse { false }
    }
}

private fun MessageEntity.toDomain(): Message = Message(
    id = id,
    sender = sender,
    public_key = publicKey,
    timestamp = timestamp,
    hlc = HLC(hlcPhysicalMs, hlcCounter, hlcDeviceId),
    ttl = ttl,
    content = content,
    hops = hops,
    signature = signatureB64,
    size = size,
    priority = priority,
    channel_id = channelId
)

private fun Message.toEntity(receivedAt: Long): MessageEntity = MessageEntity(
    id = id,
    sender = sender,
    publicKey = public_key,
    timestamp = timestamp,
    hlcPhysicalMs = hlc.physicalMs,
    hlcCounter = hlc.counter,
    hlcDeviceId = hlc.deviceId,
    ttl = ttl,
    content = content,
    hops = hops,
    signatureB64 = signature,
    size = size,
    priority = priority,
    channelId = channel_id,
    published = false,
    receivedAt = receivedAt
)
