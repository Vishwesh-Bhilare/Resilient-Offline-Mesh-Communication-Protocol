import 'package:flutter_test/flutter_test.dart';
import 'package:resilient_offline_mesh/core/protocol/hlc.dart';
import 'package:resilient_offline_mesh/core/security/signature_util.dart';
import 'package:resilient_offline_mesh/data/models/message.dart';
import 'package:resilient_offline_mesh/data/models/peer.dart';
import 'package:resilient_offline_mesh/data/repositories/message_repository.dart';
import 'package:resilient_offline_mesh/services/mesh_runtime_controller.dart';

void main() {
  test('tick increases logical counter when physical time does not advance', () {
    final clock = HybridLogicalClock(
      physicalTimeMs: DateTime.now().millisecondsSinceEpoch,
      logicalCounter: 4,
      deviceId: 'node-a',
    );

    final next = clock.tick();

    expect(next.deviceId, equals('node-a'));
    expect(next.physicalTimeMs, greaterThanOrEqualTo(clock.physicalTimeMs));
    if (next.physicalTimeMs == clock.physicalTimeMs) {
      expect(next.logicalCounter, equals(clock.logicalCounter + 1));
    }
  });

  test('merge includes remote counter when remote is ahead', () {
    final local = HybridLogicalClock(
      physicalTimeMs: 100,
      logicalCounter: 1,
      deviceId: 'node-a',
    );
    final remote = HybridLogicalClock(
      physicalTimeMs: 120,
      logicalCounter: 7,
      deviceId: 'node-b',
    );

    final merged = local.merge(remote);

    expect(merged.physicalTimeMs, greaterThanOrEqualTo(120));
    expect(merged.logicalCounter, greaterThanOrEqualTo(8));
    expect(merged.deviceId, equals('node-a'));
  });

  test('merge false-false branch starts logical counter at 1', () {
    final now = DateTime.now().millisecondsSinceEpoch;
    final local = HybridLogicalClock(
      physicalTimeMs: now - 1000,
      logicalCounter: 5,
      deviceId: 'node-a',
    );
    final remote = HybridLogicalClock(
      physicalTimeMs: now - 500,
      logicalCounter: 9,
      deviceId: 'node-b',
    );

    final merged = local.merge(remote);

    expect(merged.physicalTimeMs, greaterThan(now - 1));
    expect(merged.logicalCounter, equals(1));
  });

  test('signature verify round-trips with shared symmetric key', () {
    const payload = 'hello mesh';
    const senderDeviceId = 'sender-device-id';

    final signature = SignatureUtil.sign(
      payload: payload,
      privateKey: senderDeviceId,
    );

    expect(
      SignatureUtil.verify(
        payload: payload,
        signature: signature,
        sharedKey: senderDeviceId,
      ),
      isTrue,
    );
    expect(
      SignatureUtil.verify(
        payload: payload,
        signature: signature,
        sharedKey: 'wrong-key',
      ),
      isFalse,
    );
  });

  test('repository update persists hop progression for relayed messages', () {
    final repo = MessageRepository();
    final message = MeshMessage(
      id: 'm-1',
      channelId: 'general',
      senderId: 'node-a',
      body: 'ping',
      createdAt: DateTime.now(),
      hlc: HybridLogicalClock.seed('node-a'),
      signature: SignatureUtil.sign(payload: 'payload', privateKey: 'node-a'),
    );

    expect(repo.addIfMissing(message), isTrue);
    final hopped = message.hopTo('peer-1');
    repo.update(hopped);

    final stored = repo.list(channelId: 'general').first;
    expect(stored.hops, equals(1));
    expect(stored.deliveredTo.contains('peer-1'), isTrue);
  });

  test('repository list does not mutate backing list order', () {
    final repo = MessageRepository();

    final older = MeshMessage(
      id: 'm-old',
      channelId: 'general',
      senderId: 'node-a',
      body: 'old',
      createdAt: DateTime.now(),
      hlc: HybridLogicalClock(
        physicalTimeMs: 100,
        logicalCounter: 0,
        deviceId: 'a',
      ),
      signature: 's1',
    );
    final newer = MeshMessage(
      id: 'm-new',
      channelId: 'general',
      senderId: 'node-a',
      body: 'new',
      createdAt: DateTime.now(),
      hlc: HybridLogicalClock(
        physicalTimeMs: 200,
        logicalCounter: 0,
        deviceId: 'a',
      ),
      signature: 's2',
    );

    repo.addIfMissing(older);
    repo.addIfMissing(newer);

    final listed = repo.list();
    expect(listed.map((m) => m.id).toList(), equals(['m-new', 'm-old']));

    final pending = repo.pending();
    expect(pending.map((m) => m.id).toList(), equals(['m-old', 'm-new']));
  });

  test('repository list returns immutable copy sorted by hlc', () {
    final repo = MessageRepository();
    repo.addIfMissing(
      MeshMessage(
        id: 'x',
        channelId: 'c',
        senderId: 's',
        body: 'x',
        createdAt: DateTime.now(),
        hlc: HybridLogicalClock(physicalTimeMs: 10, logicalCounter: 0, deviceId: 'd'),
        signature: 'sx',
      ),
    );
    repo.addIfMissing(
      MeshMessage(
        id: 'y',
        channelId: 'c',
        senderId: 's',
        body: 'y',
        createdAt: DateTime.now(),
        hlc: HybridLogicalClock(physicalTimeMs: 20, logicalCounter: 0, deviceId: 'd'),
        signature: 'sy',
      ),
    );

    final listed = repo.list(channelId: 'c');
    expect(listed.first.id, equals('y'));
    expect(() => listed.add(listed.first), throwsUnsupportedError);
  });

  test('peers getter returns typed peer list sorted by lastSeen desc', () {
    final runtime = MeshRuntimeController();
    runtime.debugInjectPeer(
      Peer(
        id: 'old',
        alias: 'old',
        lastSeen: DateTime.now().subtract(const Duration(seconds: 5)),
      ),
    );
    runtime.debugInjectPeer(
      Peer(id: 'new', alias: 'new', lastSeen: DateTime.now()),
    );

    final peers = runtime.peers;

    expect(peers, isA<List<Peer>>());
    expect(peers.first.id, equals('new'));
  });
}
