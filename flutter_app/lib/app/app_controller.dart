import 'dart:async';

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
  }

  final MessageRepository _repository;
  final MeshRuntimeController _runtime;
  final Uuid _uuid;
  final String _deviceId;
  final List<String> _logs = [];
  late final GatewayManager _gateway;
  late HybridLogicalClock _clock;

  int _presenceSyncCount = 0;
  bool _meshEnabled = false;
  bool _internetEnabled = false;
  bool _disposed = false;
  String _activeChannelId = 'general';

  String get deviceId => _deviceId;
  bool get meshEnabled => _meshEnabled;
  bool get internetEnabled => _internetEnabled;
  String get activeChannelId => _activeChannelId;
  int get presenceSyncCount => _presenceSyncCount;
  int get knownDeviceCount => _runtime.knownDeviceIds.length;
  List<String> get logs => List.unmodifiable(_logs.reversed);
  List<Peer> get peers => _runtime.peers;
  List<MeshMessage> get messages => _repository.list(channelId: _activeChannelId);
  int get pendingCount => _repository.pendingCount();

  static String get _nowIso => DateTime.now().toIso8601String();

  void switchChannel(String channelId) {
    if (channelId.trim().isEmpty || channelId == _activeChannelId) {
      return;
    }
    _activeChannelId = channelId;
    _logs.add('[$_nowIso] Switched to channel "$channelId"');
    _safeNotify();
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
    if (_meshEnabled) {
      _relayMessagesToPeers(maxMessagesPerPeer: 1);
    }
    if (_internetEnabled && _meshEnabled) {
      publishPending();
    }
    _safeNotify();
  }

  Future<void> setMeshEnabled(bool enabled) async {
    if (_meshEnabled == enabled) {
      return;
    }
    _meshEnabled = enabled;
    _logs.add('[$_nowIso] Mesh ${enabled ? 'enabled' : 'disabled'}');
    if (enabled) {
      _runtime.start(
        selfId: _deviceId,
        internetAvailable: _internetEnabled,
        onPeerSeen: (peer) {
          if (!_meshEnabled) {
            return;
          }
          _logs.add('[$_nowIso] Presence ping from ${peer.alias} (${peer.latencyMs}ms)');
          _safeNotify();
        },
        onRelayTick: (_) {
          if (!_meshEnabled) {
            return;
          }
          _relayMessagesToPeers(maxMessagesPerPeer: 1);
          _safeNotify();
        },
        onGatewaySync: (knownDeviceIds) async {
          if (!_meshEnabled) {
            return;
          }
          final published = await _gateway.publishPresence(
            internetAvailable: _internetEnabled,
            deviceIds: knownDeviceIds,
          );
          if (published > 0) {
            _presenceSyncCount += published;
            _logs.add('[$_nowIso] Gateway synced $published presence IDs');
            _safeNotify();
          }
        },
      );
    } else {
      _runtime.stop();
    }
    _safeNotify();
  }

  Future<void> setInternetEnabled(bool enabled) async {
    _internetEnabled = enabled;
    _logs.add('[$_nowIso] Internet ${enabled ? 'enabled' : 'disabled'}');
    _runtime.setInternetAvailable(enabled, (knownDeviceIds) async {
      final published = await _gateway.publishPresence(
        internetAvailable: _internetEnabled,
        deviceIds: knownDeviceIds,
      );
      if (published > 0) {
        _presenceSyncCount += published;
      }
    });
    if (enabled && _meshEnabled) {
      await publishPending();
    }
    _safeNotify();
  }

  Future<void> publishPending() async {
    final published = await _gateway.publishPending(
      internetAvailable: _internetEnabled,
      onPublished: (messageId) {
        _logs.add('[$_nowIso] Published $messageId to HTTP gateway');
      },
    );
    if (published == 0 && _internetEnabled && _meshEnabled) {
      _logs.add('[$_nowIso] No pending messages to publish');
    }
    _safeNotify();
  }

  void _relayMessagesToPeers({required int maxMessagesPerPeer}) {
    final currentPeers = peers;
    if (currentPeers.isEmpty) {
      return;
    }
    for (final peer in currentPeers) {
      var relayedForPeer = 0;
      final pending = _repository.pending();
      for (final message in pending) {
        if (relayedForPeer >= maxMessagesPerPeer) {
          break;
        }
        if (message.deliveredTo.contains(peer.id)) {
          continue;
        }
        if (message.hops >= 6) {
          continue;
        }
        final hopped = message.hopTo(peer.id);
        _repository.update(hopped);
        relayedForPeer++;
        _logs.add(
          '[$_nowIso] Relayed ${message.id.substring(0, 6)} to ${peer.alias} (hop ${hopped.hops})',
        );
      }
    }
    if (_internetEnabled) {
      publishPending();
    }
  }

  @override
  void dispose() {
    _disposed = true;
    _runtime.stop();
    super.dispose();
  }

  void _safeNotify() {
    if (_disposed) {
      return;
    }
    notifyListeners();
  }
}
