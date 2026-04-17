package com.mesh.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesh.app.ble.BleAdvertiser
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.HlcClock
import com.mesh.app.core.protocol.Message
import com.mesh.app.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val keyManager: KeyManager,
    private val hlcClock: HlcClock,
    private val advertiser: BleAdvertiser
) : ViewModel() {
    val messages: StateFlow<List<Message>> = messageRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(text: String, channelId: String?) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val msg = Message.create(text.trim(), channelId, keyManager, hlcClock.now())
            messageRepository.save(msg)
            advertiser.refreshBloomAndReadvertise()
        }
    }
}
