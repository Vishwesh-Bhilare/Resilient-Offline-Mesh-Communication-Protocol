class Peer {
  Peer({required this.deviceId, required this.hasInternet, required this.lastSeen});

  final String deviceId;
  final bool hasInternet;
  final DateTime lastSeen;
}
