# Resilient Offline Mesh Communication Protocol

**BLE-based DTN with opportunistic gateway uplink**

## 1) Core idea

A decentralized Android communication system where nearby devices form an opportunistic BLE mesh and propagate messages without requiring internet access.

Each device can play two roles:

- **Node**: stores, verifies, and relays messages.
- **Gateway**: publishes messages to a backend when internet becomes available.

The system uses epidemic/gossip propagation with controls for duplication, ordering, abuse, and partial failures.

## 2) System model

This protocol is:

- a distributed system,
- a delay-tolerant network (DTN), and
- eventually consistent.

Guarantees:

- **Eventual delivery** is probabilistic.
- No real-time delivery guarantees.
- No global total ordering.

## 3) Identity and security

### Device identity

On first launch:

1. Generate an **Ed25519 keypair**.
2. Derive `device_id = sha256(public_key)`.

### Public key distribution

Each message includes the sender public key, because there is no external PKI:

```json
{
  "public_key": "ed25519_pubkey"
}
```

### Message authentication

Each message is signed:

`signature = sign(id + content, private_key)`

Effects:

- Spoofing resistance.
- Integrity protection.

## 4) Message schema (final)

```json
{
  "id": "sha256(sender + timestamp + content)",
  "sender": "sha256(public_key)",
  "public_key": "ed25519_pubkey",
  "timestamp": 1710000000,
  "hlc": "(physical_ms, logical_counter, device_id)",
  "ttl": 21600,
  "content": "text",
  "hops": 0,
  "signature": "ed25519_signature",
  "size": 142,
  "priority": 0,
  "channel_id": "optional_scope"
}
```

## 5) Ordering model

Use **Hybrid Logical Clocks (HLC)** only:

`hlc = (physical_time_ms, logical_counter, device_id)`

Rationale:

- Preserves causality.
- Maintains intuitive recency behavior.
- Reduces clock-drift ambiguity.

Uses:

- UI ordering.
- Sync prioritization (higher HLC first).

## 6) BLE discovery

Advertising payload structure:

- `[device_id_prefix:4B]`
- `[flags:1B]`
- `[bloom_filter:20B]`
- `[has_internet:1B]`
- `[protocol_version:1B]`

Bloom filter design target:

- ~100 message identifiers.
- ~1% false-positive rate.
- Reduces explicit ID exchange overhead.

## 7) Sync protocol lifecycle

### Phase 0 — Connection setup

Immediately after connect:

- Request MTU increase via `requestMtu(512)`.

### Phase 1 — HELLO

Exchange:

- `device_id`
- `protocol_version`
- capabilities
- Bloom filter

### Phase 2 — DIFF

Estimate missing messages.

### Phase 3 — REQUEST

Request specific message IDs.

### Phase 4 — TRANSFER

Send chunked message payloads.

### Phase 5 — CLOSE

Disconnect immediately.

## 8) Transport layer

Chunk format:

`[msg_id][chunk_index][total_chunks][data]`

Reliability mechanisms:

- ACK per message.
- Retry missing chunks.
- Timeout handling.

### Partial transfer handling

Persist incomplete transfers in an `in_progress` table:

- `(message_id, received_chunks, total_chunks, timestamp)`

Rules:

- Assemble message only when all chunks are present.
- Discard incomplete transfers after timeout.
- Run periodic cleanup.

## 9) Anti-flooding controls

Enforcement points:

1. On receive: per-sender rate limit.
2. Per session: max messages per sync session.
3. Global ingest cap per time window.

Identity rotation concern (Sybil via key regeneration) is mitigated with:

- session-level caps,
- storage limits.

## 10) Storage and eviction

Tables:

- `messages`
- `in_progress`
- `peer_sync`

Eviction priority:

1. Published and old.
2. Expired.
3. Oldest remaining.

Limits:

- max DB size (e.g., 50 MB),
- strict TTL enforcement.

## 11) Sync optimization

Track per-peer sync metadata:

`(peer_id, last_sync_time, sync_hash)`

Skip sync when:

- sync was recent, and
- no new content is indicated.

## 12) Gateway publishing

Rules:

- Publish immediately when internet is available.
- Mark `published=true` only on successful publish.

Server requirement:

- `message_id` must be unique/idempotent key,
- duplicate submissions are safe.

## 13) Delivery visibility

Explicit delivery ACK protocol is optional.

Lightweight mechanism:

- If a sender later sees its own message represented in peer Bloom filters, infer likely propagation/delivery.

## 14) Channel / namespace

Add `channel_id` for scoped propagation:

- campus,
- event,
- region,
- etc.

This avoids global-noise scaling issues.

## 15) Android execution model

Requirements:

- Foreground service.
- WorkManager scheduling.
- Doze-aware behavior.

Permissions:

- `BLUETOOTH_SCAN`
- `BLUETOOTH_CONNECT`
- `BLUETOOTH_ADVERTISE`

Recommended BLE operation:

- low-power scan mode,
- duty cycling.

## 16) Protocol versioning

Include `protocol_version` in discovery/handshake to support backward compatibility and safer upgrades.

## 17) Threat model summary

| Threat | Status | Primary control |
|---|---|---|
| Spoofing | Solved | Signatures + sender key verification |
| Flooding | Mitigated | Multi-layer rate limits/caps |
| Sybil | Partial | Session limits + storage constraints |
| Replay | Controlled | TTL enforcement |

## 18) Optional upgrade path

Future optimization:

- BLE for discovery/control plane.
- Wi-Fi Direct for bulk transfer.

## 19) Core risks

1. Synchronization correctness — **partially addressed**.
2. Android background restrictions — **high risk**.
3. Battery constraints — **manageable**.
4. Abuse resistance — **partially addressed**.

## 20) Final summary

This protocol defines a secure, decentralized, BLE-based opportunistic mesh with:

- cryptographic identity,
- probabilistic discovery (Bloom filters),
- HLC-based ordering,
- chunked reliable transfer,
- anti-flooding safeguards,
- partial-transfer recovery,
- channel-based scoping,
- opportunistic internet gateways.

Key ambiguities resolved in this version:

- ordering model,
- public key distribution,
- partial transfer behavior,
- unbounded storage growth,
- protocol upgrade compatibility.
