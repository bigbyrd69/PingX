package com.ping.app.domain.model

import java.util.UUID

enum class PacketType {
    SOS,
    TEXT,
    LOCATION
}

enum class DeliveryStatus {
    CREATED,
    DISCOVERED,
    RELAYED,
    DELIVERED,
    EXPIRED
}

enum class TransportType {
    WIFI_DIRECT,
    BLUETOOTH,
    NEARBY_CONNECTIONS
}

data class LocationPayload(
    val latitude: Double,
    val longitude: Double
)

sealed class PacketPayload {
    data class Text(val body: String) : PacketPayload()
    data class Location(val coordinates: LocationPayload) : PacketPayload()
    data object Sos : PacketPayload()
}

data class MeshPacket(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val targetId: String? = null,
    val packetType: PacketType,
    val payload: PacketPayload,
    val ttl: Int = 5,
    val hopCount: Int = 0,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val status: DeliveryStatus = DeliveryStatus.CREATED
)

data class Peer(
    val id: String,
    val alias: String,
    val transport: TransportType,
    val hopDistance: Int,
    val lastSeenEpochMs: Long
)
