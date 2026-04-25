import '../data/repositories/message_repository.dart';

class GatewayManager {
  GatewayManager(this._messageRepository);

  final MessageRepository _messageRepository;

  Future<void> publishPending({required bool internetAvailable}) async {
    if (!internetAvailable) return;

    for (final message in _messageRepository.getAll().where((m) => !m.published)) {
      await Future<void>.delayed(const Duration(milliseconds: 40));
      _messageRepository.markPublished(message.id);
    }
  }
}
