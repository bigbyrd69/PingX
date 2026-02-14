package com.ping.app.data.repository

import android.content.Context
import com.ping.app.data.mesh.BluetoothMeshTransport
import com.ping.app.data.mesh.BluetoothTransportStub
import com.ping.app.data.mesh.InMemoryPacketStore
import com.ping.app.data.mesh.MeshRouter
import com.ping.app.data.mesh.MeshService
import com.ping.app.data.mesh.NearbyConnectionsTransportStub
import com.ping.app.data.mesh.WifiDirectMeshTransport
import com.ping.app.domain.model.DeliveryStatus
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import com.ping.app.data.mesh.WifiDirectTransportStub
import com.ping.app.domain.model.DeliveryStatus
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import com.ping.app.domain.model.TransportType
import com.ping.app.domain.repository.MeshRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeshRepositoryImpl(
    context: Context
) : MeshRepository {
class MeshRepositoryImpl : MeshRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val service = MeshService(
        transports = listOf(
            WifiDirectMeshTransport(context),
            BluetoothMeshTransport(context),
            WifiDirectTransportStub(),
            BluetoothTransportStub(),
            NearbyConnectionsTransportStub()
        ),
        router = MeshRouter(InMemoryPacketStore()),
        scope = scope
    )

    private val peerState = MutableStateFlow<List<Peer>>(emptyList())
    override val peers: StateFlow<List<Peer>> = peerState.asStateFlow()

    private val packetState = MutableStateFlow<List<MeshPacket>>(emptyList())
    override val packetFeed: StateFlow<List<MeshPacket>> = packetState.asStateFlow()

    init {
        scope.launch {
            service.start()
            service.peers.collect { peerState.value = it }
        }
        scope.launch {
            service.inboundPackets.collect { incoming ->
                packetState.update { current ->
                    (listOf(incoming.copy(status = DeliveryStatus.RELAYED)) + current)
                        .distinctBy { it.id }
                }
            }
        }
    }

    override suspend fun refreshPeers() {
        service.refreshPeers()
        // Mocked discovery results to simulate multi-hop proximity.
        peerState.value = listOf(
            Peer("peer-alpha", "Alpha", TransportType.WIFI_DIRECT, 1, System.currentTimeMillis()),
            Peer("peer-bravo", "Bravo", TransportType.BLUETOOTH, 2, System.currentTimeMillis()),
            Peer("peer-charlie", "Charlie", TransportType.NEARBY_CONNECTIONS, 3, System.currentTimeMillis())
        )
    }

    override suspend fun sendPacket(packet: MeshPacket) {
        val outbound = packet.copy(status = DeliveryStatus.DISCOVERED)
        packetState.update { listOf(outbound) + it }
        service.broadcast(outbound, peers.value)
    }

    override suspend fun receivePacket(packet: MeshPacket) {
        packetState.update {
            (listOf(packet.copy(status = DeliveryStatus.DELIVERED)) + it).distinctBy { value -> value.id }
        }
    }
}
