import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../app/app_controller.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  bool _toggling = false;

  @override
  Widget build(BuildContext context) {
    final controller = context.watch<AppController>();
    final isOn = controller.meshEnabled;
    final peers = controller.peers;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            'Device ${controller.deviceId.substring(0, 8)}',
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 20),
          SizedBox(
            width: double.infinity,
            height: 180,
            child: FilledButton(
              style: FilledButton.styleFrom(
                backgroundColor: isOn
                    ? Theme.of(context).colorScheme.primary
                    : Theme.of(context).colorScheme.outline,
                textStyle: Theme.of(context).textTheme.headlineSmall,
              ),
              onPressed: _toggling
                  ? null
                  : () async {
                      setState(() => _toggling = true);
                      try {
                        await controller.setMeshEnabled(!isOn);
                      } finally {
                        if (mounted) {
                          setState(() => _toggling = false);
                        }
                      }
                    },
              child: _toggling
                  ? const CircularProgressIndicator(color: Colors.white)
                  : Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          isOn ? Icons.power_settings_new : Icons.power_off,
                          size: 48,
                        ),
                        const SizedBox(height: 12),
                        Text(isOn ? 'MESH ON' : 'MESH OFF'),
                      ],
                    ),
            ),
          ),
          const SizedBox(height: 24),
          Card(
            child: ListTile(
              title: const Text('Internet uplink'),
              subtitle: Text(controller.internetEnabled ? 'Connected' : 'Disconnected'),
              trailing: Switch(
                value: controller.internetEnabled,
                onChanged: controller.setInternetEnabled,
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text('Nearby peers: ${peers.length}'),
          Text('Known mesh IDs: ${controller.knownDeviceCount}'),
          Text('Presence IDs published: ${controller.presenceSyncCount}'),
        ],
      ),
    );
  }
}
