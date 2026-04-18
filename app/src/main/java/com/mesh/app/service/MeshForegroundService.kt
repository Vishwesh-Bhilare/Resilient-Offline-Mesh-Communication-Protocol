package com.mesh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothManager
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
    private var bleStarted = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // Must call startForeground immediately to avoid RemoteServiceException on Android 8+
        startForeground(1001, buildNotification())

        acquireWakeLockSafely()

        if (isBluetoothAvailable()) {
            startBleStack()
        } else {
            Logger.w("Bluetooth not available on onCreate — BLE stack not started")
        }

        // Gateway manager uses ConnectivityManager, safe to start regardless of BT state
        runCatching { gatewayManager.start() }
            .onFailure { Logger.e("GatewayManager start failed", it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!bleStarted && isBluetoothAvailable()) {
            startBleStack()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (bleStarted) {
            runCatching { advertiser.stop() }
            runCatching { scanner.stop() }
            runCatching { connectionManager.stop() }
        }
        runCatching { gatewayManager.stop() }
        releaseWakeLockSafely()
        scope.cancel()
        super.onDestroy()
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun startBleStack() {
        runCatching {
            advertiser.start()
            scanner.start(scope)
            connectionManager.start()
            bleStarted = true
            Logger.i("BLE stack started")
        }.onFailure {
            Logger.e("Failed to start BLE stack", it)
        }
    }

    private fun isBluetoothAvailable(): Boolean {
        return try {
            val btManager = getSystemService(BLUETOOTH_SERVICE) as? BluetoothManager
            val adapter = btManager?.adapter
            adapter != null && adapter.isEnabled
        } catch (t: Throwable) {
            Logger.w("isBluetoothAvailable check failed", t)
            false
        }
    }

    private fun acquireWakeLockSafely() {
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "mesh:ble_service"
            ).also {
                // 10-minute ceiling — WorkManager will re-trigger the service if needed
                it.acquire(10 * 60 * 1000L)
            }
        } catch (t: Throwable) {
            Logger.w("WakeLock acquire failed", t)
        }
    }

    private fun releaseWakeLockSafely() {
        try {
            wakeLock?.takeIf { it.isHeld }?.release()
        } catch (t: Throwable) {
            Logger.w("WakeLock release failed", t)
        } finally {
            wakeLock = null
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "mesh_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mesh Service",
                NotificationManager.IMPORTANCE_LOW
            )
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
