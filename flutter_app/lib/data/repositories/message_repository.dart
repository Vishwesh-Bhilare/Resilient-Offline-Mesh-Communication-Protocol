import '../models/message.dart';

class MessageRepository {
  final List<MeshMessage> _messages = [];

  List<MeshMessage> getAll() {
    _messages.removeWhere((m) => m.isExpired);
    _messages.sort((a, b) {
      if (a.hlc.physicalTimeMs != b.hlc.physicalTimeMs) {
        return b.hlc.physicalTimeMs.compareTo(a.hlc.physicalTimeMs);
      }
      return b.hlc.logicalCounter.compareTo(a.hlc.logicalCounter);
    });
    return List.unmodifiable(_messages);
  }

  bool insertIfMissing(MeshMessage message) {
    if (_messages.any((m) => m.id == message.id)) {
      return false;
    }
    _messages.add(message);
    return true;
  }

  void markPublished(String messageId) {
    final idx = _messages.indexWhere((m) => m.id == messageId);
    if (idx >= 0) {
      _messages[idx] = _messages[idx].copyWith(published: true);
    }
  }
}
