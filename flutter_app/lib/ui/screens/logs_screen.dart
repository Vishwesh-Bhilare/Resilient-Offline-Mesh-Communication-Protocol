import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../app/app_controller.dart';

class LogsScreen extends StatelessWidget {
  const LogsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = context.watch<AppController>();
    final logs = controller.logs;
    final peers = controller.peers;

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Row(
            children: [
              Expanded(
                child: Text(
                  'Mesh peers (${peers.length})',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ),
              TextButton.icon(
                onPressed: controller.clearLogs,
                icon: const Icon(Icons.delete_sweep),
                label: const Text('Clear logs'),
              ),
            ],
          ),
        ),
        Expanded(
          child: ListView(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            children: [
              if (peers.isEmpty)
                const Text('No discovered peers yet.')
              else
                ...peers.map(
                  (peer) {
                    final ageMs = DateTime.now().difference(peer.lastSeen).inMilliseconds;
                    return ListTile(
                      dense: true,
                      contentPadding: EdgeInsets.zero,
                      title: Text(peer.alias),
                      subtitle: Text('id=${peer.id} · latency=${peer.latencyMs}ms'),
                      trailing: Text(
                        '${(ageMs / 1000).toStringAsFixed(1)}s ago',
                        style: Theme.of(context).textTheme.bodySmall,
                      ),
                    );
                  },
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
              const SizedBox(height: 12),
            ],
          ),
        ),
      ],
    );
  }
}
