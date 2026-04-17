package com.mesh.app.core.protocol

import android.util.Base64
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.security.SignatureUtil
import com.mesh.app.util.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

@Serializable
data class Message(
    val id: String,
    val sender: String,
    val public_key: String,
    val timestamp: Long,
    val hlc: HLC,
    val ttl: Int = Constants.DEFAULT_TTL,
    val content: String,
    val hops: Int = 0,
    val signature: String,
    val size: Int,
    val priority: Int = 0,
    val channel_id: String? = null
) {
    companion object {
        private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

        fun create(content: String, channelId: String?, keyManager: KeyManager, hlc: HLC): Message {
            val sender = keyManager.getDeviceId()
            val timestamp = System.currentTimeMillis() / 1000
            val id = sha256("$sender$timestamp$content")
            val keyPair = keyManager.getOrCreateKeyPair()
            val signature = SignatureUtil.sign(id + content, keyPair.private)
            val base = Message(
                id = id,
                sender = sender,
                public_key = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP),
                timestamp = timestamp,
                hlc = hlc,
                content = content,
                signature = signature,
                size = 0,
                channel_id = channelId
            )
            val size = json.encodeToString(base).toByteArray().size
            return base.copy(size = size)
        }

        fun sha256(raw: String): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
