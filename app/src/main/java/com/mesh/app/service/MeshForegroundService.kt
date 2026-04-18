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

        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mesh:ble").apply {
            acquire(10 * 60 * 1000L) // 10 minute max to avoid infinite lock
        }

        if (isBluetoothAvailable()) {
            startBleStack()
        } else {
            Logger.w("Bluetooth not available — BLE stack not started")
        }

        gatewayManager.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If Bluetooth became available after initial onCreate, start BLE now
        if (!bleStarted && isBluetoothAvailable()) {
            startBleStack()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (bleStarted) {
            advertiser.stop()
            scanner.stop()
            connectionManager.stop()
        }
        gatewayManager.stop()
        wakeLock?.takeIf { it.isHeld }?.release()
        scope.cancel()
        super.onDestroy()
    }

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
        val btManager = getSystemService(BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = btManager?.adapter
        return adapter != null && adapter.isEnabled
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
