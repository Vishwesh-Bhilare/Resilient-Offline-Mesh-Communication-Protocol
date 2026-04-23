package com.mesh.app.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesh.app.core.protocol.Message
import com.mesh.app.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    messageRepository: MessageRepository
) : ViewModel() {
    val presenceLogs: StateFlow<List<Message>> = messageRepository.observePresenceLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
