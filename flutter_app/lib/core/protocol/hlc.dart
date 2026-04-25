class HybridLogicalClock {
  const HybridLogicalClock({
    required this.physicalTimeMs,
    required this.logicalCounter,
    required this.deviceId,
  });

  final int physicalTimeMs;
  final int logicalCounter;
  final String deviceId;

  static HybridLogicalClock seed(String deviceId) {
    return HybridLogicalClock(
      physicalTimeMs: DateTime.now().millisecondsSinceEpoch,
      logicalCounter: 0,
      deviceId: deviceId,
    );
  }

  HybridLogicalClock tick() {
    final now = DateTime.now().millisecondsSinceEpoch;
    if (now > physicalTimeMs) {
      return HybridLogicalClock(
        physicalTimeMs: now,
        logicalCounter: 0,
        deviceId: deviceId,
      );
    }

    return HybridLogicalClock(
      physicalTimeMs: physicalTimeMs,
      logicalCounter: logicalCounter + 1,
      deviceId: deviceId,
    );
  }

  HybridLogicalClock merge(HybridLogicalClock remote) {
    final now = DateTime.now().millisecondsSinceEpoch;
    final maxPhysical = [now, physicalTimeMs, remote.physicalTimeMs].reduce(
      (left, right) => left > right ? left : right,
    );

    final localAtMax = maxPhysical == physicalTimeMs;
    final remoteAtMax = maxPhysical == remote.physicalTimeMs;

    final nextLogical = switch ((localAtMax, remoteAtMax)) {
      (true, true) => (logicalCounter > remote.logicalCounter
              ? logicalCounter
              : remote.logicalCounter) +
          1,
      (true, false) => logicalCounter + 1,
      (false, true) => remote.logicalCounter + 1,
      (false, false) => 0,
    };

    return HybridLogicalClock(
      physicalTimeMs: maxPhysical,
      logicalCounter: nextLogical,
      deviceId: deviceId,
    );
  }

  int compareTo(HybridLogicalClock other) {
    if (physicalTimeMs != other.physicalTimeMs) {
      return physicalTimeMs.compareTo(other.physicalTimeMs);
    }
    if (logicalCounter != other.logicalCounter) {
      return logicalCounter.compareTo(other.logicalCounter);
    }
    return deviceId.compareTo(other.deviceId);
  }

  String compact() => '$physicalTimeMs:$logicalCounter:$deviceId';
}
