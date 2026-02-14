package com.ping.app.data.mesh

import com.ping.app.domain.model.DeliveryStatus
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer

class MeshRouter(
    private val packetStore: PacketStore
) {
    fun shouldDrop(packet: MeshPacket): Boolean {
        return packet.ttl <= 0 || packetStore.contains(packet.id)
    }

    fun processIncoming(packet: MeshPacket): MeshPacket {
        packetStore.append(packet)
        val ttl = (packet.ttl - 1).coerceAtLeast(0)
        return packet.copy(
            ttl = ttl,
            hopCount = packet.hopCount + 1,
            status = if (ttl == 0) DeliveryStatus.EXPIRED else DeliveryStatus.RELAYED
        )
    }

    fun nextHops(peers: List<Peer>, originId: String): List<Peer> {
        return peers.filterNot { it.id == originId }
    }
}
