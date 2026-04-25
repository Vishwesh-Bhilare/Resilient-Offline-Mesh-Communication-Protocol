import 'dart:async';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:permission_handler/permission_handler.dart';

import '../data/models/peer.dart';

class MeshRuntimeController {
  MeshRuntimeController();

  final Random _random = Random();
  final Map<String, Peer> _nearbyPeers = {};
  final Set<String> _knownDeviceIds = {};

  Timer? _presenceTimer;
  Timer? _relayTimer;
  StreamSubscription<List<ScanResult>>? _scanResultsSub;
  StreamSubscription<BluetoothAdapterState>? _adapterStateSub;

  bool _running = false;
  bool _internetAvailable = false;
  String _selfId = '';

  bool get isRunning => _running;
  int get connectedPeerCount => _nearbyPeers.length;
  Set<String> get knownDeviceIds => Set.unmodifiable(_knownDeviceIds);
  List<Peer> get peers => _nearbyPeers.values.toList(growable: false)
    ..sort((a, b) => b.lastSeen.compareTo(a.lastSeen));

  Future<bool> start({
    required String selfId,
    required bool internetAvailable,
    required void Function(Peer peer) onPeerSeen,
    required void Function(String peerId) onRelayTick,
    required void Function(Set<String> deviceIds) onGatewaySync,
    required void Function(String message) onLog,
  }) async {
    if (_running) return true;

    if (kIsWeb) {
      onLog('Web does not support required BLE mesh runtime');
      return false;
    }

    final granted = await _requestBlePermissions(onLog: onLog);
    if (!granted) {
      onLog('BLE permissions were denied. Mesh cannot start.');
      return false;
    }

    _running = true;
    _selfId = selfId;
    _internetAvailable = internetAvailable;
    _knownDeviceIds.add(selfId);

    _adapterStateSub = FlutterBluePlus.adapterState.listen((state) {
      onLog('Bluetooth adapter state: $state');
    });

    _scanResultsSub = FlutterBluePlus.scanResults.listen((results) {
      for (final result in results) {
        final peerId = result.device.remoteId.str;
        if (peerId.isEmpty || peerId == _selfId) {
          continue;
        }

        final alias = result.advertisementData.advName.isNotEmpty
            ? result.advertisementData.advName
            : 'peer-${peerId.replaceAll(':', '').substring(0, min(6, peerId.length))}';

        final peer = Peer(
          id: peerId,
          alias: alias,
          lastSeen: DateTime.now(),
          latencyMs: max(0, 100 + (result.rssi * -1)),
        );

        _nearbyPeers[peerId] = peer;
        _knownDeviceIds.add(peerId);
        onPeerSeen(peer);
      }
    });

    final adapterState = await FlutterBluePlus.adapterState.first;
    if (adapterState != BluetoothAdapterState.on) {
      onLog('Bluetooth is OFF. Turn it on and tap mesh button again.');
      await stop();
      return false;
    }

    await FlutterBluePlus.startScan(
      timeout: const Duration(days: 1),
      androidUsesFineLocation: true,
      continuousUpdates: true,
    );
    onLog('Started hardware BLE scanning');

    _presenceTimer = Timer.periodic(const Duration(seconds: 5), (_) {
      _evictStalePeers();
      if (_internetAvailable) {
        onGatewaySync(knownDeviceIds);
      }
    });

    _relayTimer = Timer.periodic(const Duration(seconds: 3), (_) {
      if (_nearbyPeers.isEmpty) return;

      final shuffledPeers = _nearbyPeers.keys.toList()..shuffle(_random);
      final relayCount = min(2, shuffledPeers.length);

      for (var i = 0; i < relayCount; i++) {
        onRelayTick(shuffledPeers[i]);
      }
    });

    return true;
  }

  Future<void> stop() async {
    _running = false;
    _presenceTimer?.cancel();
    _relayTimer?.cancel();
    _presenceTimer = null;
    _relayTimer = null;

    await _scanResultsSub?.cancel();
    await _adapterStateSub?.cancel();
    _scanResultsSub = null;
    _adapterStateSub = null;

    if (await FlutterBluePlus.isScanning.first) {
      await FlutterBluePlus.stopScan();
    }
  }

  void setInternetAvailable(
    bool available,
    void Function(Set<String>) onGatewaySync,
  ) {
    _internetAvailable = available;

    if (_internetAvailable && _running) {
      onGatewaySync(knownDeviceIds);
    }
  }

  Future<bool> _requestBlePermissions({
    required void Function(String message) onLog,
  }) async {
    final statuses = await [
      Permission.bluetoothScan,
      Permission.bluetoothConnect,
      Permission.bluetoothAdvertise,
      Permission.locationWhenInUse,
    ].request();

    final denied = statuses.entries.where((entry) => !entry.value.isGranted).toList();
    if (denied.isNotEmpty) {
      onLog('Missing required permissions: ${denied.map((e) => e.key).join(', ')}');
      return false;
    }

    return true;
  }

  void _evictStalePeers() {
    final now = DateTime.now();

    _nearbyPeers.removeWhere(
      (_, peer) => now.difference(peer.lastSeen).inSeconds > 20,
    );
  }
}
