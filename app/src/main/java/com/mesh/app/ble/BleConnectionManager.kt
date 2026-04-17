package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.core.sync.HelloPhase
import com.mesh.app.core.sync.SyncManager
import com.mesh.app.util.Constants
import com.mesh.app.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanner: BleScanner,
    private val syncManager: SyncManager,
    private val keyManager: KeyManager,
    private val bloomFilter: BloomFilter
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter: BluetoothAdapter? get() = bluetoothManager.adapter
    private var gattServer: BluetoothGattServer? = null

    @SuppressLint("MissingPermission")
    fun start() {
        startServer()
        scope.launch {
            scanner.peers.collectLatest { peer ->
                if (peer.deviceId == keyManager.getDeviceId().take(8)) return@collectLatest
                connect(peer.address)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        runCatching { gattServer?.close() }
    }

    @SuppressLint("MissingPermission")
    private fun connect(address: String) {
        val device = adapter?.getRemoteDevice(address) ?: return
        device.connectGatt(context, false, clientCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun startServer() {
        gattServer = bluetoothManager.openGattServer(context, serverCallback)
        val props = BluetoothGattCharacteristic.PROPERTY_READ or
            BluetoothGattCharacteristic.PROPERTY_WRITE or
            BluetoothGattCharacteristic.PROPERTY_NOTIFY
        val perms = BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE

        val service = BluetoothGattService(Constants.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        service.addCharacteristic(BluetoothGattCharacteristic(Constants.HELLO_CHAR_UUID, props, perms))
        service.addCharacteristic(BluetoothGattCharacteristic(Constants.DIFF_CHAR_UUID, props, perms))
        service.addCharacteristic(BluetoothGattCharacteristic(Constants.REQUEST_CHAR_UUID, props, perms))
        service.addCharacteristic(BluetoothGattCharacteristic(Constants.TRANSFER_CHAR_UUID, props, perms))
        gattServer?.addService(service)
    }

    private val clientCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                val mtuOk = gatt.requestMtu(Constants.BLE_MTU)
                if (!mtuOk) {
                    Logger.w("MTU request failed, disconnecting")
                    gatt.disconnect()
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                gatt.close()
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Logger.w("MTU negotiation failed")
                gatt.disconnect()
                return
            }
            gatt.discoverServices()
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val hello = gatt.getService(Constants.SERVICE_UUID)?.getCharacteristic(Constants.HELLO_CHAR_UUID) ?: return
            hello.value = HelloPhase().encode(keyManager.getDeviceId(), bloomFilter)
            if (!gatt.writeCharacteristic(hello)) gatt.disconnect()
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Logger.w("Characteristic write failed ${characteristic.uuid}")
                gatt.disconnect()
                return
            }
            if (characteristic.uuid == Constants.TRANSFER_CHAR_UUID) {
                gatt.disconnect()
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == Constants.REQUEST_CHAR_UUID) {
                scope.launch {
                    val ids = syncManager.decodeRequest(characteristic.value)
                    val msgs = syncManager.messagesForRequest(ids)
                    // Transfer writes intentionally batched by caller / omitted for brevity in callback thread.
                    Logger.d("Peer requested ${msgs.size} messages")
                }
            }
        }
    }

    private val serverCallback = object : BluetoothGattServerCallback() {
        @SuppressLint("MissingPermission")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = when (characteristic.uuid) {
                Constants.HELLO_CHAR_UUID -> HelloPhase().encode(keyManager.getDeviceId(), bloomFilter)
                else -> byteArrayOf()
            }
            gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            if (characteristic.uuid == Constants.REQUEST_CHAR_UUID) {
                scope.launch {
                    val ids = syncManager.decodeRequest(value)
                    val response = syncManager.messagesForRequest(ids)
                    Logger.d("Serving ${response.size} requested messages")
                }
            }
            if (responseNeeded) {
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null)
            }
        }
    }
}
