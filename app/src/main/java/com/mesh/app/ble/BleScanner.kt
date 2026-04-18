package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class PeerInfo(
    val deviceId: String,
    val hasInternet: Boolean,
    val bloomFilter: BloomFilter,
    val address: String
)

@Singleton
class BleScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val adapter: BluetoothAdapter?
        get() = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
            } else {
                BluetoothAdapter.getDefaultAdapter()
            }
        }.onFailure {
            Logger.w("Failed to obtain BluetoothAdapter for scanner", it)
        }.getOrNull()

    private val scanner get() = adapter?.bluetoothLeScanner
    private val _peers = MutableSharedFlow<PeerInfo>(extraBufferCapacity = 64)
    val peers: SharedFlow<PeerInfo> = _peers
    private var loopJob: Job? = null
    private var scanCallback: ScanCallback? = null

    @SuppressLint("MissingPermission")
    fun start(scope: CoroutineScope) {
        if (loopJob?.isActive == true) return
        val bleScanner = scanner
        if (bleScanner == null) {
            Logger.w("BLE scanner unavailable; skipping scan loop startup")
            return
        }
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val data = result.scanRecord?.getManufacturerSpecificData(Constants.BLE_MANUFACTURER_ID) ?: return
                if (data.size != Constants.BLE_PAYLOAD_SIZE) return
                val prefix = data.copyOfRange(0, 4).joinToString("") { "%02x".format(it) }
                val bloom = BloomFilter.fromByteArray(
                    data.copyOfRange(5, 5 + Constants.BLE_BLOOM_ADVERTISE_BYTES)
                )
                val hasInternet = (data[25].toInt() and 0xFF) != 0
                _peers.tryEmit(PeerInfo(prefix, hasInternet, bloom, result.device.address))
            }
        }
        scanCallback = callback
        loopJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                runCatching { bleScanner.startScan(null, settings, callback) }
                    .onFailure { t ->
                        if (t is SecurityException) {
                            Logger.w("Missing permission while starting BLE scan; pausing scan loop", t)
                            cancel()
                        } else {
                            Logger.e("Failed to start BLE scan", t)
                        }
                    }
                delay(Constants.BLE_SCAN_ACTIVE_MS)
                runCatching { bleScanner.stopScan(callback) }
                    .onFailure { t ->
                        if (t is SecurityException) {
                            Logger.w("Missing permission while stopping BLE scan", t)
                        } else {
                            Logger.e("Failed to stop BLE scan", t)
                        }
                    }
                delay(Constants.BLE_SCAN_PAUSE_MS)
            }
        }
        Logger.i("BLE scanner started")
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        loopJob?.cancel()
        val bleScanner = scanner
        val callback = scanCallback
        if (bleScanner != null && callback != null) {
            runCatching { bleScanner.stopScan(callback) }
                .onFailure { t ->
                    if (t is SecurityException) {
                        Logger.w("Missing permission while stopping BLE scan", t)
                    } else {
                        Logger.e("Failed to stop BLE scan", t)
                    }
                }
        }
        scanCallback = null
        Logger.i("BLE scanner stopped")
    }
}
