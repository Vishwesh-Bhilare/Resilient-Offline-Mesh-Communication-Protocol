import 'package:flutter/material.dart';

class LogsScreen extends StatelessWidget {
  const LogsScreen({super.key, required this.logs});

  final List<String> logs;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: logs.length,
      itemBuilder: (context, index) {
        return ListTile(
          dense: true,
          leading: const Icon(Icons.article_outlined),
          title: Text(logs[index]),
        );
      },
    );
  }
}
