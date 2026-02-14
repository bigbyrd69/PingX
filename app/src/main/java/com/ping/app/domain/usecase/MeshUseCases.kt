package com.ping.app.domain.usecase

import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.repository.MeshRepository

class RefreshPeersUseCase(private val repository: MeshRepository) {
    suspend operator fun invoke() = repository.refreshPeers()
}

class SendPacketUseCase(private val repository: MeshRepository) {
    suspend operator fun invoke(packet: MeshPacket) = repository.sendPacket(packet)
}
