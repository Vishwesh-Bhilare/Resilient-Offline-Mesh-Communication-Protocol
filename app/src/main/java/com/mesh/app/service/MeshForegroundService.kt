package com.mesh.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
    @Inject lateinit var keyManager: KeyManager
    @Inject lateinit var bloomFilter: BloomFilter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var wakeLock: PowerManager.WakeLock? = null
    private var bleReceiver: BroadcastReceiver? = null

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

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bluetoothManager?.adapter

        if (adapter == null) {
            Log.w("MeshService", "Bluetooth not supported on this device/emulator. Running in limited mode.")
        }

        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mesh:ble")
            wakeLock?.acquire(10 * 60 * 1000L)

            gatewayManager.start()

            if (adapter != null) {
                registerBluetoothStateReceiver()

                if (!adapter.isEnabled) {
                    Log.w("MeshService", "Bluetooth is disabled; BLE components will start when Bluetooth is enabled")
                } else {
                    startBleComponents()
                }
            }
        } catch (e: Exception) {
            Log.e("MeshService", "Error initializing mesh components", e)
        }
    }

    private fun startBleComponents() {
        // FIX 3: Pre-initialize bloom filter with own device ID so advertisement is non-zero
        // and peers can use it for presence detection immediately.
        val deviceId = runCatching { keyManager.getDeviceId() }.getOrNull()
        if (deviceId != null) {
            bloomFilter.add(deviceId)
            Log.i("MeshService", "Bloom filter pre-initialized with own device ID")
        } else {
            Log.w("MeshService", "Could not retrieve device ID for bloom filter initialization")
        }

        advertiser.start()
        scanner.start(scope)
        connectionManager.start()
        Log.i("MeshService", "BLE components started")
    }

    private fun registerBluetoothStateReceiver() {
        // FIX 2: Monitor Bluetooth state changes so we can start/stop BLE components
        // when the user toggles Bluetooth on or off.
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        Log.i("MeshService", "Bluetooth enabled; starting BLE components")
                        startBleComponents()
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        Log.w("MeshService", "Bluetooth disabled; stopping BLE components")
                        advertiser.stop()
                        scanner.stop()
                        connectionManager.stop()
                    }
                }
            }
        }
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        bleReceiver = receiver
    }

    override fun onDestroy() {
        try {
            bleReceiver?.let { unregisterReceiver(it) }
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
}
