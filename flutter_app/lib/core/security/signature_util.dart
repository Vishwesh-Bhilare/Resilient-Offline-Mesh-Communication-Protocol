import 'dart:convert';

import 'package:crypto/crypto.dart';

class SignatureUtil {
  static String sha256Hex(String value) => sha256.convert(utf8.encode(value)).toString();

  static String sign({required String id, required String content, required String privateKey}) {
    return sha256Hex('$id|$content|$privateKey');
  }

  static bool verify({
    required String id,
    required String content,
    required String signature,
    required String privateKey,
  }) {
    return signature == sign(id: id, content: content, privateKey: privateKey);
  }
}
