package com.mesh.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Base64
import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.core.security.RateLimiter
import com.mesh.app.core.sync.HelloPhase
import com.mesh.app.core.sync.SyncManager
import com.mesh.app.core.transport.MessageChunk
import com.mesh.app.data.local.entity.PeerEntity
import com.mesh.app.data.repository.PeerRepository
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
    private val bloomFilter: BloomFilter,
    private val peerRepository: PeerRepository,
    private val rateLimiter: RateLimiter
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
                rateLimiter.resetSession()
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
            if (!gatt.writeCharacteristic(hello)) {
                gatt.disconnect()
                return
            }
            scope.launch {
                peerRepository.upsert(
                    PeerEntity(
                        peerId = gatt.device.address,
                        lastSyncTime = System.currentTimeMillis(),
                        syncHash = "",
                        address = gatt.device.address
                    )
                )
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Logger.w("Characteristic write failed ${characteristic.uuid}")
                gatt.disconnect()
                return
            }
            if (characteristic.uuid == Constants.HELLO_CHAR_UUID) {
                // TODO: Full sync flow:
                // 1. Write HELLO → receive peer HELLO back (read HELLO_CHAR)
                // 2. Compute DIFF using peer's bloom filter from HELLO payload
                // 3. Write REQUEST with list of needed IDs to REQUEST_CHAR
                // 4. Receive TRANSFER chunks via notifications on TRANSFER_CHAR
                // 5. Disconnect
                val hello = gatt.getService(Constants.SERVICE_UUID)?.getCharacteristic(Constants.HELLO_CHAR_UUID)
                if (hello != null && !gatt.readCharacteristic(hello)) {
                    gatt.disconnect()
                }
                return
            }
            if (characteristic.uuid == Constants.TRANSFER_CHAR_UUID) {
                gatt.disconnect()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS || characteristic.uuid != Constants.HELLO_CHAR_UUID) return
            scope.launch {
                runCatching {
                    val payload = HelloPhase().decode(value)
                    val peerBloom = BloomFilter.fromByteArray(Base64.decode(payload.bloomFilter, Base64.NO_WRAP))
                    val resolvedPeerId = payload.deviceId.ifBlank { gatt.device.address }

                    peerRepository.upsert(
                        PeerEntity(
                            peerId = resolvedPeerId,
                            lastSyncTime = System.currentTimeMillis(),
                            syncHash = "",
                            address = gatt.device.address
                        )
                    )

                    if (!syncManager.shouldSync(resolvedPeerId, peerRepository)) {
                        Logger.d("Skipping sync for recently synced peer $resolvedPeerId")
                        gatt.disconnect()
                        return@launch
                    }

                    val neededIds = syncManager.buildRequestedIds(peerBloom)
                    val request = gatt.getService(Constants.SERVICE_UUID)?.getCharacteristic(Constants.REQUEST_CHAR_UUID)
                    if (request == null) {
                        gatt.disconnect()
                        return@launch
                    }
                    request.value = syncManager.encodeRequest(neededIds)
                    if (!gatt.writeCharacteristic(request)) {
                        gatt.disconnect()
                    }
                }.onFailure {
                    Logger.w("Failed to process peer HELLO", it)
                    gatt.disconnect()
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            if (characteristic.uuid == Constants.REQUEST_CHAR_UUID) {
                scope.launch {
                    val ids = syncManager.decodeRequest(value)
                    val msgs = syncManager.messagesForRequest(ids)
                    // Transfer writes intentionally batched by caller / omitted for brevity in callback thread.
                    Logger.d("Peer requested ${msgs.size} messages")
                }
            }
            if (characteristic.uuid == Constants.TRANSFER_CHAR_UUID) {
                scope.launch {
                    runCatching {
                        val chunk = MessageChunk.fromByteArray(value)
                        syncManager.ingestTransferredChunk(chunk)
                    }.onFailure { Logger.w("Failed to ingest transfer chunk", it) }
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
