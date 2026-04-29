import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../app/app_controller.dart';
import '../widgets/message_item.dart';

class ChatScreen extends StatefulWidget {
  const ChatScreen({super.key});

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
    final controller = context.watch<AppController>();
    final messages = controller.messages;
    final meshEnabled = controller.meshEnabled;

    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          if (!meshEnabled)
            const Padding(
              padding: EdgeInsets.only(bottom: 8),
              child: Text('Enable mesh on Main tab to relay messages.'),
            ),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _channelController,
                  decoration: const InputDecoration(
                    labelText: 'Channel',
                    prefixIcon: Icon(Icons.tag),
                  ),
                  onSubmitted: controller.switchChannel,
                ),
              ),
              const SizedBox(width: 8),
              FilledButton.tonal(
                onPressed: () => controller.switchChannel(_channelController.text),
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
                        localDeviceId: controller.deviceId,
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
              Tooltip(
                message: meshEnabled ? '' : 'Enable mesh on the Main tab first',
                child: FilledButton.icon(
                  onPressed: meshEnabled
                      ? () {
                          controller.sendMessage(_composer.text);
                          _composer.clear();
                        }
                      : null,
                  icon: const Icon(Icons.send),
                  label: const Text('Send'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
