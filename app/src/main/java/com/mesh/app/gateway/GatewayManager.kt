package com.mesh.app.gateway

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatewayManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val apiService: ApiService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            scope.launch { publishUnpublished() }
        }
    }

    fun start() {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    fun stop() {
        runCatching { connectivityManager.unregisterNetworkCallback(callback) }
        scope.cancel()
    }

    suspend fun publishUnpublished() {
        val list = messageRepository.unpublished()
        for (msg in list) {
            runCatching {
                val resp = apiService.publishMessage(MessageDto.from(msg))
                if (resp.code() == 200 || resp.code() == 201) {
                    messageRepository.markPublished(msg.id)
                }
            }.onFailure { Logger.w("Gateway publish failed for ${msg.id}", it) }
        }
    }

    fun hasInternet(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
