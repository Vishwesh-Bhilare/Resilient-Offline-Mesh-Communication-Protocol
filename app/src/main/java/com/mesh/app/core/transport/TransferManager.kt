package com.mesh.app.core.transport

import com.mesh.app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class TransferManager {
    private val inFlight = ConcurrentHashMap<String, Int>()

    fun sendWithRetry(
        scope: CoroutineScope,
        messageId: String,
        sender: suspend () -> Boolean,
        onFailed: () -> Unit
    ) {
        inFlight[messageId] = 0
        scope.launch {
            while ((inFlight[messageId] ?: 0) < Constants.RETRY_MAX) {
                if (sender()) {
                    inFlight.remove(messageId)
                    return@launch
                }
                inFlight.computeIfPresent(messageId) { _, v -> v + 1 }
                delay(Constants.RETRY_TIMEOUT_MS)
            }
            inFlight.remove(messageId)
            onFailed()
        }
    }
}
