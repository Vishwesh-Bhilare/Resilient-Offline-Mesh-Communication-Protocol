package com.mesh.app.core.security

import java.util.concurrent.ConcurrentHashMap

class TokenBucketRateLimiter(
    private val maxTokens: Int,
    private val refillPerSecond: Double
) {
    private data class Bucket(var tokens: Double, var lastRefillMs: Long)
    private val buckets = ConcurrentHashMap<String, Bucket>()

    fun tryAcquire(key: String): Boolean {
        val now = System.currentTimeMillis()
        val b = buckets.computeIfAbsent(key) { Bucket(maxTokens.toDouble(), now) }
        synchronized(b) {
            val elapsed = (now - b.lastRefillMs) / 1000.0
            b.tokens = minOf(maxTokens.toDouble(), b.tokens + elapsed * refillPerSecond)
            b.lastRefillMs = now
            if (b.tokens >= 1.0) {
                b.tokens -= 1.0
                return true
            }
            return false
        }
    }
}

class RateLimiter { // FIX: 2 — remove constructor injection/scoping to avoid duplicate Hilt binding with @Provides.
    private val senderLimiter = TokenBucketRateLimiter(20, 20.0 / 60.0)
    private val globalLimiter = TokenBucketRateLimiter(200, 200.0 / 300.0)
    @Volatile
    private var sessionCount = 0

    @Synchronized
    fun resetSession() {
        sessionCount = 0
    }

    fun tryAcquire(senderId: String): Boolean {
        if (!globalLimiter.tryAcquire("global")) return false
        if (!senderLimiter.tryAcquire(senderId)) return false
        synchronized(this) {
            if (sessionCount >= 50) return false
            sessionCount++
        }
        return true
    }
}
