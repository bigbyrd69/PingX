package com.ping.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ping.app.domain.model.DeliveryStatus
import com.ping.app.domain.model.LocationPayload
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.PacketPayload
import com.ping.app.domain.model.PacketType
import com.ping.app.domain.model.Peer
import com.ping.app.domain.repository.MeshRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PingViewModel(
    private val repository: MeshRepository
) : ViewModel() {

    val peers: StateFlow<List<Peer>> = repository.peers
    val feed: StateFlow<List<MeshPacket>> = repository.packetFeed

    private val localNodeId = "local-node"

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    init {
        refreshPeers()
    }

    fun updateDraft(value: String) {
        _draft.value = value
    }

    fun refreshPeers() {
        viewModelScope.launch {
            repository.refreshPeers()
        }
    }

    fun sendText(isBroadcast: Boolean) {
        val text = draft.value.trim()
        if (text.isEmpty()) return

        val packet = MeshPacket(
            senderId = localNodeId,
            targetId = if (isBroadcast) null else "peer-alpha",
            packetType = PacketType.TEXT,
            payload = PacketPayload.Text(text),
            status = DeliveryStatus.CREATED
        )
        viewModelScope.launch {
            repository.sendPacket(packet)
            _draft.update { "" }
        }
    }

    fun sendSos() {
        val packet = MeshPacket(
            senderId = localNodeId,
            packetType = PacketType.SOS,
            payload = PacketPayload.Sos,
            ttl = 8,
            status = DeliveryStatus.CREATED
        )
        viewModelScope.launch {
            repository.sendPacket(packet)
        }
    }

    fun shareLocation(lat: Double, lng: Double) {
        val packet = MeshPacket(
            senderId = localNodeId,
            packetType = PacketType.LOCATION,
            payload = PacketPayload.Location(LocationPayload(lat, lng)),
            status = DeliveryStatus.CREATED
        )
        viewModelScope.launch {
            repository.sendPacket(packet)
        }
    }
}
