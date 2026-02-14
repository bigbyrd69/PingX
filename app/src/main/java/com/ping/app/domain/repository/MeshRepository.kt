package com.ping.app.domain.repository

import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import kotlinx.coroutines.flow.StateFlow

interface MeshRepository {
    val peers: StateFlow<List<Peer>>
    val packetFeed: StateFlow<List<MeshPacket>>

    suspend fun refreshPeers()
    suspend fun sendPacket(packet: MeshPacket)
    suspend fun receivePacket(packet: MeshPacket)
}
