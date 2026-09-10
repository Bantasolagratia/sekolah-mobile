import 'package:flutter_test/flutter_test.dart';
import 'package:sekolah_mobile_app/config/api_config.dart';
import 'package:sekolah_mobile_app/models/guru_model.dart';
import 'package:sekolah_mobile_app/models/session_model.dart';
import 'package:sekolah_mobile_app/models/user_profile_model.dart';

void main() {
  group('ApiConfig Tests', () {
    test('Default host and uri builders', () {
      expect(ApiConfig.authPort, 8000);
      expect(ApiConfig.apiPort, 8080);
      expect(ApiConfig.tokenUri.toString(), contains(':8000/token?grant_type=password'));
      expect(ApiConfig.profileUri.toString(), contains(':8080/auth-flow/profile'));
      expect(ApiConfig.guruUri.toString(), contains(':8080/management/guru'));
    });
  });

  group('SessionModel Tests', () {
    test('SessionModel serialization and deserialization', () {
      final json = {
        'access_token': 'test_token_123',
        'token_type': 'bearer',
        'expires_in': 3600,
        'refresh_token': 'test_refresh_456',
        'user': {
          'id': 'user_uuid_1',
          'email': 'wrenley@murid.sekolah.com',
        },
      };

      final session = SessionModel.fromJson(json);
      expect(session.accessToken, 'test_token_123');
      expect(session.tokenType, 'bearer');
      expect(session.expiresIn, 3600);
      expect(session.refreshToken, 'test_refresh_456');
      expect(session.userId, 'user_uuid_1');
      expect(session.email, 'wrenley@murid.sekolah.com');

      final backToJson = session.toJson();
      expect(backToJson['access_token'], 'test_token_123');
      expect(backToJson['refresh_token'], 'test_refresh_456');
    });
  });

  group('UserProfileModel Tests', () {
    test('UserProfileModel parses murid role and identities properly', () {
      final json = {
        'idUser': 'uuid-murid-123',
        'email': 'wrenley@murid.sekolah.com',
        'isAdmin': false,
        'isTeacher': false,
        'isStudent': true,
        'isGuardian': false,
        'roles': ['MURID'],
        'identities': [
          {
            'id': '202610012',
            'name': 'Wrenley Roth',
            'detail': 'Kelas 10-C',
            'role': 'MURID',
            'isStudent': true,
          }
        ],
      };

      final profile = UserProfileModel.fromJson(json);
      expect(profile.isRoleMurid, true);
      expect(profile.displayName, 'Wrenley Roth');
      expect(profile.displayDetail, 'Kelas 10-C');
      expect(profile.nis, '202610012');
      expect(profile.identities.length, 1);
      expect(profile.identities.first.isStudent, true);
    });
  });

  group('GuruModel Tests', () {
    test('GuruModel properties, initials, and formatting', () {
      final json = {
        'nip': '198501012010011004',
        'nama': 'Jasper Novak',
        'jabatan': 'Guru Fisika',
        'telp': '081210000004',
        'wa': '081210000004',
      };

      final guru = GuruModel.fromJson(json);
      expect(guru.nip, '198501012010011004');
      expect(guru.nama, 'Jasper Novak');
      expect(guru.initials, 'JN');
      expect(guru.displayJabatan, 'Guru Fisika');
      expect(guru.telp, '081210000004');
      expect(guru.wa, '081210000004');
    });
  });
}

