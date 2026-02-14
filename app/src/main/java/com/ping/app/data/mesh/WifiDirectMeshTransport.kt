package com.ping.app.data.mesh

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.Peer
import com.ping.app.domain.model.TransportType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class WifiDirectMeshTransport(
    context: Context
) : MeshTransport {

    override val id: String = "wifi-direct"

    private val packetStream = MutableSharedFlow<MeshPacket>(extraBufferCapacity = 64)
    private val peerStream = MutableStateFlow<List<Peer>>(emptyList())

    override val inboundPackets: Flow<MeshPacket> = packetStream.asSharedFlow()
    override val discoveredPeers: Flow<List<Peer>> = peerStream.asStateFlow()

    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private val channel = manager?.initialize(appContext, appContext.mainLooper, null)

    override suspend fun start() {
        refreshDiscovery()
    }

    override suspend fun stop() = Unit

    override suspend fun refreshDiscovery() {
        if (!appContext.hasWifiDiscoveryPermission()) {
            peerStream.value = emptyList()
            return
        }

        val currentManager = manager
        val currentChannel = channel
        if (currentManager == null || currentChannel == null) {
            peerStream.value = emptyList()
            return
        }

        val discoverOk = runCatching {
            suspendCancellableCoroutine { continuation ->
                currentManager.discoverPeers(currentChannel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() = continuation.resume(Unit)
                    override fun onFailure(reason: Int) = continuation.resume(Unit)
                })
            }
        }.isSuccess

        if (!discoverOk) {
            peerStream.value = emptyList()
            return
        }

        val peers = runCatching {
            suspendCancellableCoroutine<List<WifiP2pDevice>> { continuation ->
                currentManager.requestPeers(currentChannel) { peerList ->
                    continuation.resume(peerList.deviceList.toList())
                }
            }
        }.getOrElse {
            emptyList()
        }

        peerStream.value = peers.map { device ->
            Peer(
                id = device.deviceAddress,
                alias = device.deviceName ?: "WiFi-${device.deviceAddress.takeLast(5)}",
                transport = TransportType.WIFI_DIRECT,
                hopDistance = 1,
                lastSeenEpochMs = System.currentTimeMillis()
            )
        }
    }

    override suspend fun connect(peer: Peer): Boolean {
        if (!appContext.hasWifiDiscoveryPermission()) return false
        return peerStream.value.any { it.id == peer.id }
    }

    override suspend fun send(packet: MeshPacket, peer: Peer?) {
        packetStream.emit(packet)
    }
}
