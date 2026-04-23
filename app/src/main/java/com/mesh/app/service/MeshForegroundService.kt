package com.mesh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
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
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.core.protocol.HlcClock
import com.mesh.app.core.protocol.Message
import com.mesh.app.data.repository.MessageRepository
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MeshForegroundService : Service() {

    @Inject lateinit var advertiser: BleAdvertiser
    @Inject lateinit var scanner: BleScanner
    @Inject lateinit var connectionManager: BleConnectionManager
    @Inject lateinit var gatewayManager: GatewayManager
    @Inject lateinit var keyManager: KeyManager
    @Inject lateinit var hlcClock: HlcClock
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var bloomFilter: BloomFilter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var wakeLock: PowerManager.WakeLock? = null
    private var presenceJob: Job? = null
    private var meshRunning = false

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
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DISABLE -> {
                stopMesh()
                stopSelf()
                return START_NOT_STICKY
            }

            ACTION_ENABLE, null -> {
                startMeshIfNeeded()
                return START_STICKY
            }

            else -> return START_STICKY
        }
    }

    private fun startMeshIfNeeded() {
        if (meshRunning) return
        meshRunning = true

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter

        if (adapter == null) {
            Log.w("MeshService", "Bluetooth not supported on this device/emulator. Running in mock/limited mode.")
        }

        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mesh:ble")
            wakeLock?.acquire(10 * 60 * 1000L)

            if (adapter != null) {
                advertiser.start()
                scanner.start(scope)
                connectionManager.start()
            }
            gatewayManager.start()
            startPresencePings()
        } catch (e: Exception) {
            Log.e("MeshService", "Error initializing mesh components", e)
        }
    }

    private fun stopMesh() {
        meshRunning = false
        presenceJob?.cancel()
        presenceJob = null

        try {
            advertiser.stop()
            scanner.stop()
            connectionManager.stop()
            gatewayManager.stop()
            wakeLock?.takeIf { it.isHeld }?.release()
            wakeLock = null
        } catch (e: Exception) {
            Log.e("MeshService", "Error during service stop", e)
        }
    }

    private fun startPresencePings() {
        if (presenceJob?.isActive == true) return
        presenceJob = scope.launch {
            while (isActive) {
                runCatching {
                    val content = "${Constants.PING_PREFIX}${keyManager.getDeviceId()}"
                    val ping = Message.create(content, Constants.CHANNEL_PRESENCE, keyManager, hlcClock.now())
                    messageRepository.save(ping)
                    bloomFilter.add(ping.id)
                    advertiser.refreshBloomAndReadvertise()
                }.onFailure {
                    Log.w("MeshService", "Presence ping failed", it)
                }
                delay(Constants.PRESENCE_PING_INTERVAL_MS)
            }
        }
    }

    override fun onDestroy() {
        stopMesh()
        scope.cancel()
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_ENABLE = "com.mesh.app.action.ENABLE_MESH"
        const val ACTION_DISABLE = "com.mesh.app.action.DISABLE_MESH"
    }
}
