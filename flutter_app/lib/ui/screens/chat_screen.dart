import 'package:flutter/material.dart';

import '../../app/app_controller.dart';
import '../widgets/message_item.dart';

class ChatScreen extends StatefulWidget {
  const ChatScreen({
    super.key,
    required this.controller,
  });

  final AppController controller;

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final _composer = TextEditingController();
  final _channelController = TextEditingController(text: 'general');

  @override
  void dispose() {
    _composer.dispose();
    _channelController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final messages = widget.controller.messages;

    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _channelController,
                  decoration: const InputDecoration(
                    labelText: 'Channel',
                    prefixIcon: Icon(Icons.tag),
                  ),
                  onSubmitted: widget.controller.switchChannel,
                ),
              ),
              const SizedBox(width: 8),
              FilledButton.tonal(
                onPressed: () => widget.controller.switchChannel(_channelController.text),
                child: const Text('Switch'),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Expanded(
            child: messages.isEmpty
                ? const Center(child: Text('No messages yet for this channel.'))
                : ListView.separated(
                    reverse: true,
                    itemCount: messages.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 4),
                    itemBuilder: (context, index) {
                      final message = messages[index];
                      return MessageItem(
                        message: message,
                        localDeviceId: widget.controller.deviceId,
                      );
                    },
                  ),
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _composer,
                  minLines: 1,
                  maxLines: 4,
                  decoration: const InputDecoration(
                    hintText: 'Write a resilient message...',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              FilledButton.icon(
                onPressed: () {
                  widget.controller.sendMessage(_composer.text);
                  _composer.clear();
                },
                icon: const Icon(Icons.send),
                label: const Text('Send'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
