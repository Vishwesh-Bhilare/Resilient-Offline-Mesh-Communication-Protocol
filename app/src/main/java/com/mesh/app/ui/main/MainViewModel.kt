package com.mesh.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesh.app.data.repository.PeerRepository
import com.mesh.app.gateway.GatewayManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    peerRepository: PeerRepository,
    gatewayManager: GatewayManager
) : ViewModel() {
    val peerCount: StateFlow<Int> = peerRepository.observePeers()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val isGateway: StateFlow<Boolean> = kotlinx.coroutines.flow.flowOf(gatewayManager.hasInternet())
        .stateIn(viewModelScope, SharingStarted.Eagerly, gatewayManager.hasInternet())
}
