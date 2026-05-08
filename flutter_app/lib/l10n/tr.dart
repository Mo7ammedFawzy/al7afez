import 'dart:convert';
import 'package:flutter/services.dart';

class Tr {
  static Map<String, String> _strings = {};

  static Future<void> load(String locale) async {
    final jsonStr = await rootBundle.loadString('assets/l10n/$locale.json');
    _strings = (jsonDecode(jsonStr) as Map<String, dynamic>).cast<String, String>();
  }

  // Returns the translation for [key], substituting any {placeholder} tokens
  // from [params]. Falls back to the key itself if not found.
  static String translate(String key, [Map<String, String>? params]) {
    String value = _strings[key] ?? key;
    if (params != null) {
      params.forEach((k, v) => value = value.replaceAll('{$k}', v));
    }
    return value;
  }
}
