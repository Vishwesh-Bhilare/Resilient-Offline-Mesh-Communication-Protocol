package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleAdvertiser @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keyManager: KeyManager,
    private val bloomFilter: BloomFilter,
    private val gatewayManager: GatewayManager
) {
    private val adapter: BluetoothAdapter?
        get() = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
            } else {
                BluetoothAdapter.getDefaultAdapter()
            }
        }.onFailure {
            Logger.w("Failed to obtain BluetoothAdapter for advertiser", it)
        }.getOrNull()

    private val advertiser get() = adapter?.bluetoothLeAdvertiser
    private var callback: AdvertiseCallback? = null

    @SuppressLint("MissingPermission")
    @Synchronized
    fun start() {
        if (callback != null) return
        val bleAdvertiser = advertiser
        if (bleAdvertiser == null) {
            Logger.w("BLE advertiser unavailable; cannot start advertising")
            return
        }
        val settings = buildSettings()
        val cb = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Logger.i("BLE advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                Logger.e("BLE advertising failed to start, errorCode=$errorCode")
                callback = null
            }
        }
        runCatching {
            bleAdvertiser.startAdvertising(settings, buildData(), cb)
            callback = cb
        }.onFailure { t ->
            if (t is SecurityException) {
                Logger.w("Missing permission while starting BLE advertising; skipping start", t)
            } else {
                Logger.e("Failed to start BLE advertising", t)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        val bleAdvertiser = advertiser
        callback?.let { cb ->
            runCatching { bleAdvertiser?.stopAdvertising(cb) }
                .onFailure { t ->
                    if (t is SecurityException) {
                        Logger.w("Missing permission while stopping BLE advertising", t)
                    } else {
                        Logger.e("Failed to stop BLE advertising", t)
                    }
                }
        }
        callback = null
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun refreshBloomAndReadvertise() {
        val cb = callback ?: return
        val bleAdvertiser = advertiser
        if (bleAdvertiser == null) {
            Logger.w("BLE advertiser unavailable; cannot refresh advertising payload")
            callback = null
            return
        }
        runCatching {
            bleAdvertiser.stopAdvertising(cb)
            bleAdvertiser.startAdvertising(buildSettings(), buildData(), cb)
        }.onFailure { t ->
            if (t is SecurityException) {
                Logger.w("Missing permission while refreshing BLE advertising", t)
            } else {
                Logger.e("Failed to refresh BLE advertising payload", t)
            }
            callback = null
        }
    }

    private fun buildSettings(): AdvertiseSettings =
        AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .setConnectable(true)
            .build()

    private fun buildData(): AdvertiseData {
        val payload = ByteArray(Constants.BLE_PAYLOAD_SIZE)
        val idPrefix = runCatching {
            keyManager.getDeviceId().take(8).chunked(2).map { it.toInt(16).toByte() }
        }.getOrDefault(listOf(0, 0, 0, 0).map { it.toByte() })
        for (i in idPrefix.indices) payload[i] = idPrefix[i]
        payload[4] = 0
        val bloomBytes = bloomFilter.toByteArray()
        Logger.d("buildData: bloom non-zero bytes = ${bloomBytes.count { it != 0.toByte() }}")
        System.arraycopy(bloomBytes, 0, payload, 5, Constants.BLE_BLOOM_ADVERTISE_BYTES)
        payload[25] = if (gatewayManager.hasInternet()) 1 else 0
        payload[26] = Constants.PROTOCOL_VERSION.toByte()
        return AdvertiseData.Builder().addManufacturerData(Constants.BLE_MANUFACTURER_ID, payload).build()
    }
}
