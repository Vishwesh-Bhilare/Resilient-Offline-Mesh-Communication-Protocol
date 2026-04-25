import '../data/models/peer.dart';

class MeshRuntimeController {
  final List<Peer> _peers = [];

  List<Peer> get peers => List.unmodifiable(_peers);

  void registerPeer(Peer peer) {
    _peers.removeWhere((existing) => existing.deviceId == peer.deviceId);
    _peers.add(peer);
  }

  int get connectedPeerCount => _peers.length;
}
