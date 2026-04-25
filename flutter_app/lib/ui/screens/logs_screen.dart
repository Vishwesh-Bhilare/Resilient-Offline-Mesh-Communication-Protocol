import 'package:flutter/material.dart';

import '../../app/app_controller.dart';

class LogsScreen extends StatelessWidget {
  const LogsScreen({
    super.key,
    required this.controller,
  });

  final AppController controller;

  @override
  Widget build(BuildContext context) {
    final logs = controller.logs;
    final peers = controller.peers;

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text('Mesh peers (${peers.length})', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        if (peers.isEmpty)
          const Text('No discovered peers yet.')
        else
          ...peers.map(
            (peer) => ListTile(
              dense: true,
              contentPadding: EdgeInsets.zero,
              title: Text(peer.alias),
              subtitle: Text('id=${peer.id} · latency=${peer.latencyMs}ms'),
              trailing: Text(
                '${DateTime.now().difference(peer.lastSeen).inSeconds}s ago',
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ),
          ),
        const Divider(height: 24),
        Text('Runtime logs', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        if (logs.isEmpty)
          const Text('No logs yet.')
        else
          ...logs.map(
            (line) => Padding(
              padding: const EdgeInsets.only(bottom: 6),
              child: Text(line, style: Theme.of(context).textTheme.bodySmall),
            ),
          ),
      ],
    );
  }
}
