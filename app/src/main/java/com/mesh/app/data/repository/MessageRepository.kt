package com.mesh.app.data.repository

import android.content.Context
import com.mesh.app.core.protocol.HLC
import com.mesh.app.core.protocol.HlcClock
import com.mesh.app.core.protocol.Message
import com.mesh.app.core.security.RateLimiter
import com.mesh.app.core.security.SignatureUtil
import com.mesh.app.data.local.db.MessageDao
import com.mesh.app.data.local.entity.MessageEntity
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64

@Singleton
class MessageRepository @Inject constructor(
    private val dao: MessageDao,
    private val rateLimiter: RateLimiter,
    private val hlcClock: HlcClock,
    @ApplicationContext private val context: Context
) {
    fun observeAll(): Flow<List<Message>> = dao.getAll().map { it.map(::toDomain) }

    fun observeChannel(channelId: String): Flow<List<Message>> = dao.getByChannel(channelId).map { it.map(::toDomain) }

    suspend fun save(message: Message) {
        dao.insert(toEntity(message))
    }

    suspend fun ingestFromPeer(message: Message): Boolean {
        if (dao.countById(message.id) > 0) return false

        if (!rateLimiter.tryAcquire(message.sender)) return false

        if (message.hops >= Constants.MAX_HOPS) {
            Logger.w("Dropping message ${message.id}: max hops reached (${message.hops})")
            return false
        }

        val nowSec = System.currentTimeMillis() / 1000
        if (message.timestamp + message.ttl < nowSec) {
            Logger.w("Dropping expired message ${message.id}")
            return false
        }

        val expectedId = Message.sha256("${message.sender}${message.timestamp}${message.content}")
        if (message.id != expectedId) {
            Logger.w("Dropping message with invalid id ${message.id}")
            return false
        }

        val verified = verify(message)
        if (!verified) {
            Logger.w("Dropping invalid signature message ${message.id}")
            return false
        }
        hlcClock.update(message.hlc)
        dao.insert(toEntity(message.copy(hops = message.hops + 1)))
        return true
    }

    suspend fun ids(): List<String> = dao.getAllIds()

    suspend fun getMessages(ids: List<String>): List<Message> = dao.getByIds(ids).map(::toDomain)

    suspend fun unpublished(): List<Message> = dao.getUnpublished().map(::toDomain)

    suspend fun markPublished(id: String) = dao.markPublished(id)

    suspend fun cleanupAndEvict() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.deleteExpired(now)
        val dbFile = context.getDatabasePath("mesh.db")
        if (dbFile.exists() && dbFile.length() > Constants.MAX_DB_SIZE_BYTES) {
            dao.deletePublishedOldest(100)
            dao.deleteExpired(now)
            if (dbFile.length() > Constants.MAX_DB_SIZE_BYTES) {
                dao.deleteOldest(200)
            }
        }
    }

    private fun verify(message: Message): Boolean {
        return try {
            val publicBytes = Base64.decode(message.public_key, Base64.NO_WRAP)
            val keyFactory = KeyFactory.getInstance("Ed25519", "BC")
            val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicBytes))
            SignatureUtil.verify(message.id + message.content, message.signature, publicKey)
        } catch (_: Throwable) {
            false
        }
    }

    private fun toEntity(message: Message) = MessageEntity(
        id = message.id,
        sender = message.sender,
        publicKey = message.public_key,
        timestamp = message.timestamp,
        hlcPhysicalMs = message.hlc.physicalMs,
        hlcCounter = message.hlc.counter,
        hlcDeviceId = message.hlc.deviceId,
        ttl = message.ttl,
        content = message.content,
        hops = message.hops,
        signatureB64 = message.signature,
        size = message.size,
        priority = message.priority,
        channelId = message.channel_id,
        receivedAt = System.currentTimeMillis()
    )

    private fun toDomain(entity: MessageEntity) = Message(
        id = entity.id,
        sender = entity.sender,
        public_key = entity.publicKey,
        timestamp = entity.timestamp,
        hlc = HLC(entity.hlcPhysicalMs, entity.hlcCounter, entity.hlcDeviceId),
        ttl = entity.ttl,
        content = entity.content,
        hops = entity.hops,
        signature = entity.signatureB64,
        size = entity.size,
        priority = entity.priority,
        channel_id = entity.channelId
    )
}
