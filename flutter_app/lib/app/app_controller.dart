import 'dart:async';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:uuid/uuid.dart';

import '../core/protocol/hlc.dart';
import '../core/security/signature_util.dart';
import '../data/models/message.dart';
import '../data/models/peer.dart';
import '../data/repositories/message_repository.dart';
import '../services/gateway_manager.dart';
import '../services/mesh_runtime_controller.dart';

class AppController extends ChangeNotifier {
  AppController({
    MessageRepository? repository,
    MeshRuntimeController? runtime,
    GatewayManager? gateway,
    Uuid? uuid,
  })  : _repository = repository ?? MessageRepository(),
        _runtime = runtime ?? MeshRuntimeController(),
        _uuid = uuid ?? const Uuid(),
        _deviceId = (uuid ?? const Uuid()).v4().replaceAll('-', '') {
    _clock = HybridLogicalClock.seed(_deviceId);
    _gateway = gateway ?? GatewayManager(_repository);
    _logs.add('[$_nowIso] App controller initialized for $_deviceId');
    _startPeerHeartbeat();
  }

  final MessageRepository _repository;
  final MeshRuntimeController _runtime;
  final Uuid _uuid;
  final String _deviceId;
  final List<String> _logs = [];
  final List<Peer> _peers = [];
  late final GatewayManager _gateway;
  late HybridLogicalClock _clock;

  Timer? _peerTimer;
  bool _internetEnabled = false;
  String _activeChannelId = 'general';

  String get deviceId => _deviceId;
  bool get internetEnabled => _internetEnabled;
  String get activeChannelId => _activeChannelId;
  List<String> get logs => List.unmodifiable(_logs.reversed);
  List<Peer> get peers => List.unmodifiable(_peers);
  List<MeshMessage> get messages => _repository.list(channelId: _activeChannelId);
  int get pendingCount => _repository.pendingCount();

  static String get _nowIso => DateTime.now().toIso8601String();

  void switchChannel(String channelId) {
    if (channelId.trim().isEmpty || channelId == _activeChannelId) {
      return;
    }
    _activeChannelId = channelId;
    _logs.add('[$_nowIso] Switched to channel "$channelId"');
    notifyListeners();
  }

  void sendMessage(String body) {
    final normalized = body.trim();
    if (normalized.isEmpty) {
      return;
    }

    _clock = _clock.tick();
    final payload = '${_clock.compact()}::$normalized';
    final message = MeshMessage(
      id: _uuid.v4(),
      channelId: _activeChannelId,
      senderId: _deviceId,
      body: normalized,
      createdAt: DateTime.now(),
      hlc: _clock,
      signature: SignatureUtil.sign(payload: payload, privateKey: _deviceId),
    );

    final inserted = _repository.addIfMissing(message);
    if (inserted) {
      _logs.add('[$_nowIso] Queued message ${message.id.substring(0, 8)} (${message.body.length} chars)');
    }
    if (_internetEnabled) {
      publishPending();
    }
    notifyListeners();
  }

  Future<void> setInternetEnabled(bool enabled) async {
    _internetEnabled = enabled;
    _logs.add('[$_nowIso] Internet ${enabled ? 'enabled' : 'disabled'}');
    if (enabled) {
      await publishPending();
    }
    notifyListeners();
  }

  Future<void> publishPending() async {
    final published = await _gateway.publishPending(
      internetAvailable: _internetEnabled,
      onPublished: (messageId) {
        _logs.add('[$_nowIso] Published $messageId to HTTP gateway');
      },
    );
    if (published == 0 && _internetEnabled) {
      _logs.add('[$_nowIso] No pending messages to publish');
    }
    notifyListeners();
  }

  void _startPeerHeartbeat() {
    _peerTimer = Timer.periodic(const Duration(seconds: 5), (_) {
      final random = Random();
      if (_peers.length < 4 && random.nextBool()) {
        final id = _uuid.v4().split('-').first;
        _peers.add(
          Peer(
            id: id,
            alias: 'node-${id.substring(0, 4)}',
            lastSeen: DateTime.now(),
            latencyMs: 20 + random.nextInt(150),
          ),
        );
      } else if (_peers.isNotEmpty) {
        final index = random.nextInt(_peers.length);
        _peers[index] = _peers[index].heartbeat(latencyMs: 20 + random.nextInt(150));
      }
      _runtime.setConnectedPeerCount(_peers.length);
      _logs.add('[$_nowIso] Peer heartbeat: ${_runtime.connectedPeerCount} peers reachable');
      notifyListeners();
    });
  }

  @override
  void dispose() {
    _peerTimer?.cancel();
    super.dispose();
  }
}
