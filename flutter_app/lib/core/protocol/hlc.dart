class HybridLogicalClock {
  HybridLogicalClock({required this.physicalTimeMs, required this.logicalCounter, required this.deviceId});

  final int physicalTimeMs;
  final int logicalCounter;
  final String deviceId;

  HybridLogicalClock tick(HybridLogicalClock? remote) {
    final now = DateTime.now().millisecondsSinceEpoch;
    final maxPhysical = [physicalTimeMs, remote?.physicalTimeMs ?? 0, now].reduce((a, b) => a > b ? a : b);

    final nextLogical = switch ((maxPhysical == physicalTimeMs, remote != null && maxPhysical == remote.physicalTimeMs)) {
      (true, true) => [logicalCounter, remote!.logicalCounter].reduce((a, b) => a > b ? a : b) + 1,
      (true, false) => logicalCounter + 1,
      (false, true) => remote!.logicalCounter + 1,
      _ => 0,
    };

    return HybridLogicalClock(
      physicalTimeMs: maxPhysical,
      logicalCounter: nextLogical,
      deviceId: deviceId,
    );
  }

  @override
  String toString() => '($physicalTimeMs,$logicalCounter,$deviceId)';
}
