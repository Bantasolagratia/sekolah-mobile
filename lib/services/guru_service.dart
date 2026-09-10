import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/guru_model.dart';

class GuruService {
  static final GuruService _instance = GuruService._internal();
  factory GuruService() => _instance;
  GuruService._internal();

  Future<List<GuruModel>> getDaftarGuru(String token) async {
    final response = await http.get(
      ApiConfig.guruUri,
      headers: {
        HttpHeaders.authorizationHeader: 'Bearer $token',
        HttpHeaders.acceptHeader: 'application/json',
      },
    ).timeout(const Duration(seconds: 15));

    if (response.statusCode != 200) {
      String message = 'Gagal mengambil data guru (${response.statusCode})';
      try {
        final errorBody = jsonDecode(response.body);
        if (errorBody is Map<String, dynamic> && errorBody['message'] != null) {
          message = errorBody['message'].toString();
        }
      } catch (_) {}
      throw HttpException(message);
    }

    final List<dynamic> data = jsonDecode(response.body) as List<dynamic>;
    return data
        .map((item) => GuruModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}

