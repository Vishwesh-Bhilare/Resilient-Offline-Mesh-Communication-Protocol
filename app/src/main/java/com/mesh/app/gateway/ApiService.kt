package com.mesh.app.gateway

import com.mesh.app.core.protocol.Message
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/messages")
    suspend fun publishMessage(@Body msg: MessageDto): Response<Unit>
}

@Serializable // FIX: 3 — required for Retrofit kotlinx-serialization converter when used as @Body.
data class MessageDto(
    val message_id: String,
    val sender: String,
    val public_key: String,
    val timestamp: Long,
    val hlc: String,
    val ttl: Int,
    val content: String,
    val hops: Int,
    val signature: String,
    val size: Int,
    val priority: Int,
    val channel_id: String?
) {
    companion object {
        fun from(message: Message) = MessageDto(
            message_id = message.id,
            sender = message.sender,
            public_key = message.public_key,
            timestamp = message.timestamp,
            hlc = message.hlc.toString(),
            ttl = message.ttl,
            content = message.content,
            hops = message.hops,
            signature = message.signature,
            size = message.size,
            priority = message.priority,
            channel_id = message.channel_id
        )
    }
}
