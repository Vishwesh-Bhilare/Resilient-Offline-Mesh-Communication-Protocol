package com.mesh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mesh.app.ble.BleAdvertiser
import com.mesh.app.ble.BleConnectionManager
import com.mesh.app.ble.BleScanner
import com.mesh.app.gateway.GatewayManager
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
        
        val notification = buildNotification()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
            } else {
                startForeground(1001, notification)
            }
        } catch (e: Exception) {
            Log.e("MeshService", "Failed to start foreground service", e)
            stopSelf()
            return
        }

        // Check if Bluetooth is even available (common null on emulators)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        
        if (adapter == null) {
            Log.w("MeshService", "Bluetooth not supported on this device/emulator. Running in mock/limited mode.")
        }

        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mesh:ble")
            wakeLock?.acquire(10 * 60 * 1000L)
            
            // Only start BLE components if adapter is available
            if (adapter != null) {
                advertiser.start()
                scanner.start(scope)
                connectionManager.start()
            }
            gatewayManager.start()
        } catch (e: Exception) {
            Log.e("MeshService", "Error initializing mesh components", e)
        }
    }

    override fun onDestroy() {
        try {
            advertiser.stop()
            scanner.stop()
            connectionManager.stop()
            gatewayManager.stop()
            wakeLock?.takeIf { it.isHeld }?.release()
        } catch (e: Exception) {
            Log.e("MeshService", "Error during service destruction", e)
        } finally {
            scope.cancel()
        }
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        val channelId = "mesh_service"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Mesh Service", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mesh protocol active")
            .setContentText("Scanning and relaying nearby messages")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Safer standard icon
            .setOngoing(true)
            .build()
    }
}
