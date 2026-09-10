import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../config/api_config.dart';
import '../models/session_model.dart';
import '../models/user_profile_model.dart';

class AuthService extends ChangeNotifier {
  static final AuthService _instance = AuthService._internal();
  factory AuthService() => _instance;
  AuthService._internal();

  static const String _keyToken = 'auth_access_token';
  static const String _keyRefreshToken = 'auth_refresh_token';
  static const String _keyProfileJson = 'auth_profile_json';

  SessionModel? _session;
  UserProfileModel? _profile;
  bool _isLoading = false;

  SessionModel? get session => _session;
  UserProfileModel? get profile => _profile;
  bool get isAuthenticated => _session != null && _session!.accessToken.isNotEmpty;
  bool get isLoading => _isLoading;

  Future<bool> tryAutoLogin() async {
    _isLoading = true;
    notifyListeners();

    try {
      await ApiConfig.init();
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString(_keyToken);

      if (token == null || token.isEmpty) {
        _isLoading = false;
        notifyListeners();
        return false;
      }

      final refreshToken = prefs.getString(_keyRefreshToken);
      _session = SessionModel(
        accessToken: token,
        refreshToken: refreshToken,
      );

      final cachedProfileStr = prefs.getString(_keyProfileJson);
      if (cachedProfileStr != null) {
        try {
          final profileMap = jsonDecode(cachedProfileStr) as Map<String, dynamic>;
          _profile = UserProfileModel.fromJson(profileMap);
        } catch (_) {}
      }

      // Refresh profile from server
      try {
        final freshProfile = await fetchProfile(token);
        if (freshProfile != null) {
          _profile = freshProfile;
          await prefs.setString(_keyProfileJson, jsonEncode(freshProfile.toJson()));
        }
      } catch (_) {
        // If offline, use cached profile
      }

      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> signIn(String email, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.post(
        ApiConfig.tokenUri,
        headers: {
          HttpHeaders.contentTypeHeader: 'application/json',
          HttpHeaders.acceptHeader: 'application/json',
        },
        body: jsonEncode({
          'email': email.trim(),
          'password': password,
        }),
      ).timeout(const Duration(seconds: 15));

      if (response.statusCode != 200) {
        String message = 'Email atau password tidak valid.';
        try {
          final errorBody = jsonDecode(response.body);
          if (errorBody is Map<String, dynamic>) {
            message = errorBody['error_description']?.toString() ??
                errorBody['msg']?.toString() ??
                errorBody['message']?.toString() ??
                message;
          }
        } catch (_) {}
        throw HttpException(message);
      }

      final sessionData = jsonDecode(response.body) as Map<String, dynamic>;
      final newSession = SessionModel.fromJson(sessionData);
      _session = newSession;

      // Save token
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_keyToken, newSession.accessToken);
      if (newSession.refreshToken != null) {
        await prefs.setString(_keyRefreshToken, newSession.refreshToken!);
      }

      // Fetch profile from backend
      final userProfile = await fetchProfile(newSession.accessToken);
      _profile = userProfile;
      if (userProfile != null) {
        await prefs.setString(_keyProfileJson, jsonEncode(userProfile.toJson()));
      }

      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      rethrow;
    }
  }

  Future<UserProfileModel?> fetchProfile(String token) async {
    final response = await http.get(
      ApiConfig.profileUri,
      headers: {
        HttpHeaders.authorizationHeader: 'Bearer $token',
        HttpHeaders.acceptHeader: 'application/json',
      },
    ).timeout(const Duration(seconds: 15));

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      return UserProfileModel.fromJson(data);
    }
    return null;
  }

  Future<void> signOut() async {
    _session = null;
    _profile = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyToken);
    await prefs.remove(_keyRefreshToken);
    await prefs.remove(_keyProfileJson);
    notifyListeners();
  }
}

