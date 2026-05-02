import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class ApiException implements Exception {
  final String message;
  ApiException(this.message);
  @override
  String toString() => message;
}

class ApiService {
  // 10.0.2.2 is the Android emulator's alias for the host machine's localhost
  static const String _base = 'http://10.0.2.2:6767/api';
  static String? _token;

  static Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
  }

  static Future<void> saveToken(String token) async {
    _token = token;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
  }

  static Future<void> clearToken() async {
    _token = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
  }

  static bool get hasToken => _token != null;

  static Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (_token != null) 'Authorization': 'Bearer $_token',
      };

  static Future<dynamic> get(String path, {Map<String, String>? params}) async {
    final uri = Uri.parse('$_base$path').replace(queryParameters: params);
    final res = await http.get(uri, headers: _headers);
    return _handle(res);
  }

  static Future<dynamic> post(String path, Map<String, dynamic> body) async {
    final res = await http.post(
      Uri.parse('$_base$path'),
      headers: _headers,
      body: jsonEncode(body),
    );
    return _handle(res);
  }

  static Future<dynamic> put(String path, Map<String, dynamic> body) async {
    final res = await http.put(
      Uri.parse('$_base$path'),
      headers: _headers,
      body: jsonEncode(body),
    );
    return _handle(res);
  }

  static dynamic _handle(http.Response res) {
    if (res.statusCode == 204) return null;
    if (res.statusCode == 401) throw ApiException('غير مصرح — يرجى تسجيل الدخول مجدداً');
    if (res.statusCode < 200 || res.statusCode >= 300) {
      final msg = res.body.isNotEmpty ? res.body : 'خطأ في الطلب (${res.statusCode})';
      throw ApiException(msg);
    }
    if (res.body.isEmpty) return null;
    return jsonDecode(utf8.decode(res.bodyBytes));
  }
}
