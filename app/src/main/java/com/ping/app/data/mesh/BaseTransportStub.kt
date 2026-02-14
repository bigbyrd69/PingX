package com.ping.app.data.mesh

import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseTransportStub(
    override val id: String
) : MeshTransport {

    private val packetStream = MutableSharedFlow<MeshPacket>(extraBufferCapacity = 64)
    private val peerStream = MutableStateFlow<List<Peer>>(emptyList())

    override val inboundPackets: Flow<MeshPacket> = packetStream.asSharedFlow()
    override val discoveredPeers: Flow<List<Peer>> = peerStream.asStateFlow()

    override suspend fun start() {
        delay(40)
    }

    override suspend fun stop() {
        delay(20)
    }

    override suspend fun send(packet: MeshPacket, peer: Peer?) {
        // Stub echo to simulate eventual relay acknowledgement.
        delay(80)
        packetStream.emit(packet)
    }
}
