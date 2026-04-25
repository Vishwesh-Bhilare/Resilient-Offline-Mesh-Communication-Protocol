import 'dart:async';
import 'dart:math';

import '../data/models/peer.dart';

class MeshRuntimeController {
  MeshRuntimeController();

  final Random _random = Random();
  final Map<String, Peer> _nearbyPeers = {};
  final Set<String> _knownDeviceIds = {};

  Timer? _presenceTimer;
  Timer? _relayTimer;
  bool _running = false;
  bool _internetAvailable = false;

  bool get isRunning => _running;
  int get connectedPeerCount => _nearbyPeers.length;
  Set<String> get knownDeviceIds => Set.unmodifiable(_knownDeviceIds);
  List<Peer> get peers => _nearbyPeers.values.toList(growable: false);

  void start({
    required String selfId,
    required bool internetAvailable,
    required void Function(Peer peer) onPeerSeen,
    required void Function(String peerId) onRelayTick,
    required void Function(Set<String> deviceIds) onGatewaySync,
  }) {
    if (_running) {
      return;
    }
    _running = true;
    _internetAvailable = internetAvailable;
    _knownDeviceIds.add(selfId);

    _presenceTimer = Timer.periodic(const Duration(seconds: 4), (_) {
      final peer = _discoverOrRefreshPeer();
      _evictStalePeers();
      _knownDeviceIds.add(peer.id);
      onPeerSeen(peer);
      if (_internetAvailable) {
        onGatewaySync(knownDeviceIds);
      }
    });

    _relayTimer = Timer.periodic(const Duration(seconds: 3), (_) {
      if (_nearbyPeers.isEmpty) {
        return;
      }
      final shuffledPeers = _nearbyPeers.keys.toList()..shuffle(_random);
      final relayCount = min(2, shuffledPeers.length);
      for (var i = 0; i < relayCount; i++) {
        onRelayTick(shuffledPeers[i]);
      }
    });
  }

  void stop() {
    _running = false;
    _presenceTimer?.cancel();
    _relayTimer?.cancel();
    _presenceTimer = null;
    _relayTimer = null;
  }

  void setInternetAvailable(bool available, void Function(Set<String>) onGatewaySync) {
    _internetAvailable = available;
    if (_internetAvailable && _running) {
      onGatewaySync(knownDeviceIds);
    }
  }

  Peer _discoverOrRefreshPeer() {
    if (_nearbyPeers.length < 5 && _random.nextDouble() > 0.35) {
      final id = 'node-${_random.nextInt(1 << 20).toRadixString(16)}';
      final peer = Peer(
        id: id,
        alias: 'peer-${id.substring(id.length - 4)}',
        lastSeen: DateTime.now(),
        latencyMs: 20 + _random.nextInt(120),
      );
      _nearbyPeers[id] = peer;
      return peer;
    }

    final targetId = _nearbyPeers.keys.elementAt(_random.nextInt(_nearbyPeers.length));
    final refreshed = _nearbyPeers[targetId]!.heartbeat(
      latencyMs: 20 + _random.nextInt(120),
    );
    _nearbyPeers[targetId] = refreshed;
    return refreshed;
  }

  void _evictStalePeers() {
    final now = DateTime.now();
    _nearbyPeers.removeWhere((_, peer) => now.difference(peer.lastSeen).inSeconds > 20);
  }
}
