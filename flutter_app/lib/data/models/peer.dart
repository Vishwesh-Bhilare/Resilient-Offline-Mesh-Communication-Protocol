class Peer {
  const Peer({
    required this.id,
    required this.alias,
    required this.lastSeen,
    this.latencyMs = 0,
  });

  final String id;
  final String alias;
  final DateTime lastSeen;
  final int latencyMs;

  Peer heartbeat({int? latencyMs}) {
    return Peer(
      id: id,
      alias: alias,
      lastSeen: DateTime.now(),
      latencyMs: latencyMs ?? this.latencyMs,
    );
  }
}
