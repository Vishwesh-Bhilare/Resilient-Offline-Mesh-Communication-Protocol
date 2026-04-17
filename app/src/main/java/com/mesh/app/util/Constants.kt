package com.mesh.app.util

import java.util.UUID

object Constants {
    const val PROTOCOL_VERSION = 1
    const val DEFAULT_TTL = 21600
    const val MAX_HOPS = 10
    const val MAX_DB_SIZE_BYTES = 50 * 1024 * 1024L
    const val CHUNK_DATA_SIZE = 476
    const val BLOOM_FILTER_BYTES = 20
    const val IN_PROGRESS_TIMEOUT_MS = 10 * 60 * 1000L
    const val GATEWAY_BASE_URL = "https://your-gateway.example.com/"
    const val BLOOM_HASHES = 7
    const val BLE_MTU = 512
    const val BLE_SCAN_ACTIVE_MS = 5_000L
    const val BLE_SCAN_PAUSE_MS = 25_000L
    const val BLE_MANUFACTURER_ID = 0x1234
    // Layout: [0..3]=device prefix (4), [4]=flags (1), [5..24]=bloom filter (20), [25]=has_internet (1), [26]=protocol_version (1).
    // Total = 27 bytes and scanner uses copyOfRange(5, 25) for the 20-byte bloom segment.
    const val BLE_PAYLOAD_SIZE = 27
    const val RETRY_TIMEOUT_MS = 5_000L
    const val RETRY_MAX = 3

    val SERVICE_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")
    val HELLO_CHAR_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789001")
    val DIFF_CHAR_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789002")
    val REQUEST_CHAR_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789003")
    val TRANSFER_CHAR_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789004")
}
