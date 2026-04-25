import '../models/message.dart';

class MessageRepository {
  final List<MeshMessage> _messages = [];

  List<MeshMessage> list({String? channelId}) {
    _messages.removeWhere((message) => message.isExpired);
    final selected = channelId == null
        ? _messages
        : _messages.where((message) => message.channelId == channelId).toList();
    selected.sort((a, b) => b.hlc.compareTo(a.hlc));
    return List.unmodifiable(selected);
  }

  bool addIfMissing(MeshMessage message) {
    final alreadyExists = _messages.any((item) => item.id == message.id);
    if (alreadyExists) {
      return false;
    }
    _messages.add(message);
    return true;
  }

  int pendingCount() {
    _messages.removeWhere((message) => message.isExpired);
    return _messages.where((message) => !message.published).length;
  }

  List<MeshMessage> pending() {
    _messages.removeWhere((message) => message.isExpired);
    return _messages.where((message) => !message.published).toList(growable: false);
  }

  void markPublished(String id) {
    final index = _messages.indexWhere((message) => message.id == id);
    if (index < 0) {
      return;
    }
    _messages[index] = _messages[index].copyWith(published: true);
  }

  void update(MeshMessage message) {
    final index = _messages.indexWhere((item) => item.id == message.id);
    if (index < 0) {
      _messages.add(message);
      return;
    }
    _messages[index] = message;
  }
}
