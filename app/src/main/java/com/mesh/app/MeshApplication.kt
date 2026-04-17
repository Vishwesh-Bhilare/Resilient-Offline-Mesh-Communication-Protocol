package com.mesh.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.service.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        runBlocking(Dispatchers.IO) {
            messageRepository.ids().forEach { bloomFilter.add(it) }
        }
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "mesh_sync_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
