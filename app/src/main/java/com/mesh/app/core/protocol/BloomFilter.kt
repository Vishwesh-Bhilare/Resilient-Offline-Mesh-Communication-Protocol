package com.mesh.app.core.protocol

import com.mesh.app.util.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BloomFilter {
    private val bits = ByteArray(Constants.BLOOM_FILTER_BYTES)
    private val m = Constants.BLOOM_FILTER_BYTES * 8
    private val k = Constants.BLOOM_HASHES

    @Synchronized
    fun add(id: String) {
        val (h1, h2) = hashPair(id.toByteArray())
        for (i in 0 until k) {
            val combined = (h1 + i * h2).toLong() and 0x7fffffffffffffffL
            val bitIdx = (combined % m).toInt()
            setBit(bitIdx)
        }
    }

    @Synchronized
    fun mightContain(id: String): Boolean {
        val (h1, h2) = hashPair(id.toByteArray())
        for (i in 0 until k) {
            val combined = (h1 + i * h2).toLong() and 0x7fffffffffffffffL
            val bitIdx = (combined % m).toInt()
            if (!getBit(bitIdx)) return false
        }
        return true
    }

    @Synchronized
    fun toByteArray(): ByteArray = bits.copyOf()

    @Synchronized
    fun clear() = bits.fill(0)

    private fun setBit(idx: Int) {
        bits[idx / 8] = (bits[idx / 8].toInt() or (1 shl (idx % 8))).toByte()
    }

    private fun getBit(idx: Int): Boolean = (bits[idx / 8].toInt() and (1 shl (idx % 8))) != 0

    private fun hashPair(data: ByteArray): Pair<Int, Int> {
        val h1 = murmur3_32(data, 0x9747b28c.toInt())
        val h2 = murmur3_32(data, 0x5bd1e995)
        return h1 to if (h2 == 0) 1 else h2
    }

    private fun murmur3_32(data: ByteArray, seed: Int): Int {
        val c1 = 0xcc9e2d51.toInt()
        val c2 = 0x1b873593
        var h1 = seed
        val bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        while (bb.remaining() >= 4) {
            var k1 = bb.int
            k1 *= c1
            k1 = Integer.rotateLeft(k1, 15)
            k1 *= c2
            h1 = h1 xor k1
            h1 = Integer.rotateLeft(h1, 13)
            h1 = h1 * 5 + 0xe6546b64.toInt()
        }
        var k1 = 0
        when (bb.remaining()) {
            3 -> k1 = (bb.get(2).toInt() and 0xff shl 16) or (bb.get(1).toInt() and 0xff shl 8) or (bb.get(0).toInt() and 0xff)
            2 -> k1 = (bb.get(1).toInt() and 0xff shl 8) or (bb.get(0).toInt() and 0xff)
            1 -> k1 = (bb.get(0).toInt() and 0xff)
        }
        if (k1 != 0) {
            k1 *= c1
            k1 = Integer.rotateLeft(k1, 15)
            k1 *= c2
            h1 = h1 xor k1
        }
        h1 = h1 xor data.size
        h1 = h1 xor (h1 ushr 16)
        h1 *= 0x85ebca6b.toInt()
        h1 = h1 xor (h1 ushr 13)
        h1 *= 0xc2b2ae35.toInt()
        h1 = h1 xor (h1 ushr 16)
        return h1
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): BloomFilter {
            require(bytes.isNotEmpty() && bytes.size <= Constants.BLOOM_FILTER_BYTES) {
                "BloomFilter byte array must be 1..${Constants.BLOOM_FILTER_BYTES} bytes, got ${bytes.size}"
            }
            return BloomFilter().apply {
                System.arraycopy(bytes, 0, this.bits, 0, bytes.size)
            }
        }
    }
}
