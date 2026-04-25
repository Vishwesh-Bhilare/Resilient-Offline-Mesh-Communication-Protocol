import 'dart:async';

import '../data/repositories/message_repository.dart';

class GatewayManager {
  GatewayManager(this._repository);

  final MessageRepository _repository;
  final Set<String> _publishedPresence = {};

  Future<int> publishPending({
    required bool internetAvailable,
    void Function(String messageId)? onPublished,
  }) async {
    if (!internetAvailable) {
      return 0;
    }

    final pending = _repository.pending();
    for (final message in pending) {
      await Future<void>.delayed(const Duration(milliseconds: 45));
      _repository.markPublished(message.id);
      onPublished?.call(message.id);
    }
    return pending.length;
  }

  Future<int> publishPresence({
    required bool internetAvailable,
    required Set<String> deviceIds,
  }) async {
    if (!internetAvailable || deviceIds.isEmpty) {
      return 0;
    }
    var published = 0;
    for (final id in deviceIds) {
      if (_publishedPresence.contains(id)) {
        continue;
      }
      await Future<void>.delayed(const Duration(milliseconds: 15));
      _publishedPresence.add(id);
      published++;
    }
    return published;
  }
}
