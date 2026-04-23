package com.mesh.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesh.app.data.repository.PeerRepository
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.service.MeshRuntimeController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    peerRepository: PeerRepository,
    gatewayManager: GatewayManager,
    private val meshRuntimeController: MeshRuntimeController
) : ViewModel() {
    val peerCount: StateFlow<Int> = peerRepository.observePeers()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private fun safeHasInternet(gatewayManager: GatewayManager): Boolean =
        runCatching { gatewayManager.hasInternet() }.getOrDefault(false)

    val isGateway: StateFlow<Boolean> = kotlinx.coroutines.flow.flowOf(safeHasInternet(gatewayManager))
        .stateIn(viewModelScope, SharingStarted.Eagerly, safeHasInternet(gatewayManager))

    val isMeshEnabled: StateFlow<Boolean> = meshRuntimeController.enabled

    fun setMeshEnabled(enabled: Boolean) {
        meshRuntimeController.setEnabled(enabled)
    }
}
