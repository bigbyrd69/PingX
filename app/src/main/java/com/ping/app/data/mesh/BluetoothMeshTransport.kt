package com.ping.app.data.mesh

import android.annotation.SuppressLint
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

    @SuppressLint("MissingPermission")
    override suspend fun refreshDiscovery() {
        val currentAdapter = adapter
        if (currentAdapter == null || !currentAdapter.isEnabled) {
            peerStream.value = emptyList()
            return
        }

        val bondedPeers = currentAdapter.bondedDevices.orEmpty().map { device ->
            Peer(
                id = device.address,
                alias = device.name ?: "BT-${device.address.takeLast(5)}",
                transport = TransportType.BLUETOOTH,
                hopDistance = 1,
                lastSeenEpochMs = System.currentTimeMillis()
            )
        }
        peerStream.value = bondedPeers
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(peer: Peer): Boolean {
        val currentAdapter = adapter ?: return false
        val device = currentAdapter.bondedDevices.orEmpty().firstOrNull { it.address == peer.id }
        return device != null
    }

    override suspend fun send(packet: MeshPacket, peer: Peer?) {
        // Transport payload channel is not yet implemented; keep relay path active.
        packetStream.emit(packet)
    }
}
