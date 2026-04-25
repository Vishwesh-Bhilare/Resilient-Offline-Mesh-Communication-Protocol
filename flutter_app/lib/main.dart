import 'package:flutter/material.dart';

import 'app/app_controller.dart';
import 'ui/screens/chat_screen.dart';
import 'ui/screens/logs_screen.dart';

void main() {
  runApp(const MeshFlutterApp());
}

class MeshFlutterApp extends StatefulWidget {
  const MeshFlutterApp({super.key});

  @override
  State<MeshFlutterApp> createState() => _MeshFlutterAppState();
}

class _MeshFlutterAppState extends State<MeshFlutterApp> {
  late final AppController _controller;
  int _tabIndex = 0;

  @override
  void initState() {
    super.initState();
    _controller = AppController()..addListener(_onAppChanged);
  }

  void _onAppChanged() {
    if (mounted) {
      setState(() {});
    }
  }

  @override
  void dispose() {
    _controller
      ..removeListener(_onAppChanged)
      ..dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Resilient Offline Mesh',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: Text('Mesh Node ${_controller.deviceId.substring(0, 6)}'),
          actions: [
            Row(
              children: [
                Text(_controller.internetEnabled ? 'Online' : 'Offline'),
                Switch(
                  value: _controller.internetEnabled,
                  onChanged: _controller.setInternetEnabled,
                ),
              ],
            ),
            const SizedBox(width: 6),
          ],
        ),
        body: IndexedStack(
          index: _tabIndex,
          children: [
            ChatScreen(controller: _controller),
            LogsScreen(controller: _controller),
          ],
        ),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _tabIndex,
          destinations: [
            NavigationDestination(
              icon: const Icon(Icons.forum_outlined),
              selectedIcon: const Icon(Icons.forum),
              label: 'Chat',
            ),
            NavigationDestination(
              icon: Badge(
                label: Text('${_controller.pendingCount}'),
                isLabelVisible: _controller.pendingCount > 0,
                child: const Icon(Icons.event_note_outlined),
              ),
              selectedIcon: const Icon(Icons.event_note),
              label: 'Logs',
            ),
          ],
          onDestinationSelected: (index) => setState(() => _tabIndex = index),
        ),
        floatingActionButton: FloatingActionButton.extended(
          onPressed: _controller.publishPending,
          icon: const Icon(Icons.cloud_upload_outlined),
          label: const Text('Flush pending'),
        ),
      ),
    );
  }
}
