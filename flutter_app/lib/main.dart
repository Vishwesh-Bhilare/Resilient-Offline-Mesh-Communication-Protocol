import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'app/app_controller.dart';
import 'ui/screens/chat_screen.dart';
import 'ui/screens/logs_screen.dart';
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
  int _tabIndex = 0;

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => AppController(),
      child: MaterialApp(
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
            children: const [
              MainScreen(),
              ChatScreen(),
              LogsScreen(),
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
              NavigationDestination(
                icon: Icon(Icons.receipt_long_outlined),
                selectedIcon: Icon(Icons.receipt_long),
                label: 'Logs',
              ),
            ],
            onDestinationSelected: (index) => setState(() => _tabIndex = index),
          ),
        ),
      ),
    );
  }
}
