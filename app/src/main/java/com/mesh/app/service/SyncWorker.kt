package com.mesh.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mesh.app.data.repository.InProgressRepository
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val messageRepository: MessageRepository,
    private val gatewayManager: GatewayManager,
    private val inProgressRepository: InProgressRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Logger.i("SyncWorker doWork started") // FIX: 7 — log worker execution start for Hilt/WorkManager verification
        messageRepository.cleanupAndEvict()
        gatewayManager.publishUnpublished()
        inProgressRepository.cleanup(System.currentTimeMillis() - Constants.IN_PROGRESS_TIMEOUT_MS)
        Logger.i("SyncWorker doWork completed successfully") // FIX: 7 — log worker execution completion
        return Result.success()
    }
}
