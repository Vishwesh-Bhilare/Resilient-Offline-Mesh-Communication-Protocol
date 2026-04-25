import 'package:flutter/material.dart';

import 'app/app_controller.dart';
import 'ui/screens/chat_screen.dart';
import 'ui/screens/main_screen.dart';

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
          title: const Text('Resilient Offline Mesh'),
        ),
        body: IndexedStack(
          index: _tabIndex,
          children: [
            MainScreen(controller: _controller),
            ChatScreen(controller: _controller),
          ],
        ),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _tabIndex,
          destinations: const [
            NavigationDestination(
              icon: Icon(Icons.hub_outlined),
              selectedIcon: Icon(Icons.hub),
              label: 'Main',
            ),
            NavigationDestination(
              icon: Icon(Icons.forum_outlined),
              selectedIcon: Icon(Icons.forum),
              label: 'Chat room',
            ),
          ],
          onDestinationSelected: (index) => setState(() => _tabIndex = index),
        ),
      ),
    );
  }
}
