package com.mesh.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.service.SyncWorker
import com.mesh.app.util.Logger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MeshApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var bloomFilter: BloomFilter

    // Application-scoped coroutine scope — cancelled when process dies
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        // Populate bloom filter off the main thread — never block onCreate()
        applicationScope.launch {
            runCatching {
                messageRepository.ids().forEach { bloomFilter.add(it) }
            }.onFailure { Logger.e("Failed to populate bloom filter on startup", it) } // FIX: 8 — catch and log startup bloom population failures
        }

        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "mesh_sync_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
