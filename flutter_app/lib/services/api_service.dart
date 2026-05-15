import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_info.dart';

class ApiException implements Exception {
  final String message;
  final int statusCode;
  ApiException(this.message, {this.statusCode = 0});
  bool get isUnauthorized => statusCode == 401;
  @override
  String toString() => message;
}

class ApiService {
  // 10.0.2.2 is the Android emulator's alias for the host machine's localhost
  static const String _base = 'http://10.0.2.2:6767/api';
  static String? _token;
  static UserInfo? _currentUser;

  static UserInfo? get currentUser => _currentUser;

  static Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    final userJson = prefs.getString('userInfo');
    if (userJson != null) {
      _currentUser = UserInfo.fromJson(jsonDecode(userJson) as Map<String, dynamic>);
    }
  }

  static Future<void> saveToken(String token) async {
    _token = token;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
  }

  static Future<void> saveUserInfo(UserInfo info) async {
    _currentUser = info;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('userInfo', jsonEncode(info.toJson()));
  }

  static Future<void> clearToken() async {
    _token = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    await clearUserInfo();
  }

  static Future<void> clearUserInfo() async {
    _currentUser = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('userInfo');
  }

  static bool get hasToken => _token != null;

  static Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'Accept-Language': 'ar',
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
    if (res.statusCode < 200 || res.statusCode >= 300) {
      throw ApiException(_extractMessage(res), statusCode: res.statusCode);
    }
    if (res.body.isEmpty) return null;
    return jsonDecode(utf8.decode(res.bodyBytes));
  }

  static String _extractMessage(http.Response res) {
    if (res.body.isEmpty) return '';
    try {
      final json = jsonDecode(utf8.decode(res.bodyBytes));
      if (json is Map<String, dynamic>) {
        return json['message'] as String? ??
               json['error'] as String? ??
               res.body;
      }
    } catch (_) {}
    return res.body;
  }
}
