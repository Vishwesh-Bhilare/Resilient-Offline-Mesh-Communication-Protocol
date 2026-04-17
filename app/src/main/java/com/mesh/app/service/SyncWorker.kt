package com.mesh.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mesh.app.data.local.db.InProgressDao
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val messageRepository: MessageRepository,
    private val gatewayManager: GatewayManager,
    private val inProgressDao: InProgressDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        messageRepository.cleanupAndEvict()
        gatewayManager.publishUnpublished()
        inProgressDao.deleteOlderThan(System.currentTimeMillis() - Constants.IN_PROGRESS_TIMEOUT_MS)
        return Result.success()
    }
}
