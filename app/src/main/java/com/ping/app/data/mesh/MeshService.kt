package com.ping.app.data.mesh

import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn

class MeshService(
    private val transports: List<MeshTransport>,
    private val router: MeshRouter,
    scope: CoroutineScope
) {

    val peers: Flow<List<Peer>> = combine(transports.map { it.discoveredPeers }) { discovered ->
        discovered.flatMap { it }
            .distinctBy { it.id }
            .sortedBy { it.hopDistance }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val inboundPackets: Flow<MeshPacket> = kotlinx.coroutines.flow.merge(
        transports.map { transport ->
            transport.inboundPackets
        }
    ).flatMapMerge { packet ->
        kotlinx.coroutines.flow.flow {
            if (!router.shouldDrop(packet)) {
                emit(router.processIncoming(packet))
            }
        }
    }

    suspend fun start() {
        transports.forEach { it.start() }
    }

    suspend fun refreshPeers() {
        transports.forEach { it.refreshDiscovery() }
    }

    suspend fun broadcast(packet: MeshPacket, peers: List<Peer>) {
        val nextHops = router.nextHops(peers, packet.senderId)
        transports.forEach { transport ->
            nextHops.forEach { peer ->
                if (transport.connect(peer)) {
                    transport.send(packet, peer)
                }
            }
        }
    }
}
