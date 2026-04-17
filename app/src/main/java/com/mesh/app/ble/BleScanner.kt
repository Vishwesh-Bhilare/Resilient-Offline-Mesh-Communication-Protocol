package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class BleScanner @Inject constructor() {
    private val scanner get() = BluetoothAdapter.getDefaultAdapter()?.bluetoothLeScanner
    private val _peers = MutableSharedFlow<PeerInfo>(extraBufferCapacity = 64)
    val peers: SharedFlow<PeerInfo> = _peers
    private var loopJob: Job? = null

    @SuppressLint("MissingPermission")
    fun start(scope: CoroutineScope) {
        if (loopJob?.isActive == true) return
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val data = result.scanRecord?.getManufacturerSpecificData(Constants.BLE_MANUFACTURER_ID) ?: return
                if (data.size != Constants.BLE_PAYLOAD_SIZE) return
                val prefix = data.copyOfRange(0, 4).joinToString("") { "%02x".format(it) }
                val bloom = BloomFilter.fromByteArray(data.copyOfRange(5, 25))
                val hasInternet = data[25].toInt() == 1
                _peers.tryEmit(PeerInfo(prefix, hasInternet, bloom, result.device.address))
            }
        }
        loopJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                runCatching { scanner?.startScan(null, settings, callback) }
                delay(Constants.BLE_SCAN_ACTIVE_MS)
                runCatching { scanner?.stopScan(callback) }
                delay(Constants.BLE_SCAN_PAUSE_MS)
            }
        }
        Logger.i("BLE scanner started")
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        loopJob?.cancel()
        Logger.i("BLE scanner stopped")
    }
}
