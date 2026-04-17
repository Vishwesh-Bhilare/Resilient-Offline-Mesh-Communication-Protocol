package com.mesh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.mesh.app.ble.BleAdvertiser
import com.mesh.app.ble.BleConnectionManager
import com.mesh.app.ble.BleScanner
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class MeshForegroundService : Service() {

    @Inject lateinit var advertiser: BleAdvertiser
    @Inject lateinit var scanner: BleScanner
    @Inject lateinit var connectionManager: BleConnectionManager
    @Inject lateinit var gatewayManager: GatewayManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1001, buildNotification())
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mesh:ble").apply { acquire(10 * 60 * 1000L) }
        advertiser.start()
        scanner.start(scope)
        connectionManager.start()
        gatewayManager.start()
    }

    override fun onDestroy() {
        advertiser.stop()
        scanner.stop()
        connectionManager.stop()
        gatewayManager.stop()
        wakeLock?.takeIf { it.isHeld }?.release()
        scope.cancel()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        val channelId = "mesh_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Mesh Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mesh protocol active")
            .setContentText("Scanning and relaying nearby messages")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOngoing(true)
            .build()
    }
}
