import 'dart:async';

import '../data/repositories/message_repository.dart';

class GatewayManager {
  GatewayManager(this._repository);

  final MessageRepository _repository;

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
}
