package com.ping.app.data.mesh

import com.ping.app.domain.model.MeshPacket

interface PacketStore {
    fun append(packet: MeshPacket)
    fun all(): List<MeshPacket>
    fun contains(packetId: String): Boolean
}

class InMemoryPacketStore : PacketStore {
    private val items = mutableListOf<MeshPacket>()

    override fun append(packet: MeshPacket) {
        if (items.none { it.id == packet.id }) {
            items += packet
        }
    }

    override fun all(): List<MeshPacket> = items.toList()

    override fun contains(packetId: String): Boolean = items.any { it.id == packetId }
}
