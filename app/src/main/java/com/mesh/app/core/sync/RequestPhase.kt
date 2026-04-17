package com.mesh.app.core.sync

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RequestPhase {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    fun encode(ids: List<String>): ByteArray = json.encodeToString(ids).toByteArray()
    fun decode(data: ByteArray): List<String> = json.decodeFromString(data.decodeToString())
}
