import 'dart:convert';

import 'package:crypto/crypto.dart';

class SignatureUtil {
  static String sign({required String payload, required String privateKey}) {
    final bytes = utf8.encode('$payload::$privateKey');
    return sha256.convert(bytes).toString();
  }

  /// Verifies a signature using the shared symmetric key (= sender's deviceId).
  /// Call with [sharedKey] = the sender's raw deviceId (NOT their sha256 public key).
  static bool verify({
    required String payload,
    required String signature,
    required String sharedKey,
  }) {
    return sign(payload: payload, privateKey: sharedKey) == signature;
  }
}
