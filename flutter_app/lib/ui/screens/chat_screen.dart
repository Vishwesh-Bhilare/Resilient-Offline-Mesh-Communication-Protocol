import 'package:flutter/material.dart';

import '../../core/protocol/hlc.dart';
import '../../core/security/signature_util.dart';
import '../../data/models/message.dart';
import '../../data/repositories/message_repository.dart';
import '../widgets/message_item.dart';

class ChatScreen extends StatefulWidget {
  const ChatScreen({super.key, required this.repository, required this.deviceId});

  final MessageRepository repository;
  final String deviceId;

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final _controller = TextEditingController();
  late HybridLogicalClock _clock;

  @override
  void initState() {
    super.initState();
    _clock = HybridLogicalClock(
      physicalTimeMs: DateTime.now().millisecondsSinceEpoch,
      logicalCounter: 0,
      deviceId: widget.deviceId,
    );
  }

  void _sendMessage() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;

    _clock = _clock.tick(null);
    final timestamp = DateTime.now();
    final publicKey = SignatureUtil.sha256Hex(widget.deviceId);
    final id = SignatureUtil.sha256Hex('${widget.deviceId}|${timestamp.millisecondsSinceEpoch}|$text');
    final signature = SignatureUtil.sign(id: id, content: text, privateKey: widget.deviceId);

    widget.repository.insertIfMissing(
      MeshMessage(
        id: id,
        sender: widget.deviceId,
        publicKey: publicKey,
        timestamp: timestamp,
        hlc: _clock,
        ttlSeconds: 60 * 60 * 6,
        content: text,
        signature: signature,
      ),
    );

    _controller.clear();
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final messages = widget.repository.getAll();

    return Column(
      children: [
        Expanded(
          child: messages.isEmpty
              ? const Center(child: Text('No messages yet.'))
              : ListView.builder(
                  reverse: true,
                  itemCount: messages.length,
                  itemBuilder: (context, index) => MessageItem(message: messages[index]),
                ),
        ),
        SafeArea(
          top: false,
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(hintText: 'Write a mesh message'),
                    onSubmitted: (_) => _sendMessage(),
                  ),
                ),
                const SizedBox(width: 8),
                FilledButton(onPressed: _sendMessage, child: const Text('Send')),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
