import 'package:flutter/material.dart';
import 'package:uuid/uuid.dart';

import 'data/repositories/message_repository.dart';
import 'services/gateway_manager.dart';
import 'services/mesh_runtime_controller.dart';
import 'ui/screens/chat_screen.dart';
import 'ui/screens/logs_screen.dart';

void main() {
  runApp(const MeshApp());
}

class MeshApp extends StatefulWidget {
  const MeshApp({super.key});

  @override
  State<MeshApp> createState() => _MeshAppState();
}

class _MeshAppState extends State<MeshApp> {
  final _messageRepository = MessageRepository();
  late final GatewayManager _gatewayManager;
  final _runtime = MeshRuntimeController();
  final _logs = <String>[];
  final _deviceId = const Uuid().v4().replaceAll('-', '');

  int _index = 0;
  bool _internetAvailable = false;

  @override
  void initState() {
    super.initState();
    _gatewayManager = GatewayManager(_messageRepository);
    _logs.add('Mesh runtime initialized.');
  }

  Future<void> _toggleInternet(bool enabled) async {
    setState(() => _internetAvailable = enabled);
    _logs.add('Internet ${enabled ? 'enabled' : 'disabled'} at ${DateTime.now()}');
    await _gatewayManager.publishPending(internetAvailable: enabled);
    if (mounted) setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final screens = [
      ChatScreen(repository: _messageRepository, deviceId: _deviceId),
      LogsScreen(logs: _logs),
    ];

    return MaterialApp(
      title: 'Resilient Offline Mesh (Flutter)',
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Resilient Offline Mesh'),
          actions: [
            Row(
              children: [
                Text(_internetAvailable ? 'Online' : 'Offline'),
                Switch(
                  value: _internetAvailable,
                  onChanged: _toggleInternet,
                ),
              ],
            ),
          ],
        ),
        body: screens[_index],
        bottomNavigationBar: NavigationBar(
          selectedIndex: _index,
          destinations: const [
            NavigationDestination(icon: Icon(Icons.chat_bubble_outline), label: 'Chat'),
            NavigationDestination(icon: Icon(Icons.list_alt_outlined), label: 'Logs'),
          ],
          onDestinationSelected: (index) => setState(() => _index = index),
        ),
        floatingActionButton: FloatingActionButton.small(
          onPressed: () {
            _logs.add('Connected peers: ${_runtime.connectedPeerCount}');
            setState(() {});
          },
          child: const Icon(Icons.hub_outlined),
        ),
      ),
    );
  }
}
