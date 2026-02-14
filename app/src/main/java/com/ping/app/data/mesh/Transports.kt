package com.ping.app.data.mesh

import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import kotlinx.coroutines.flow.Flow

interface MeshTransport {
    val id: String
    val inboundPackets: Flow<MeshPacket>
    val discoveredPeers: Flow<List<Peer>>

    suspend fun start()
    suspend fun stop()
    suspend fun refreshDiscovery()
    suspend fun connect(peer: Peer): Boolean
    suspend fun send(packet: MeshPacket, peer: Peer?)
}

    suspend fun send(packet: MeshPacket, peer: Peer?)
}

class WifiDirectTransportStub : BaseTransportStub("wifi-direct")

class BluetoothTransportStub : BaseTransportStub("bluetooth")

class NearbyConnectionsTransportStub : BaseTransportStub("nearby-connections")
