import 'dart:convert';

import 'package:crypto/crypto.dart';

class SignatureUtil {
  static String sign({required String payload, required String privateKey}) {
    final bytes = utf8.encode('$payload::$privateKey');
    return sha256.convert(bytes).toString();
  }

  static bool verify({
    required String payload,
    required String signature,
    required String publicKey,
  }) {
    final expected = sign(payload: payload, privateKey: publicKey);
    return expected == signature;
  }
}
