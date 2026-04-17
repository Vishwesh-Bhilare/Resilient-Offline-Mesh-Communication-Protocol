package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleAdvertiser @Inject constructor(
    private val keyManager: KeyManager,
    private val bloomFilter: BloomFilter,
    private val gatewayManager: GatewayManager
) {
    private val advertiser get() = BluetoothAdapter.getDefaultAdapter()?.bluetoothLeAdvertiser
    private var callback: AdvertiseCallback? = null

    @SuppressLint("MissingPermission")
    @Synchronized
    fun start() {
        if (callback != null) return
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .setConnectable(true)
            .build()
        val cb = object : AdvertiseCallback() {}
        callback = cb
        advertiser?.startAdvertising(settings, buildData(), cb)
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        callback?.let { advertiser?.stopAdvertising(it) }
        callback = null
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun refreshBloomAndReadvertise() {
        val cb = callback ?: return
        advertiser?.stopAdvertising(cb)
        advertiser?.startAdvertising(
            AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .setConnectable(true)
                .build(),
            buildData(),
            cb
        )
    }

    private fun buildData(): AdvertiseData {
        val payload = ByteArray(Constants.BLE_PAYLOAD_SIZE)
        val idPrefix = keyManager.getDeviceId().take(8).chunked(2).map { it.toInt(16).toByte() }
        for (i in idPrefix.indices) payload[i] = idPrefix[i]
        payload[4] = 0
        System.arraycopy(bloomFilter.toByteArray(), 0, payload, 5, Constants.BLE_BLOOM_ADVERTISE_BYTES)
        // has_internet: 1 = yes, 0 = no. Reader should treat any non-zero as true.
        payload[25] = if (gatewayManager.hasInternet()) 1 else 0
        payload[26] = Constants.PROTOCOL_VERSION.toByte()
        return AdvertiseData.Builder().addManufacturerData(Constants.BLE_MANUFACTURER_ID, payload).build()
    }
}
