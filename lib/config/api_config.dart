import 'dart:io' show Platform;
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ApiConfig {
  static const String _keyCustomHost = 'custom_server_host';

  static String get defaultHost {
    if (kIsWeb) return 'localhost';
    try {
      if (Platform.isAndroid) {
        return '10.0.2.2';
      }
    } catch (_) {}
    return 'localhost';
  }

  static String _host = defaultHost;
  static int authPort = 8000;
  static int apiPort = 8080;

  static String get host => _host;

  static Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    final savedHost = prefs.getString(_keyCustomHost);
    if (savedHost != null && savedHost.trim().isNotEmpty) {
      _host = savedHost.trim();
    } else {
      _host = defaultHost;
    }
  }

  static Future<void> setHost(String newHost) async {
    _host = newHost.trim().isEmpty ? defaultHost : newHost.trim();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyCustomHost, _host);
  }

  static String get baseUrlAuth => 'http://$_host:$authPort';
  static String get baseUrlApi => 'http://$_host:$apiPort';

  // Endpoints
  static Uri get tokenUri => Uri.parse('$baseUrlAuth/token?grant_type=password');
  static Uri get profileUri => Uri.parse('$baseUrlApi/auth-flow/profile');
  static Uri get guruUri => Uri.parse('$baseUrlApi/management/guru');
}

