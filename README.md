# Ping (Android Mesh Emergency App)

Ping is an offline-first emergency communication prototype for disaster scenarios. It demonstrates clean architecture boundaries and mesh networking behavior with production-ready interfaces and partially integrated transport discovery.

## Stack
- Kotlin
- Jetpack Compose
- Material 3
- Clean architecture (domain/data/ui)

## Architecture

### Domain layer
- `MeshPacket`, `Peer`, packet/transport/status enums
- Payload types (`Text`, `Location`, `SOS`)
- Repository abstraction (`MeshRepository`)

### Data layer
- Transport abstraction (`MeshTransport`) with implementations:
  - Wi-Fi Direct discovery transport
  - Bluetooth bonded-peer discovery transport
  - Nearby Connections (optional stub)
- `MeshRouter` for TTL decrement, deduplication, and next-hop resolution
- `PacketStore` for message ID deduplication
- `MeshService` for store-and-forward orchestration across transports
- `MeshRepositoryImpl` connecting service flows to UI state

### UI layer
- `PingApp.kt` tab host only (SOS / Messages / Peers)
- `SosScreen.kt`
- `MessageScreen.kt`
- `PeerScreen.kt`
- Componentized list/graph cards

## Features
- SOS high-priority broadcast packet
- Location packet sharing (lat/lng payload)
- Text messaging with broadcast/direct mode toggle
- Peer discovery list with transport + hop distance
- Delivery status in feed (`CREATED`, `DISCOVERED`, `RELAYED`, etc.)
- Packet visualization panel for TTL/hop progression
- Black + neon green hacker-style Material 3 theme
- Adaptive launcher icon with mesh node visual

## Run
1. Open in Android Studio (Jellyfish+ recommended).
2. Sync Gradle.
3. Run on emulator/device (API 26+).
4. Grant nearby, location, and bluetooth permissions at runtime.

## Permissions (placeholders included)
- Fine/coarse location
- Bluetooth + Bluetooth admin/scan/connect
- Nearby Wi-Fi devices
- Wi-Fi state/change
- Internet (optional for future relays/telemetry)

## What is implemented vs. stubbed

### Implemented
- Full project scaffold with Compose + Material 3.
- Domain/data/ui separation.
- Packet model supporting TTL, hop count, status, dedupe ID.
- Router logic for deduplication + TTL decrement.
- Repository/ViewModel wiring and interactive screens.
- Adaptive icon setup and custom branding resources.
- Wi-Fi Direct and Bluetooth peer discovery integration points.

### Stubbed (mocked but production-ready interfaces)
- Actual packet socket/session data channels for Wi-Fi Direct and Bluetooth payload transfer.
- Nearby Connections transport integration.
- Real multi-hop route metric optimization and ACK protocol.
- Reliable persistence layer (currently in-memory packet store).
