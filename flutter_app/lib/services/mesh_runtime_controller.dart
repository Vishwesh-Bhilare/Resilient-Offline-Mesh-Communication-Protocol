class MeshRuntimeController {
  int _connectedPeerCount = 0;

  int get connectedPeerCount => _connectedPeerCount;

  void setConnectedPeerCount(int value) {
    _connectedPeerCount = value;
  }
}
