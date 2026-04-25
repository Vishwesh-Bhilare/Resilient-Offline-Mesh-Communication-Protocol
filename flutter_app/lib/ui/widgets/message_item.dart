import 'package:flutter/material.dart';

import '../../data/models/message.dart';

class MessageItem extends StatelessWidget {
  const MessageItem({super.key, required this.message});

  final MeshMessage message;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text(message.content),
        subtitle: Text('Sender: ${message.sender.substring(0, 8)} • HLC ${message.hlc}'),
        trailing: Icon(
          message.published ? Icons.cloud_done : Icons.cloud_off,
          color: message.published ? Colors.green : Colors.orange,
        ),
      ),
    );
  }
}
