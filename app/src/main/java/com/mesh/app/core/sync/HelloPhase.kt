package com.mesh.app.core.sync

import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.util.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class HelloPayload(
    val deviceId: String,
    val protocolVersion: Int,
    val capabilities: List<String>,
    val bloomFilter: String
)

class HelloPhase {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    // Full bloom filter (120 bytes → ~160 Base64 chars) is included.
    // At MTU=512, usable ATT payload = 509 bytes. Total JSON ≈ 290 bytes. Fits in one write.
    fun encode(deviceId: String, bloomFilter: BloomFilter): ByteArray = json.encodeToString(
        HelloPayload(
            deviceId = deviceId,
            protocolVersion = Constants.PROTOCOL_VERSION,
            capabilities = listOf("relay", "gateway"),
            bloomFilter = android.util.Base64.encodeToString(bloomFilter.toByteArray(), android.util.Base64.NO_WRAP)
        )
    ).toByteArray()

    fun decode(data: ByteArray): HelloPayload = json.decodeFromString(data.decodeToString())
}
