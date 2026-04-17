# Resilient Offline Mesh Communication Protocol

## 1. Overview
This Android DTN protocol provides decentralized BLE relay, eventually-consistent gossip sync, message signing, anti-flood controls, and optional internet gateway publishing.

## 2. Identity and Keys
- Ed25519 keypair generated at first launch (BouncyCastle provider).
- Keys are persisted in `EncryptedSharedPreferences` under encrypted values.
- `device_id = SHA-256(publicKey.encoded)` in lowercase hex.
- APIs:
  - `KeyManager.getOrCreateKeyPair(): KeyPair`
  - `KeyManager.getDeviceId(): String`

## 3. Message Schema
`Message` serialized with kotlinx.serialization JSON:
- `id`: `sha256(sender + timestamp + content)`
- `sender`: device id
- `public_key`: base64 X.509-encoded Ed25519 public key
- `timestamp`: epoch seconds
- `hlc`: hybrid logical clock tuple
- `ttl`: default `21600`
- `content`
- `hops`: incremented on relay
- `signature`: base64 Ed25519 signature over `id + content`
- `size`: JSON payload byte size
- `priority`: integer (default 0)
- `channel_id`: optional string namespace

## 4. HLC Rules
Serialized format: `physicalMs:counter:deviceId`
- `now()` advances by wall-clock or logical counter.
- `update(received)` merges local and received clocks with monotonic safety.
- Sorting order for chat feeds: physical desc, counter desc, device id tie-break.

## 5. Bloom Filter
- 20 bytes (160 bits), 7 hashes.
- Two-hash trick using Murmur3 hash pair:
  - `h_i = h1 + i * h2`
- Used in BLE advertisement and sync diff planning.

## 6. BLE Advertisement Payload (27 bytes)
`[idPrefix(4)][flags(1)][bloom(20)][hasInternet(1)][protocolVersion(1)]`
- Low-power mode + low TX power.
- Re-advertised after new local message insertion.

## 7. BLE Scan
- Duty cycle: scan 5s, pause 25s.
- Parses manufacturer data and emits `PeerInfo(deviceIdPrefix, hasInternet, bloom, address)` through Flow.

## 8. GATT and Sync Phases
Service UUID and characteristic UUIDs are constants.
All chars are read+write+notify.

### Phase 0: MTU
- Central requests MTU 512 immediately on connection.

### Phase 1: HELLO
JSON: `{deviceId, protocolVersion, capabilities, bloomFilter(base64)}`

### Phase 2: DIFF
Compare peer bloom against local IDs to find candidate requests.

### Phase 3: REQUEST
JSON list of requested IDs.

### Phase 4: TRANSFER
Chunk format: `[msgId 32B][chunkIdx 2B][total 2B][payload N]`
- Payload max 476 bytes.
- Chunks reassembled in order; complete set required.

### Phase 5: CLOSE
Disconnect after transfer completion or error.

## 9. Rate Limiting
- Per sender token bucket: 20/min.
- Global token bucket: 200/5min.
- Session cap: 50 accepted messages.

## 10. Storage
Room DB entities:
- `messages`
- `in_progress`
- `peers`

Cleanup and eviction:
1. Delete expired (`receivedAt + ttl * 1000 < now`).
2. If DB > 50MB, delete oldest published first.
3. Re-delete expired, then oldest overall if still oversized.

## 11. Gateway Publishing
- Connectivity callback triggers upload pass.
- Unpublished messages POSTed to `/api/messages`.
- Mark as published only on HTTP 200/201.
- Runs on IO scope, independent from BLE loops.

## 12. Foreground Runtime
`MeshForegroundService` keeps BLE stack alive:
- Persistent notification.
- Partial wake lock for BLE reliability during doze.
- Starts advertiser, scanner, connection manager, gateway manager.

## 13. Periodic Worker
Every 15 minutes:
1. Message cleanup/eviction
2. Retry gateway publishes
3. Delete stale in-progress chunks (>10 minutes)
