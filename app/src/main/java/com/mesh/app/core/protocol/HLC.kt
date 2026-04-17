package com.mesh.app.core.protocol

import kotlinx.serialization.Serializable

@Serializable
data class HLC(
    val physicalMs: Long,
    val counter: Int,
    val deviceId: String
) : Comparable<HLC> {
    override fun compareTo(other: HLC): Int {
        val p = other.physicalMs.compareTo(physicalMs)
        if (p != 0) return p
        val c = other.counter.compareTo(counter)
        if (c != 0) return c
        return deviceId.compareTo(other.deviceId)
    }

    override fun toString(): String = "$physicalMs:$counter:$deviceId"

    companion object {
        fun fromString(raw: String): HLC {
            val parts = raw.split(":", limit = 3)
            require(parts.size == 3)
            return HLC(parts[0].toLong(), parts[1].toInt(), parts[2])
        }
    }
}

class HlcClock(private val deviceId: String) {
    @Volatile private var current = HLC(System.currentTimeMillis(), 0, deviceId)

    @Synchronized
    fun now(): HLC {
        val wall = System.currentTimeMillis()
        current = if (wall > current.physicalMs) {
            HLC(wall, 0, deviceId)
        } else {
            HLC(current.physicalMs, current.counter + 1, deviceId)
        }
        return current
    }

    @Synchronized
    fun update(received: HLC): HLC {
        val wall = System.currentTimeMillis()
        val maxPhysical = maxOf(wall, current.physicalMs, received.physicalMs)
        val counter = when {
            maxPhysical == current.physicalMs && maxPhysical == received.physicalMs -> maxOf(current.counter, received.counter) + 1
            maxPhysical == current.physicalMs -> current.counter + 1
            maxPhysical == received.physicalMs -> received.counter + 1
            else -> 0
        }
        current = HLC(maxPhysical, counter, deviceId)
        return current
    }
}
