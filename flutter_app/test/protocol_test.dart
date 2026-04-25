import 'package:flutter_test/flutter_test.dart';
import 'package:resilient_offline_mesh/core/protocol/hlc.dart';

void main() {
  test('hlc tick advances physical time or logical counter', () {
    final base = HybridLogicalClock(
      physicalTimeMs: DateTime.now().millisecondsSinceEpoch,
      logicalCounter: 0,
      deviceId: 'node-a',
    );

    final next = base.tick(base);

    expect(next.deviceId, 'node-a');
    expect(next.physicalTimeMs >= base.physicalTimeMs, isTrue);
  });
}
