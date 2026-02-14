package com.ping.app.data.repository

import android.content.Context
import com.ping.app.data.mesh.BluetoothMeshTransport
import com.ping.app.data.mesh.InMemoryPacketStore
import com.ping.app.data.mesh.MeshRouter
import com.ping.app.data.mesh.MeshService
import com.ping.app.data.mesh.NearbyConnectionsTransportStub
import com.ping.app.data.mesh.WifiDirectMeshTransport
import com.ping.app.domain.model.DeliveryStatus
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import com.ping.app.domain.repository.MeshRepository
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)

    private val service = MeshService(
        transports = listOf(
            WifiDirectMeshTransport(context),
            BluetoothMeshTransport(context),
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
            runCatching {
                service.start()
            }
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
        runCatching {
            service.refreshPeers()
        }
    }

    override suspend fun sendPacket(packet: MeshPacket) {
        val outbound = packet.copy(status = DeliveryStatus.DISCOVERED)
        packetState.update { listOf(outbound) + it }
        runCatching {
            service.broadcast(outbound, peers.value)
        }
    }

    override suspend fun receivePacket(packet: MeshPacket) {
        packetState.update {
            (listOf(packet.copy(status = DeliveryStatus.DELIVERED)) + it).distinctBy { value -> value.id }
        }
    }
}
