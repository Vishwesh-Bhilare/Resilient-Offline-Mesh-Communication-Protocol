import '../../core/protocol/hlc.dart';

class MeshMessage {
  MeshMessage({
    required this.id,
    required this.sender,
    required this.publicKey,
    required this.timestamp,
    required this.hlc,
    required this.ttlSeconds,
    required this.content,
    required this.signature,
    this.hops = 0,
    this.priority = 0,
    this.channelId,
    this.published = false,
  });

  final String id;
  final String sender;
  final String publicKey;
  final DateTime timestamp;
  final HybridLogicalClock hlc;
  final int ttlSeconds;
  final String content;
  final String signature;
  final int hops;
  final int priority;
  final String? channelId;
  final bool published;

  int get size => content.length;

  bool get isExpired => DateTime.now().isAfter(timestamp.add(Duration(seconds: ttlSeconds)));

  MeshMessage copyWith({int? hops, bool? published}) {
    return MeshMessage(
      id: id,
      sender: sender,
      publicKey: publicKey,
      timestamp: timestamp,
      hlc: hlc,
      ttlSeconds: ttlSeconds,
      content: content,
      signature: signature,
      hops: hops ?? this.hops,
      priority: priority,
      channelId: channelId,
      published: published ?? this.published,
    );
  }
}
