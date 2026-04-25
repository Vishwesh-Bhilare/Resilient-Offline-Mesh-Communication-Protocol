# Kotlin to Flutter migration notes

This folder provides the Flutter replacement application layer for the original Android implementation.

## Module mapping

- `ui/main`, `ui/chat`, `ui/logs` (Kotlin) -> `lib/ui/screens/*` (Flutter widgets)
- `core/protocol/HLC.kt`, `Message.kt` -> `lib/core/protocol/hlc.dart`, `lib/data/models/message.dart`
- `gateway/GatewayManager.kt` -> `lib/services/gateway_manager.dart`
- `service/MeshRuntimeController.kt` -> `lib/services/mesh_runtime_controller.dart`

## Outstanding items

1. BLE advertiser/scanner/connection manager currently requires Flutter platform channels (`android/` + `ios/`) or a plugin strategy.
2. Persistent storage (Room) should be replaced with `drift` or `sqflite` in a follow-up.
3. WorkManager/background execution requires per-platform integration.
