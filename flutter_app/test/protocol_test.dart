import 'package:flutter_test/flutter_test.dart';
import 'package:resilient_offline_mesh/core/protocol/hlc.dart';

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
}
