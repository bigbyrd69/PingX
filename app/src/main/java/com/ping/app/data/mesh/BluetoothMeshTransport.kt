package com.ping.app.data.mesh

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import com.ping.app.domain.model.TransportType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothMeshTransport(
    private val context: Context
) : MeshTransport {

    override val id: String = "bluetooth"

    private val packetStream = MutableSharedFlow<MeshPacket>(extraBufferCapacity = 64)
    private val peerStream = MutableStateFlow<List<Peer>>(emptyList())

    override val inboundPackets: Flow<MeshPacket> = packetStream.asSharedFlow()
    override val discoveredPeers: Flow<List<Peer>> = peerStream.asStateFlow()

    private val adapter: BluetoothAdapter?
        get() = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    override suspend fun start() {
        refreshDiscovery()
    }

    override suspend fun stop() = Unit

    override suspend fun refreshDiscovery() {
        if (!context.hasBluetoothConnectPermission() || !context.hasBluetoothScanPermission()) {
            peerStream.value = emptyList()
            return
        }

        val currentAdapter = adapter
        if (currentAdapter == null || !currentAdapter.isEnabled) {
            peerStream.value = emptyList()
            return
        }

        peerStream.value = runCatching {
            currentAdapter.bondedDevices.orEmpty().map { device ->
                Peer(
                    id = device.address,
                    alias = device.name ?: "BT-${device.address.takeLast(5)}",
                    transport = TransportType.BLUETOOTH,
                    hopDistance = 1,
                    lastSeenEpochMs = System.currentTimeMillis()
                )
            }
        }.getOrElse {
            emptyList()
        }
    }

    override suspend fun connect(peer: Peer): Boolean {
        if (!context.hasBluetoothConnectPermission()) return false

        val currentAdapter = adapter ?: return false
        return runCatching {
            currentAdapter.bondedDevices.orEmpty().any { it.address == peer.id }
        }.getOrDefault(false)
    }

    override suspend fun send(packet: MeshPacket, peer: Peer?) {
        packetStream.emit(packet)
    }
}
