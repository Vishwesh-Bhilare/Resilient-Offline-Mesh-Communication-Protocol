import '../../core/protocol/hlc.dart';

class MeshMessage {
  const MeshMessage({
    required this.id,
    required this.channelId,
    required this.senderId,
    required this.body,
    required this.createdAt,
    required this.hlc,
    required this.signature,
    this.hops = 0,
    this.ttlSeconds = 86400,
    this.published = false,
    this.deliveredTo = const {},
  });

  final String id;
  final String channelId;
  final String senderId;
  final String body;
  final DateTime createdAt;
  final HybridLogicalClock hlc;
  final String signature;
  final int hops;
  final int ttlSeconds;
  final bool published;
  final Set<String> deliveredTo;

  bool get isExpired => DateTime.now().isAfter(createdAt.add(Duration(seconds: ttlSeconds)));

  MeshMessage hopTo(String peerId) {
    return copyWith(
      hops: hops + 1,
      deliveredTo: {...deliveredTo, peerId},
    );
  }

  MeshMessage copyWith({
    int? hops,
    bool? published,
    Set<String>? deliveredTo,
  }) {
    return MeshMessage(
      id: id,
      channelId: channelId,
      senderId: senderId,
      body: body,
      createdAt: createdAt,
      hlc: hlc,
      signature: signature,
      hops: hops ?? this.hops,
      ttlSeconds: ttlSeconds,
      published: published ?? this.published,
      deliveredTo: deliveredTo ?? this.deliveredTo,
    );
  }
}
